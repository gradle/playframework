package org.gradle.playframework.tools.internal.webjars;

import java.io.File;
import java.io.Serializable;
import java.util.Set;

public interface WebJarsExtractSpec extends Serializable {
    Set<File> getClasspath();

    File getDestinationDir();
}
