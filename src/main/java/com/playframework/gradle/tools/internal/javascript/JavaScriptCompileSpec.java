package com.playframework.gradle.tools.internal.javascript;

import com.playframework.gradle.tools.internal.PlayCompileSpec;
import org.gradle.api.internal.file.RelativeFile;

public interface JavaScriptCompileSpec extends PlayCompileSpec {
    Iterable<RelativeFile> getSources();
}
