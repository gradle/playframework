package org.gradle.playframework.tasks.internal;

import org.gradle.api.UncheckedIOException;
import org.gradle.playframework.tools.internal.Compiler;
import org.gradle.playframework.tools.internal.routes.RoutesCompileSpec;
import org.gradle.workers.WorkAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class RoutesCompileWorkAction implements WorkAction<RoutesCompileParameters> {

    @Override
    public void execute() {
        RoutesCompileSpec routesCompileSpec = getParameters().getSpec().get();
        Path destinationPath = routesCompileSpec.getDestinationDir().toPath();
        deleteOutputs(destinationPath);
        Compiler<RoutesCompileSpec> compiler = getParameters().getCompiler().get();
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
