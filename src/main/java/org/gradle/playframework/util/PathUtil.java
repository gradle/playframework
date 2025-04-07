package org.gradle.playframework.util;

import java.io.File;

public class PathUtil {

    public static String relativePath(File from, File to) {
        String path = from.toPath().relativize(to.toPath()).toString();
        return path.replace(File.separatorChar, '/');
    }

}
