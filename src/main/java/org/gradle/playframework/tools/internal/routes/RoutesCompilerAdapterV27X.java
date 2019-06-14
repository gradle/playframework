package org.gradle.playframework.tools.internal.routes;

import org.gradle.playframework.tools.internal.reflection.DirectInstantiator;
import org.gradle.playframework.tools.internal.scala.ScalaListBuffer;
import org.gradle.playframework.tools.internal.scala.ScalaObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RoutesCompilerAdapterV27X extends RoutesCompilerAdapterV24X {

    private static final String PLAY_ROUTES_COMPILER_INJECTED_ROUTES_GENERATOR = "play.routes.compiler.InjectedRoutesGenerator";

    private final List<String> defaultScalaImports = new ArrayList<>(Collections.singletonList("controllers.Assets.Asset"));
    private final List<String> defaultJavaImports = new ArrayList<>(Arrays.asList("controllers.Assets.Asset", "play.libs.F"));

    public RoutesCompilerAdapterV27X(String playVersion, String scalaVersion) {
        // No 2.11 version of routes compiler published
        super(playVersion, scalaVersion);
    }

    @Override
    public Object[] createCompileParameters(ClassLoader cl, File file, File destinationDir, boolean javaProject, boolean namespaceReverseRouter, boolean generateReverseRoutes, boolean injectedRoutesGenerator, Collection<String> additionalImports) throws ClassNotFoundException {
        List<String> defaultImports = javaProject ? defaultJavaImports : defaultScalaImports;
        defaultImports.addAll(additionalImports);

        Object routesCompilerTask = DirectInstantiator.instantiate(cl.loadClass("play.routes.compiler.RoutesCompiler$RoutesCompilerTask"),
                file,
                ScalaListBuffer.fromList(cl, defaultImports),
                isGenerateForwardsRouter(),
                generateReverseRoutes,
                namespaceReverseRouter
        );

        return new Object[]{
                routesCompilerTask,
                new ScalaObject(cl, PLAY_ROUTES_COMPILER_INJECTED_ROUTES_GENERATOR).getInstance(),
                destinationDir
        };
    }
}

