package org.gradle.playframework.tools.internal.javascript;

import org.gradle.api.Transformer;
import org.gradle.api.internal.file.RelativeFile;

import java.io.File;

public class JavaScriptCompileDestinationCalculator implements Transformer<File, RelativeFile> {
    private final File destinationDir;

    public JavaScriptCompileDestinationCalculator(File destinationDir) {
        this.destinationDir = destinationDir;
    }

    @Override
    public File transform(RelativeFile file) {
        final File outputFileDir = new File(destinationDir, file.getRelativePath().getParent().getPathString());
        return new File(outputFileDir, getMinifiedFileName(file.getFile().getName()));
    }

    private static String getMinifiedFileName(String fileName) {
        int extIndex = fileName.lastIndexOf('.');
        if (extIndex == -1) {
            return fileName + ".min";
        }
        String prefix = fileName.substring(0, extIndex);
        String extension = fileName.substring(extIndex);
        return prefix + ".min" + extension;
    }
}

