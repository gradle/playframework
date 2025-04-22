package org.gradle.playframework.plugins.internal;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.playframework.plugins.JavaScriptSourceDirectorySet;
import org.gradle.playframework.sourcesets.JavaScriptSourceSet;

import javax.inject.Inject;

public class DefaultJavaScriptSourceDirectorySet extends DefaultSourceDirectorySet implements JavaScriptSourceDirectorySet {
    private final SourceDirectorySet sourceDirSet;

//    @Inject
//    public DefaultJavaScriptSourceDirectorySet(SourceDirectorySet sourceDirectorySet) {
//        super(sourceDirectorySet);
//    }


    @Inject
    public DefaultJavaScriptSourceDirectorySet(SourceDirectorySet sourceDirectorySet, TaskDependencyFactory taskDependencyFactory) {
        super(sourceDirectorySet, taskDependencyFactory);
        this.sourceDirSet = sourceDirectorySet;
    }

//    @Override
//    public JavaScriptSourceDirectorySet javaScript(Action<? super SourceDirectorySet> configureAction) {
//            configureAction.execute(sourceDirSet);
//            return this;
//        }
}
