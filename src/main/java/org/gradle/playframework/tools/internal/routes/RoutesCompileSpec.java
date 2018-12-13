package org.gradle.playframework.tools.internal.routes;

import org.gradle.playframework.tools.internal.PlayCompileSpec;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;

public interface RoutesCompileSpec extends PlayCompileSpec, Serializable {
    Iterable<File> getSources();

    boolean isJavaProject();

    boolean isNamespaceReverseRouter();

    boolean isGenerateReverseRoutes();

    boolean isInjectedRoutesGenerator();

    Collection<String> getAdditionalImports();
}
