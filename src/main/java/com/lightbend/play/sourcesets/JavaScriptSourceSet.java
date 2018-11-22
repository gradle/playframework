package com.lightbend.play.sourcesets;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

public interface JavaScriptSourceSet {

    SourceDirectorySet getJavaScript();
    JavaScriptSourceSet javaScript(Closure configureClosure);
    JavaScriptSourceSet javaScript(Action<? super SourceDirectorySet> configureAction);
}
