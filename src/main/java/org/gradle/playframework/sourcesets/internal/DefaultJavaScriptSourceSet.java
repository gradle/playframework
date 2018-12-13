package org.gradle.playframework.sourcesets.internal;

import org.gradle.playframework.sourcesets.JavaScriptSourceSet;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class DefaultJavaScriptSourceSet implements JavaScriptSourceSet {

    private final SourceDirectorySet javaScript;

    @Inject
    public DefaultJavaScriptSourceSet(String name, String displayName, ObjectFactory objectFactory) {
        javaScript = objectFactory.sourceDirectorySet(name, displayName +  " JavaScript source");
        javaScript.srcDirs("app/assets");
        javaScript.include("**/*.js");
    }

    @Override
    public SourceDirectorySet getJavaScript() {
        return javaScript;
    }

    @Override
    public JavaScriptSourceSet javaScript(Action<? super SourceDirectorySet> configureAction) {
        configureAction.execute(getJavaScript());
        return this;
    }
}
