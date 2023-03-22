package org.gradle.playframework.util;

import org.gradle.api.Transformer;
import org.gradle.api.specs.Spec;

import javax.annotation.Nullable;
import java.util.*;

public final class CollectionUtils {
    private CollectionUtils() {
        throw new AssertionError("No org.gradle.playframework.util.CollectionUtils instances for you!");
    }

    @Nullable
    public static <T> T findFirst(Iterable<? extends T> source, Spec<? super T> filter) {
        for (T item : source) {
            if (filter.isSatisfiedBy(item)) {
                return item;
            }
        }

        return null;
    }

    public static <R, I> List<R> collect(Iterable<? extends I> source, Transformer<? extends R, ? super I> transformer) {
        if (source instanceof Collection<?>) {
            Collection<? extends I> collection = (Collection<? extends I>) source;
            return collect(source, new ArrayList<R>(collection.size()), transformer);
        } else {
            return collect(source, new LinkedList<R>(), transformer);
        }
    }

    public static <R, I, C extends Collection<R>> C collect(Iterable<? extends I> source, C destination, Transformer<? extends R, ? super I> transformer) {
        for (I item : source) {
            destination.add(transformer.transform(item));
        }
        return destination;
    }

    /**
     * Recursively unpacks all the given things into a flat list.
     *
     * Nulls are not removed, they are left intact.
     *
     * @param things The things to flatten
     * @return A flattened list of the given things
     */
    public static List<?> flattenCollections(Object... things) {
        return flattenCollections(Object.class, things);
    }

    /**
     * Recursively unpacks all the given things into a flat list, ensuring they are of a certain type.
     *
     * Nulls are not removed, they are left intact.
     *
     * If a non null object cannot be cast to the target type, a ClassCastException will be thrown.
     *
     * @param things The things to flatten
     * @param <T> The target type in the flattened list
     * @return A flattened list of the given things
     */
    public static <T> List<T> flattenCollections(Class<T> type, Object... things) {
        if (things == null) {
            return Collections.singletonList(null);
        } else if (things.length == 0) {
            return Collections.emptyList();
        } else if (things.length == 1) {
            Object thing = things[0];

            if (thing == null) {
                return Collections.singletonList(null);
            }

            // Casts to Class below are to workaround Eclipse compiler bug
            // See: https://github.com/gradle/gradle/pull/200

            if (thing.getClass().isArray()) {
                Object[] thingArray = (Object[]) thing;
                List<T> list = new ArrayList<T>(thingArray.length);
                for (Object thingThing : thingArray) {
                    list.addAll(flattenCollections(type, thingThing));
                }
                return list;
            }

            if (thing instanceof Collection) {
                Collection<?> collection = (Collection<?>) thing;
                List<T> list = new ArrayList<T>();
                for (Object element : collection) {
                    list.addAll(flattenCollections(type, element));
                }
                return list;
            }

            return Collections.singletonList(type.cast(thing));
        } else {
            List<T> list = new ArrayList<T>();
            for (Object thing : things) {
                list.addAll(flattenCollections(type, thing));
            }
            return list;
        }
    }

}
