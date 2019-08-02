package org.gradle.playframework.tools.internal.run;

import org.gradle.api.tasks.compile.BaseForkOptions;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class DefaultPlayRunSpec implements PlayRunSpec, Serializable {
    private final Iterable<File> classpath;
    private final Iterable<File> changingClasspath;
    private final File applicationJar;
    private final File assetsJar;
    private final Iterable<File> assetsDirs;
    private final File projectPath;
    private BaseForkOptions forkOptions;
    private int httpPort;

    public DefaultPlayRunSpec(Iterable<File> classpath, Iterable<File> changingClasspath, File applicationJar, File assetsJar, Iterable<File> assetsDirs, File projectPath, BaseForkOptions forkOptions, int httpPort) {
        this.classpath = toLinkedHashSet(classpath);
        this.changingClasspath = changingClasspath != null ? toLinkedHashSet(changingClasspath) : Collections.emptySet();
        this.applicationJar = applicationJar;
        this.assetsJar = assetsJar;
        this.assetsDirs = toLinkedHashSet(assetsDirs);
        this.projectPath = projectPath;
        this.forkOptions = forkOptions;
        this.httpPort = httpPort;
    }

    private LinkedHashSet<File> toLinkedHashSet(Iterable<File> classpath) {
        List<File> target = new ArrayList<>();
        classpath.forEach(target::add);
        return new LinkedHashSet<>(target);
    }

    @Override
    public BaseForkOptions getForkOptions() {
        return forkOptions;
    }

    @Override
    public Iterable<File> getClasspath() {
        return classpath;
    }

    @Override
    public Iterable<File> getChangingClasspath() {
        return changingClasspath;
    }

    @Override
    public File getProjectPath() {
        return projectPath;
    }

    @Override
    public int getHttpPort() {
        return httpPort;
    }

    @Override
    public File getApplicationJar() {
        return applicationJar;
    }

    @Override
    public File getAssetsJar() {
        return assetsJar;
    }

    @Override
    public Iterable<File> getAssetsDirs() {
        return assetsDirs;
    }
}
