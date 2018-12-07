package com.playframework.gradle.tasks.internal;

import com.playframework.gradle.tools.internal.Compiler;
import com.playframework.gradle.tools.internal.routes.RoutesCompileSpec;
import org.gradle.api.UncheckedIOException;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RoutesCompileRunnable implements Runnable {

    private final RoutesCompileSpec routesCompileSpec;
    private final Compiler<RoutesCompileSpec> compiler;

    @Inject
    public RoutesCompileRunnable(RoutesCompileSpec routesCompileSpec, Compiler<RoutesCompileSpec> compiler) {
        this.routesCompileSpec = routesCompileSpec;
        this.compiler = compiler;
    }

    @Override
    public void run() {
        Path destinationPath = routesCompileSpec.getDestinationDir().toPath();
        deleteOutputs(destinationPath);
        compiler.execute(routesCompileSpec);
    }

    private void deleteOutputs(Path pathToBeDeleted) {
        try {
            Files.walk(pathToBeDeleted).map(path -> path.toFile()).forEach(file -> file.delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
