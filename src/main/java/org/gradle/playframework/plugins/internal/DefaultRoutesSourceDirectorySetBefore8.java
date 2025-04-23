package org.gradle.playframework.plugins.internal;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.playframework.plugins.RoutesSourceDirectorySet;

import javax.inject.Inject;

public class DefaultRoutesSourceDirectorySetBefore8 extends DefaultSourceDirectorySet implements RoutesSourceDirectorySet {
    @Inject
    public DefaultRoutesSourceDirectorySetBefore8(SourceDirectorySet sourceSet) {
        super(sourceSet);
        srcDirs("conf");
        include("routes", "*.routes");
    }
}
