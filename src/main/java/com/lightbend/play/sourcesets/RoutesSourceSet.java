package com.lightbend.play.sourcesets;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

public interface RoutesSourceSet {

    SourceDirectorySet getRoutes();
    RoutesSourceSet routes(Closure configureClosure);
    RoutesSourceSet routes(Action<? super SourceDirectorySet> configureAction);
}
