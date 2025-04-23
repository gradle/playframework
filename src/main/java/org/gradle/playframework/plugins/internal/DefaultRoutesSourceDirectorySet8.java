package org.gradle.playframework.plugins.internal;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.playframework.plugins.RoutesSourceDirectorySet;

import javax.inject.Inject;

public class DefaultRoutesSourceDirectorySet8 extends DefaultSourceDirectorySet implements RoutesSourceDirectorySet {
    @Inject
    public DefaultRoutesSourceDirectorySet8(SourceDirectorySet sourceSet, TaskDependencyFactory taskDependencyFactory) {
        super(sourceSet, taskDependencyFactory);
        srcDirs("conf");
        include("routes", "*.routes");
    }
}
