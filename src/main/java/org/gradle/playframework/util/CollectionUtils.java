package org.gradle.playframework.util;

import org.gradle.api.Transformer;
import org.gradle.api.specs.Spec;
import org.gradle.internal.Pair;

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
     * @param type Class token for the target type {@code <T>} in the flattened list
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

    public static <T> Set<T> toSet(Iterable<? extends T> things) {
        if (things == null) {
            return new HashSet<T>(0);
        }
        if (things instanceof Set) {
            @SuppressWarnings("unchecked") Set<T> castThings = (Set<T>) things;
            return castThings;
        }

        Set<T> set = new LinkedHashSet<T>();
        for (T thing : things) {
            set.add(thing);
        }
        return set;
    }

    /**
     * Given a set of values, derive a set of keys and populate a map.
     *
     * @param destination The map to populate
     * @param items The values to derive keys from
     * @param keyGenerator The function to derive keys from the values
     * @param <K> The type of the keys
     * @param <V> The type of the values
     */
    public static <K, V> void collectMap(Map<K, V> destination, Iterable<? extends V> items, Transformer<? extends K, ? super V> keyGenerator) {
        for (V item : items) {
            destination.put(keyGenerator.transform(item), item);
        }
    }

    /**
     * Given a set of values, derive a set of keys and return a map
     *
     * @param items The values to derive keys from
     * @param keyGenerator The function to derive keys from the values
     * @param <K> The type of the keys
     * @param <V> The type of the values
     * @return The resulting map of keys to values
     */
    public static <K, V> Map<K, V> collectMap(Iterable<? extends V> items, Transformer<? extends K, ? super V> keyGenerator) {
        Map<K, V> map = new LinkedHashMap<K, V>();
        collectMap(map, items, keyGenerator);
        return map;
    }

    /**
     * The result of diffing two sets.
     *
     * @param <T> The type of element the sets contain
     * @see CollectionUtils#diffSetsBy(java.util.Set, java.util.Set, org.gradle.api.Transformer)
     */
    public static class SetDiff<T> {
        public Set<T> leftOnly = new HashSet<T>();
        public Set<Pair<T, T>> common = new HashSet<Pair<T, T>>();
        public Set<T> rightOnly = new HashSet<T>();
    }

    /**
     * Provides a “diff report” of how the two sets are similar and how they are different, comparing the entries by some aspect.
     *
     * The transformer is used to generate the value to use to compare the entries by. That is, the entries are not compared by equals by an attribute or characteristic.
     *
     * The transformer is expected to produce a unique value for each entry in a single set. Behaviour is undefined if this condition is not met.
     *
     * @param left The set on the “left” side of the comparison.
     * @param right The set on the “right” side of the comparison.
     * @param compareBy Provides the value to compare entries from either side by
     * @param <T> The type of the entry objects
     * @return A representation of the difference
     */
    public static <T> SetDiff<T> diffSetsBy(Set<? extends T> left, Set<? extends T> right, Transformer<?, T> compareBy) {
        if (left == null) {
            throw new NullPointerException("'left' set is null");
        }
        if (right == null) {
            throw new NullPointerException("'right' set is null");
        }

        SetDiff<T> setDiff = new SetDiff<T>();

        Map<Object, T> indexedLeft = collectMap(left, compareBy);
        Map<Object, T> indexedRight = collectMap(right, compareBy);

        for (Map.Entry<Object, T> leftEntry : indexedLeft.entrySet()) {
            T rightValue = indexedRight.remove(leftEntry.getKey());
            if (rightValue == null) {
                setDiff.leftOnly.add(leftEntry.getValue());
            } else {
                Pair<T, T> pair = Pair.of(leftEntry.getValue(), rightValue);
                setDiff.common.add(pair);
            }
        }

        for (T rightValue : indexedRight.values()) {
            setDiff.rightOnly.add(rightValue);
        }

        return setDiff;
    }

}
