package com.lightbend.play.javascript;

import org.gradle.api.internal.file.RelativeFile;

public interface JavaScriptCompileSpec extends PlayCompileSpec {
    Iterable<RelativeFile> getSources();
}
