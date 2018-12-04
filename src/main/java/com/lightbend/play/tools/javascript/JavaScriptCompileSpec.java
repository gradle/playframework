package com.lightbend.play.tools.javascript;

import com.lightbend.play.tools.PlayCompileSpec;
import org.gradle.api.internal.file.RelativeFile;

public interface JavaScriptCompileSpec extends PlayCompileSpec {
    Iterable<RelativeFile> getSources();
}
