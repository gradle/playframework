package org.gradle.playframework.tasks.internal;

import org.gradle.playframework.tools.internal.twirl.TwirlCompileSpec;
import org.gradle.playframework.tools.internal.Compiler;
import org.gradle.api.UncheckedIOException;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TwirlCompileRunnable implements Runnable {

    private final TwirlCompileSpec twirlCompileSpec;
    private final Compiler<TwirlCompileSpec> compiler;

    @Inject
    public TwirlCompileRunnable(TwirlCompileSpec twirlCompileSpec, Compiler<TwirlCompileSpec> compiler) {
        this.twirlCompileSpec = twirlCompileSpec;
        this.compiler = compiler;
    }

    @Override
    public void run() {
        Path destinationPath = twirlCompileSpec.getDestinationDir().toPath();
        deleteOutputs(destinationPath);
        compiler.execute(twirlCompileSpec);
    }

    private void deleteOutputs(Path pathToBeDeleted) {
        try {
            Files.walk(pathToBeDeleted).map(path -> path.toFile()).forEach(file -> file.delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
