package org.gradle.playframework.tools.internal.run;

import org.gradle.api.tasks.compile.BaseForkOptions;

import java.io.File;

public interface PlayRunSpec {

    BaseForkOptions getForkOptions();

    Iterable<File> getClasspath();

    Iterable<File> getChangingClasspath();

    File getApplicationJar();

    File getAssetsJar();

    Iterable<File> getAssetsDirs();

    File getProjectPath();

    int getHttpPort();

}
