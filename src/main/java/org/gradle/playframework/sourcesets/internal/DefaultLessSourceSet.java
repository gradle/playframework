package org.gradle.playframework.sourcesets.internal;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.playframework.sourcesets.LessSourceSet;

import javax.inject.Inject;

public class DefaultLessSourceSet implements LessSourceSet {

    private final SourceDirectorySet less;

    @Inject
    public DefaultLessSourceSet(String name, String displayName, ObjectFactory objectFactory) {
        less = objectFactory.sourceDirectorySet(name, displayName +  " LESS source");
        less.srcDirs("app/assets");
        less.include("**/*.less");
        less.exclude("**/_*.less");
    }

    @Override
    public SourceDirectorySet getLess() {
        return less;
    }

    @Override
    public LessSourceSet less(Action<? super SourceDirectorySet> configureAction) {
        configureAction.execute(getLess());
        return this;
    }
}
