package org.gradle.playframework.tools.internal.javascript;

import org.gradle.playframework.tools.internal.PlayCompileSpec;
import org.gradle.api.internal.file.RelativeFile;

public interface JavaScriptCompileSpec extends PlayCompileSpec {
    Iterable<RelativeFile> getSources();
}
