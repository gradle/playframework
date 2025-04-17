package org.gradle.playframework.plugins.internal;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.playframework.plugins.JavaScriptSourceDirectorySet;

import javax.inject.Inject;

public class DefaultJavaScriptSourceDirectorySet extends DefaultSourceDirectorySet implements JavaScriptSourceDirectorySet {

    @Inject
    public DefaultJavaScriptSourceDirectorySet(SourceDirectorySet sourceDirectorySet) {
        super(sourceDirectorySet);
    }


    @Inject
    public DefaultJavaScriptSourceDirectorySet(SourceDirectorySet sourceDirectorySet, TaskDependencyFactory taskDependencyFactory) {
        super(sourceDirectorySet, taskDependencyFactory);
    }
}
