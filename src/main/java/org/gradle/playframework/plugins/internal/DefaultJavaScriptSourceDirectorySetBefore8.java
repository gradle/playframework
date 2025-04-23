package org.gradle.playframework.plugins.internal;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.playframework.plugins.JavaScriptSourceDirectorySet;

import javax.inject.Inject;

public class DefaultJavaScriptSourceDirectorySetBefore8 extends DefaultSourceDirectorySet implements JavaScriptSourceDirectorySet {
    @Inject
    public DefaultJavaScriptSourceDirectorySetBefore8(SourceDirectorySet sourceDirectorySet) {
        super(sourceDirectorySet);
        srcDirs("app/assets");
        include("**/*.js");
    }
}
