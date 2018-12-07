package com.playframework.gradle.sourcesets.internal;

import com.playframework.gradle.sourcesets.CoffeeScriptSourceSet;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class DefaultCoffeeScriptSourceSet implements CoffeeScriptSourceSet {

    private final SourceDirectorySet coffeeScript;

    @Inject
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
    public CoffeeScriptSourceSet coffeeScript(Action<? super SourceDirectorySet> configureAction) {
        configureAction.execute(getCoffeeScript());
        return this;
    }
}
