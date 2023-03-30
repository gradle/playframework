package org.gradle.playframework.tools.internal.routes;

import org.gradle.internal.Cast;
import org.gradle.playframework.tools.internal.scala.ScalaListBuffer;
import org.gradle.playframework.tools.internal.scala.ScalaMethod;
import org.gradle.playframework.tools.internal.scala.ScalaReflectionUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class RoutesCompilerAdapterV23X extends DefaultVersionedRoutesCompilerAdapter {
    private final List<String> defaultScalaImports = Arrays.asList("controllers.Assets.Asset");
    private final List<String> defaultJavaImports = Arrays.asList("controllers.Assets.Asset", "play.libs.F");

    public RoutesCompilerAdapterV23X(String playVersion) {
        // No 2.11 version of routes compiler published
        super(playVersion, "2.10");
    }

    @Override
    public ScalaMethod getCompileMethod(ClassLoader cl) throws ClassNotFoundException {
        return ScalaReflectionUtil.scalaMethod(
                cl,
                "play.router.RoutesCompiler",
                "compile",
                File.class,
                File.class,
                cl.loadClass("scala.collection.Seq"),
                boolean.class,
                boolean.class,
                boolean.class
        );
    }

    @Override
    public Object[] createCompileParameters(ClassLoader cl, File file, File destinationDir, boolean javaProject, boolean namespaceReverseRouter, boolean generateReverseRoutes, boolean injectedRoutesGenerator, Collection<String> additionalImports) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<String> defaultImports = javaProject ? defaultJavaImports : defaultScalaImports;
        defaultImports.addAll(additionalImports);
        return new Object[] {
                file,
                destinationDir,
                ScalaListBuffer.fromList(cl, defaultImports),
                generateReverseRoutes,
                isGenerateRefReverseRouter(),
                namespaceReverseRouter
        };
    }

    @Override
    public Boolean interpretResult(Object result) {
        return true;
    }
}
