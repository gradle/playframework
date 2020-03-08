package org.gradle.playframework.sourcesets;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

/**
 * Represents a source set containing LESS sources.
 * <p>
 * The following example demonstrate the use of the source set in a build script using the Groovy DSL:
 * <pre>
 * sourceSets {
 *     main {
 *         less {
 *             srcDir 'app/assets'
 *             include '{@literal **}/*.less'
 *             exclude '{@literal **}/_*.less'
 *         }
 *     }
 * }
 * </pre>
 */
public interface LessSourceSet {

    /**
     * Returns the source directory set.
     *
     * @return The source directory set
     */
    SourceDirectorySet getLess();

    /**
     * Configures the source set.
     *
     * @param configureAction The configuration action
     * @return The source set
     */
    LessSourceSet less(Action<? super SourceDirectorySet> configureAction);
}
