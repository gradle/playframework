package org.gradle.playframework.sourcesets.internal;

import org.gradle.playframework.sourcesets.RoutesSourceSet;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class DefaultRoutesSourceSet implements RoutesSourceSet {

    private final SourceDirectorySet routes;

    @Inject
    public DefaultRoutesSourceSet(String name, String displayName, ObjectFactory objectFactory) {
        routes = objectFactory.sourceDirectorySet(name, displayName +  " Routes source");
        routes.srcDirs("conf");
        routes.include("routes", "*.routes");
    }

    @Override
    public SourceDirectorySet getRoutes() {
        return routes;
    }

    @Override
    public RoutesSourceSet routes(Action<? super SourceDirectorySet> configureAction) {
        configureAction.execute(getRoutes());
        return this;
    }
}
