package com.playframework.gradle.sourcesets;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

public interface RoutesSourceSet {

    SourceDirectorySet getRoutes();
    RoutesSourceSet routes(Action<? super SourceDirectorySet> configureAction);
}
