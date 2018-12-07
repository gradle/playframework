package com.playframework.gradle.tools;

import org.gradle.api.tasks.compile.BaseForkOptions;

import java.io.File;

public interface PlayCompileSpec extends CompileSpec {
    File getDestinationDir();

    BaseForkOptions getForkOptions();
}
