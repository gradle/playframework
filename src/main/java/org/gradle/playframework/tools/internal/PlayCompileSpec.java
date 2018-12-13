package org.gradle.playframework.tools.internal;

import java.io.File;

public interface PlayCompileSpec extends CompileSpec {
    File getDestinationDir();
}
