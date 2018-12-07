package com.playframework.gradle.sourcesets;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

public interface JavaScriptSourceSet {

    SourceDirectorySet getJavaScript();
    JavaScriptSourceSet javaScript(Action<? super SourceDirectorySet> configureAction);
}
