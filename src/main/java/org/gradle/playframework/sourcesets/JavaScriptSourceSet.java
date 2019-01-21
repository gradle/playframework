package org.gradle.playframework.sourcesets;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

/**
 * Represents a source set containing JavaScript sources.
 * <p>
 * The following example demonstrate the use of the source set in a build script using the Groovy DSL:
 * <pre>
 * sourceSets {
 *     main {
 *         javaScript {
 *             srcDir 'additional/javascript'
 *             exclude '{@literal **}/old_*.js'
 *         }
 *     }
 * }
 * </pre>
 */
public interface JavaScriptSourceSet {

    /**
     * Returns the source directory set.
     *
     * @return The source directory set
     */
    SourceDirectorySet getJavaScript();

    /**
     * Configures the source set.
     *
     * @param configureAction The configuration action
     * @return The source set
     */
    JavaScriptSourceSet javaScript(Action<? super SourceDirectorySet> configureAction);
}
