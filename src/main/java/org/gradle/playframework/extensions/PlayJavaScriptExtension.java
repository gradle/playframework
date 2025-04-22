package org.gradle.playframework.extensions;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

public interface PlayJavaScriptExtension{
    /**
     * Configures the JavaScript source set.
     *
     * @param configureAction The configuration action
     * @return The JavaScript source set
     */
    PlayJavaScriptExtension javaScript(Action<? super SourceDirectorySet> configureAction);
}
