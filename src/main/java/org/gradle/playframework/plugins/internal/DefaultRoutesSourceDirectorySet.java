package org.gradle.playframework.plugins.internal;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.playframework.plugins.RoutesSourceDirectorySet;

import javax.inject.Inject;

public class DefaultRoutesSourceDirectorySet extends DefaultSourceDirectorySet implements RoutesSourceDirectorySet {
    @Inject
    public DefaultRoutesSourceDirectorySet(SourceDirectorySet sourceSet, TaskDependencyFactory taskDependencyFactory) {
        super(sourceSet, taskDependencyFactory);
        srcDirs("conf");
        include("routes", "*.routes");
    }
}
