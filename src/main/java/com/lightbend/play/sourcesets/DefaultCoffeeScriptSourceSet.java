package com.lightbend.play.sourcesets;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;

import static org.gradle.util.ConfigureUtil.configure;

public class DefaultCoffeeScriptSourceSet implements CoffeeScriptSourceSet {

    private final SourceDirectorySet coffeeScript;

    public DefaultCoffeeScriptSourceSet(String name, String displayName, ObjectFactory objectFactory) {
        coffeeScript = objectFactory.sourceDirectorySet(name, displayName +  " CoffeeScript source");
        coffeeScript.srcDirs("app/assets");
        coffeeScript.include("**/*.coffee");
    }

    @Override
    public SourceDirectorySet getCoffeeScript() {
        return coffeeScript;
    }

    @Override
    public CoffeeScriptSourceSet coffeeScript(Closure configureClosure) {
        configure(configureClosure, getCoffeeScript());
        return this;
    }

    @Override
    public CoffeeScriptSourceSet coffeeScript(Action<? super SourceDirectorySet> configureAction) {
        configureAction.execute(getCoffeeScript());
        return this;
    }
}
