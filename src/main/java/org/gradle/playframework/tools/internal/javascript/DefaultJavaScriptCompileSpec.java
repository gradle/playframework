package org.gradle.playframework.tools.internal.javascript;

import org.gradle.api.internal.file.RelativeFile;

import java.io.File;
import java.io.Serializable;

public class DefaultJavaScriptCompileSpec implements JavaScriptCompileSpec, Serializable {

    private final Iterable<RelativeFile> sources;
    private final File destinationDir;

    public DefaultJavaScriptCompileSpec(Iterable<RelativeFile> sources, File destinationDir) {
        this.sources = sources;
        this.destinationDir = destinationDir;
    }

    @Override
    public Iterable<RelativeFile> getSources() {
        return sources;
    }

    @Override
    public File getDestinationDir() {
        return destinationDir;
    }
}