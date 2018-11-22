package com.lightbend.play.sourcesets;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

public interface CoffeeScriptSourceSet {

    SourceDirectorySet getCoffeeScript();
    CoffeeScriptSourceSet coffeeScript(Closure configureClosure);
    CoffeeScriptSourceSet coffeeScript(Action<? super SourceDirectorySet> configureAction);
}
