package org.gradle.playframework.plugins.internal;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.playframework.plugins.JavaScriptSourceDirectorySet;

import javax.inject.Inject;

public class DefaultPlaySourceDirectorySet8 extends DefaultSourceDirectorySet {
    @Inject
    public DefaultPlaySourceDirectorySet8(SourceDirectorySet sourceDirectorySet, TaskDependencyFactory taskDependencyFactory) {
        super(sourceDirectorySet, taskDependencyFactory);
    }
}
