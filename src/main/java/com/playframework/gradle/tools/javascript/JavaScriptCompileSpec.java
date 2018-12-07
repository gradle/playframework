package com.playframework.gradle.tools.javascript;

import com.playframework.gradle.tools.PlayCompileSpec;
import org.gradle.api.internal.file.RelativeFile;

public interface JavaScriptCompileSpec extends PlayCompileSpec {
    Iterable<RelativeFile> getSources();
}
