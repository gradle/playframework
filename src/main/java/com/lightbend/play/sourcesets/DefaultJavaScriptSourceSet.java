package com.lightbend.play.sourcesets;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

import static org.gradle.util.ConfigureUtil.configure;

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
    public JavaScriptSourceSet javaScript(Closure configureClosure) {
        configure(configureClosure, getJavaScript());
        return this;
    }

    @Override
    public JavaScriptSourceSet javaScript(Action<? super SourceDirectorySet> configureAction) {
        configureAction.execute(getJavaScript());
        return this;
    }
}
