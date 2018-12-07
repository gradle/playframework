package com.playframework.gradle.tools.internal;

import java.io.File;

public interface PlayCompileSpec extends CompileSpec {
    File getDestinationDir();
}
