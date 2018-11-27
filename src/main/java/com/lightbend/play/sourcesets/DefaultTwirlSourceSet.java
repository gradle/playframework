package com.lightbend.play.sourcesets;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

import static org.gradle.util.ConfigureUtil.configure;

public class DefaultTwirlSourceSet implements TwirlSourceSet {

    private final SourceDirectorySet twirl;

    @Inject
    public DefaultTwirlSourceSet(String name, String displayName, ObjectFactory objectFactory) {
        twirl = objectFactory.sourceDirectorySet(name, displayName +  " Twirl source");
        twirl.srcDirs("app");
        twirl.include("**/*.scala.*");
    }

    @Override
    public SourceDirectorySet getTwirl() {
        return twirl;
    }

    @Override
    public TwirlSourceSet twirl(Closure configureClosure) {
        configure(configureClosure, getTwirl());
        return this;
    }

    @Override
    public TwirlSourceSet twirl(Action<? super SourceDirectorySet> configureAction) {
        configureAction.execute(getTwirl());
        return this;
    }
}
