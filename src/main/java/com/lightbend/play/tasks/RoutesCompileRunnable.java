package com.lightbend.play.tasks;

import com.lightbend.play.tools.routes.RoutesCompileSpec;
import com.lightbend.play.tools.routes.RoutesCompiler;
import org.gradle.api.UncheckedIOException;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RoutesCompileRunnable implements Runnable {

    private final RoutesCompileSpec routesCompileSpec;
    private final RoutesCompiler routesCompiler;

    @Inject
    public RoutesCompileRunnable(RoutesCompileSpec routesCompileSpec, RoutesCompiler routesCompiler) {
        this.routesCompileSpec = routesCompileSpec;
        this.routesCompiler = routesCompiler;
    }

    @Override
    public void run() {
        Path destinationPath = routesCompileSpec.getDestinationDir().toPath();
        deleteOutputs(destinationPath);
        routesCompiler.execute(routesCompileSpec);
    }

    private void deleteOutputs(Path pathToBeDeleted) {
        try {
            Files.walk(pathToBeDeleted).map(path -> path.toFile()).forEach(file -> file.delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
