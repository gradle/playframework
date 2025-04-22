package org.gradle.playframework.extensions.internal;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.playframework.extensions.PlayJavaScriptExtension;
import org.gradle.playframework.sourcesets.JavaScriptSourceSet;
import org.gradle.playframework.sourcesets.internal.DefaultJavaScriptSourceSet;

public class DefaultPlayJavaScriptExtension implements PlayJavaScriptExtension {
    private final SourceDirectorySet javaScriptSourceSet;

    public DefaultPlayJavaScriptExtension(SourceDirectorySet javaScriptSourceSet) {
        this.javaScriptSourceSet = javaScriptSourceSet;
    }

    @Override
    public PlayJavaScriptExtension javaScript(Action<? super SourceDirectorySet> configureAction) {
        System.err.println("javaScriptSourceSet: " + javaScriptSourceSet.getClass().getName());
        configureAction.execute(javaScriptSourceSet);
        return this;
    }

}
