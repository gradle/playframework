package org.gradle.playframework.plugins.internal;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.playframework.plugins.JavaScriptSourceDirectorySet;

import javax.inject.Inject;

public class DefaultPlaySourceDirectorySetBefore8 extends DefaultSourceDirectorySet {
    @Inject
    public DefaultPlaySourceDirectorySetBefore8(SourceDirectorySet sourceDirectorySet) {
        super(sourceDirectorySet);
    }
}
