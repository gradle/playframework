package com.playframework.gradle.tasks;

import com.playframework.gradle.platform.PlayPlatform;
import com.playframework.gradle.tools.Compiler;
import com.playframework.gradle.tools.routes.RoutesCompileSpec;
import com.playframework.gradle.tools.routes.RoutesCompilerFactory;
import org.gradle.api.UncheckedIOException;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RoutesCompileRunnable implements Runnable {

    private final RoutesCompileSpec routesCompileSpec;
    private final PlayPlatform playPlatform;

    @Inject
    public RoutesCompileRunnable(RoutesCompileSpec routesCompileSpec, PlayPlatform playPlatform) {
        this.routesCompileSpec = routesCompileSpec;
        this.playPlatform = playPlatform;
    }

    @Override
    public void run() {
        Path destinationPath = routesCompileSpec.getDestinationDir().toPath();
        deleteOutputs(destinationPath);
        getCompiler().execute(routesCompileSpec);
    }

    private void deleteOutputs(Path pathToBeDeleted) {
        try {
            Files.walk(pathToBeDeleted).map(path -> path.toFile()).forEach(file -> file.delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Compiler<RoutesCompileSpec> getCompiler() {
        return RoutesCompilerFactory.create(playPlatform);
    }
}
