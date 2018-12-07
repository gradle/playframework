package com.playframework.gradle.tools.routes;

import org.gradle.api.tasks.compile.BaseForkOptions;

import java.io.File;
import java.util.Collection;

public class DefaultRoutesCompileSpec implements RoutesCompileSpec {
    private final Iterable<File> sourceFiles;
    private final File outputDirectory;
    private final BaseForkOptions forkOptions;
    private final boolean javaProject;
    private final boolean namespaceReverseRouter;
    private final boolean generateReverseRoutes;
    private final boolean injectedRoutesGenerator;
    private final Collection<String> additionalImports;

    public DefaultRoutesCompileSpec(Iterable<File> sourceFiles, File outputDirectory, BaseForkOptions forkOptions, boolean javaProject, boolean namespaceReverseRouter, boolean generateReverseRoutes, boolean injectedRoutesGenerator, Collection<String> additionalImports) {
        this.sourceFiles = sourceFiles;
        this.outputDirectory = outputDirectory;
        this.forkOptions = forkOptions;
        this.javaProject = javaProject;
        this.namespaceReverseRouter = namespaceReverseRouter;
        this.generateReverseRoutes = generateReverseRoutes;
        this.injectedRoutesGenerator = injectedRoutesGenerator;
        this.additionalImports = additionalImports;
    }

    @Override
    public Iterable<File> getSources() {
        return sourceFiles;
    }

    @Override
    public File getDestinationDir() {
        return outputDirectory;
    }

    @Override
    public BaseForkOptions getForkOptions() {
        return forkOptions;
    }

    @Override
    public boolean isJavaProject() {
        return javaProject;
    }

    @Override
    public boolean isNamespaceReverseRouter() {
        return namespaceReverseRouter;
    }

    @Override
    public boolean isGenerateReverseRoutes() {
        return generateReverseRoutes;
    }

    @Override
    public boolean isInjectedRoutesGenerator() {
        return injectedRoutesGenerator;
    }

    @Override
    public Collection<String> getAdditionalImports() {
        return additionalImports;
    }
}
