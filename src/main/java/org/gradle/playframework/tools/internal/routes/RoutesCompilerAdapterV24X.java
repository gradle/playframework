package org.gradle.playframework.tools.internal.routes;

import org.gradle.internal.Cast;
import org.gradle.playframework.tools.internal.reflection.DirectInstantiator;
import org.gradle.playframework.tools.internal.reflection.JavaMethod;
import org.gradle.playframework.tools.internal.reflection.JavaReflectionUtil;
import org.gradle.playframework.tools.internal.scala.ScalaListBuffer;
import org.gradle.playframework.tools.internal.scala.ScalaMethod;
import org.gradle.playframework.tools.internal.scala.ScalaObject;
import org.gradle.playframework.tools.internal.scala.ScalaReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RoutesCompilerAdapterV24X extends DefaultVersionedRoutesCompilerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoutesCompilerAdapterV24X.class);

    protected static final String PLAY_ROUTES_COMPILER_STATIC_ROUTES_GENERATOR = "play.routes.compiler.StaticRoutesGenerator";
    protected static final String PLAY_ROUTES_COMPILER_INJECTED_ROUTES_GENERATOR = "play.routes.compiler.InjectedRoutesGenerator";

    private final List<String> defaultScalaImports = new ArrayList<>(Arrays.asList("controllers.Assets.Asset"));
    private final List<String> defaultJavaImports = new ArrayList<>(Arrays.asList("controllers.Assets.Asset", "play.libs.F"));

    public RoutesCompilerAdapterV24X(String playVersion, String scalaVersion) {
        // No 2.11 version of routes compiler published
        super(playVersion, scalaVersion);
    }

    @Override
    public ScalaMethod getCompileMethod(ClassLoader cl) throws ClassNotFoundException {
        return ScalaReflectionUtil.scalaMethod(
                cl,
                "play.routes.compiler.RoutesCompiler",
                "compile",
                cl.loadClass("play.routes.compiler.RoutesCompiler$RoutesCompilerTask"),
                cl.loadClass("play.routes.compiler.RoutesGenerator"),
                File.class
        );
    }

    @Override
    public Object[] createCompileParameters(ClassLoader cl, File file, File destinationDir, boolean javaProject, boolean namespaceReverseRouter, boolean generateReverseRoutes, boolean injectedRoutesGenerator, Collection<String> additionalImports) throws ClassNotFoundException {
        List<String> defaultImports = getDefaultImports(javaProject);
        defaultImports.addAll(additionalImports);

        Object routesCompilerTask = DirectInstantiator.instantiate(cl.loadClass("play.routes.compiler.RoutesCompiler$RoutesCompilerTask"),
                file,
                ScalaListBuffer.fromList(cl, defaultImports),
                isGenerateForwardsRouter(),
                generateReverseRoutes,
                namespaceReverseRouter
        );

        String routeGenerator;
        if (injectedRoutesGenerator) {
            routeGenerator = PLAY_ROUTES_COMPILER_INJECTED_ROUTES_GENERATOR;
        } else {
            routeGenerator = PLAY_ROUTES_COMPILER_STATIC_ROUTES_GENERATOR;
        }
        return new Object[]{
                routesCompilerTask,
                new ScalaObject(cl, routeGenerator).getInstance(),
                destinationDir
        };
    }

    protected List<String> getDefaultImports(boolean javaProject) {
        return javaProject ? defaultJavaImports : defaultScalaImports;
    }

    protected boolean isGenerateForwardsRouter() {
        return true;
    }

    @Override
    public Boolean interpretResult(Object result) throws ClassNotFoundException {
        // result is a scala.util.Either
        // right is a Seq of files that were generated
        // left is routes compilation errors
        // TODO: It might be nice to pass along these errors in some way
        JavaMethod<Object, Boolean> isRight = JavaReflectionUtil.method(result, Boolean.class, "isRight");
        Boolean successful = Cast.cast(Boolean.class, isRight.invoke(result));
        if (successful) {
            // extract the files that were generated
            /*
                val rightResult = result.right()
                val generatedFiles = right.get()
                val empty = generatedFiles.isEmpty()
                empty.booleanValue()
             */
            JavaMethod<Object, Object> right = JavaReflectionUtil.method(result, Object.class, "right");
            Object rightResult = right.invoke(result);
            JavaMethod<Object, Object> get = JavaReflectionUtil.method(rightResult, Object.class, "get");
            Object generatedFiles = get.invoke(rightResult);
            JavaMethod<Object, Object> isEmpty = JavaReflectionUtil.method(generatedFiles, Object.class, "isEmpty");
            Object empty = isEmpty.invoke(generatedFiles);
            JavaMethod<Object, Boolean> booleanValue = JavaReflectionUtil.method(empty, Boolean.class, "booleanValue");
            return booleanValue.invoke(empty);
        } else {
            // extract exceptions
            /*
                val leftResult = result.left()
                val errorSeq = left.get()

                // convert errorSeq -> Java types
             */
            JavaMethod<Object, Object> left = JavaReflectionUtil.method(result, Object.class, "left");
            Object leftResult = left.invoke(result);
            JavaMethod<Object, Object> get = JavaReflectionUtil.method(leftResult, Object.class, "get");
            Object errorSeq = get.invoke(leftResult);

            // Convert Scala Seq[RoutesCompilationError] -> Java List<RoutesCompilationError>
            ClassLoader resultCl = result.getClass().getClassLoader();
            ScalaMethod seqAsJavaList = ScalaReflectionUtil.scalaMethod(resultCl, getScalaToJavaConverterClassName(), "seqAsJavaList", resultCl.loadClass("scala.collection.Seq"));
            List<Object> errors = Cast.uncheckedCast(seqAsJavaList.invoke(errorSeq));

            RoutesCompilationErrorAdapter errorAdapter = new RoutesCompilationErrorAdapter(
                    resultCl.loadClass("play.routes.compiler.RoutesCompilationError"),
                    resultCl.loadClass("scala.Option"));

            for (Object error : errors) {
                RoutesCompilationError adaptedError = errorAdapter.adapt(error);
                String message = adaptedError.toString();
                LOGGER.error(message);
            }
            throw new RuntimeException("route compilation failed with errors");
        }
    }

    protected String getScalaToJavaConverterClassName() {
        return "scala.collection.JavaConversions";
    }

    private static class RoutesCompilationErrorAdapter {
        private final JavaMethod<Object, File> sourceMethod;
        private final JavaMethod<Object, String> messageMethod;
        private final JavaMethod<Object, Object> lineMethod;
        private final JavaMethod<Object, Object> columnMethod;
        private final JavaMethod<Object, Object> getMethod;

        private RoutesCompilationErrorAdapter(Class<?> routesCompilationError, Class<?> option) {
            this.sourceMethod = Cast.uncheckedCast(JavaReflectionUtil.method(routesCompilationError, File.class, "source"));
            this.messageMethod = Cast.uncheckedCast(JavaReflectionUtil.method(routesCompilationError, String.class, "message"));
            this.lineMethod = Cast.uncheckedCast(JavaReflectionUtil.method(routesCompilationError, Object.class, "line"));
            this.columnMethod = Cast.uncheckedCast(JavaReflectionUtil.method(routesCompilationError, Object.class, "column"));
            this.getMethod = Cast.uncheckedCast(JavaReflectionUtil.method(option, Object.class, "get"));
        }

        RoutesCompilationError adapt(Object error) {
            return new RoutesCompilationError(
                    sourceMethod.invoke(error),
                    messageMethod.invoke(error),
                    toInt(lineMethod.invoke(error)),
                    toInt(columnMethod.invoke(error)));
        }

        Integer toInt(Object optionInt) {
            try {
                return Cast.uncheckedCast(getMethod.invoke(optionInt));
            } catch (Exception e) {
                return 0;
            }
        }
    }

    private static class RoutesCompilationError {
        private final File source;
        private final String message;
        private final int line;
        private final int col;

        private RoutesCompilationError(File source, String message, int line, int col) {
            this.source = source;
            this.message = message;
            this.line = line;
            this.col = col;
        }

        @Override
        public String toString() {
            if (line > 0 && col > 0) {
                return source.getAbsolutePath() + ":" + line + ":" + col + " " + message;
            } else {
                return source.getAbsolutePath() + " " + message;
            }
        }
    }
}

