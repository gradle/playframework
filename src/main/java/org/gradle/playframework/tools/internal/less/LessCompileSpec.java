package org.gradle.playframework.tools.internal.less;

import org.gradle.api.internal.file.RelativeFile;
import org.gradle.playframework.tools.internal.PlayCompileSpec;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public interface LessCompileSpec extends PlayCompileSpec, Serializable {
    Iterable<RelativeFile> getSources();

    List<File> getIncludePaths();
}
