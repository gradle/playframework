package org.gradle.playframework.tools.internal.routes;

import org.gradle.playframework.tools.internal.reflection.DirectInstantiator;
import org.gradle.playframework.tools.internal.scala.ScalaListBuffer;
import org.gradle.playframework.tools.internal.scala.ScalaObject;
import org.gradle.playframework.tools.internal.scala.ScalaSeq;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class RoutesCompilerAdapterV27X extends RoutesCompilerAdapterV24X {
    public RoutesCompilerAdapterV27X(String playVersion, String scalaVersion) {
        super(playVersion, scalaVersion);
    }

    @Override
    public Object[] createCompileParameters(ClassLoader cl, File file, File destinationDir, boolean javaProject, boolean namespaceReverseRouter, boolean generateReverseRoutes, boolean injectedRoutesGenerator, Collection<String> additionalImports) throws ClassNotFoundException {
        List<String> defaultImports = getDefaultImports(javaProject);
        defaultImports.addAll(additionalImports);

        Object routesCompilerTask = DirectInstantiator.instantiate(cl.loadClass("play.routes.compiler.RoutesCompiler$RoutesCompilerTask"),
                file,
                ScalaSeq.fromList(cl, defaultImports),
                isGenerateForwardsRouter(),
                generateReverseRoutes,
                namespaceReverseRouter
        );

        String routeGenerator = PLAY_ROUTES_COMPILER_INJECTED_ROUTES_GENERATOR;
        assert !injectedRoutesGenerator : "Play 2.7+ does not support the static routes generator";

        return new Object[]{
                routesCompilerTask,
                new ScalaObject(cl, routeGenerator).getInstance(),
                destinationDir
        };
    }

    protected String getScalaToJavaConverterClassName() {
        return "scala.collection.JavaConverters";
    }
}
