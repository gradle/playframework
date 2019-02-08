package org.gradle.playframework.tools.internal.routes;

import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.WorkResults;
import org.gradle.playframework.tools.internal.Compiler;
import org.gradle.playframework.tools.internal.scala.ScalaMethod;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class RoutesCompiler implements Compiler<RoutesCompileSpec>, Serializable {
    private final VersionedRoutesCompilerAdapter adapter;

    public RoutesCompiler(VersionedRoutesCompilerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public WorkResult execute(RoutesCompileSpec spec) {
        boolean didWork = false;
        // Need to compile all secondary routes ("Foo.routes") before primary ("routes")
        ArrayList<File> primaryRoutes = new ArrayList<>();
        ArrayList<File> secondaryRoutes = new ArrayList<>();
        for (File source : spec.getSources()) {
            if (source.getName().equals("routes")) {
                primaryRoutes.add(source);
            } else {
                secondaryRoutes.add(source);
            }
        }

        // Compile all secondary routes files first
        for (File sourceFile : secondaryRoutes) {
            Boolean ret = compile(sourceFile, spec);
            didWork = ret || didWork;
        }

        // Compile all main routes files last
        for (File sourceFile : primaryRoutes) {
            Boolean ret = compile(sourceFile, spec);
            didWork = ret || didWork;
        }

        return WorkResults.didWork(didWork);
    }

    private Boolean compile(File sourceFile, RoutesCompileSpec spec) {

        try {
            ClassLoader cl = getClass().getClassLoader();
            ScalaMethod compile = adapter.getCompileMethod(cl);
            return adapter.interpretResult(compile.invoke(adapter.createCompileParameters(cl, sourceFile, spec.getDestinationDir(), spec.isJavaProject(), spec.isNamespaceReverseRouter(), spec.isGenerateReverseRoutes(), spec.isInjectedRoutesGenerator(), spec.getAdditionalImports())));
        } catch (Exception e) {
            throw new RuntimeException("Error invoking the Play routes compiler.", e);
        }
    }

    public Object getDependencyNotation() {
        return adapter.getDependencyNotation();
    }

    public Iterable<String> getClassLoaderPackages() {
        return adapter.getClassLoaderPackages();
    }
}
