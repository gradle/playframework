package com.playframework.gradle.tools;

import java.io.File;

public interface PlayCompileSpec extends CompileSpec {
    File getDestinationDir();
}
