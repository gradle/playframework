package com.lightbend.play.sourcesets;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

public interface TwirlSourceSet {

    SourceDirectorySet getTwirl();
    TwirlSourceSet twirl(Closure configureClosure);
    TwirlSourceSet twirl(Action<? super SourceDirectorySet> configureAction);
}
