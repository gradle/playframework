package com.lightbend.play.sourcesets;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

public interface CoffeeScriptSourceSet {

    SourceDirectorySet getCoffeeScript();
    CoffeeScriptSourceSet coffeeScript(Action<? super SourceDirectorySet> configureAction);
}
