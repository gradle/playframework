package org.gradle.playframework.tools.internal.less;

import org.gradle.api.internal.file.RelativeFile;

import java.io.File;
import java.util.List;

public class DefaultLessCompileSpec implements LessCompileSpec {
    private final Iterable<RelativeFile> sourceFiles;
    private final File destinationDir;
    private final List<File> includePaths;

    public DefaultLessCompileSpec(Iterable<RelativeFile> sourceFiles, File destinationDir, List<File> includePaths) {
        this.sourceFiles = sourceFiles;
        this.destinationDir = destinationDir;
        this.includePaths = includePaths;
    }

    @Override
    public Iterable<RelativeFile> getSources() {
        return sourceFiles;
    }

    @Override
    public File getDestinationDir() {
        return destinationDir;
    }

    @Override
    public List<File> getIncludePaths() {
        return includePaths;
    }
}
