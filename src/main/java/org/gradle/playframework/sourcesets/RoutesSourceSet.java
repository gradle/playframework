package org.gradle.playframework.sourcesets;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

/**
 * Represents a source set containing routes files.
 * <p>
 * The following example demonstrate the use of the source set in a build script using the Groovy DSL:
 * <pre>
 * sourceSets {
 *     main {
 *         routes {
 *             srcDir 'extra/more'
 *         }
 *     }
 * }
 * </pre>
 */
public interface RoutesSourceSet {

    /**
     * Returns the source directory set.
     *
     * @return The source directory set
     */
    SourceDirectorySet getRoutes();

    /**
     * Configures the source set.
     *
     * @param configureAction The configuration action
     * @return The source set
     */
    RoutesSourceSet routes(Action<? super SourceDirectorySet> configureAction);
}
