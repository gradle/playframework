package com.lightbend.play.tools;

import org.gradle.api.tasks.compile.BaseForkOptions;
import org.gradle.language.base.internal.compile.CompileSpec;

import java.io.File;

public interface PlayCompileSpec extends CompileSpec {
    File getDestinationDir();

    BaseForkOptions getForkOptions();
}
