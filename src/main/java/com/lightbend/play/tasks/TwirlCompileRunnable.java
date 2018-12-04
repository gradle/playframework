package com.lightbend.play.tasks;

import org.gradle.api.UncheckedIOException;
import org.gradle.play.internal.twirl.TwirlCompileSpec;
import org.gradle.play.internal.twirl.TwirlCompiler;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TwirlCompileRunnable implements Runnable {

    private final TwirlCompileSpec twirlCompileSpec;
    private final TwirlCompiler twirlCompiler;

    @Inject
    public TwirlCompileRunnable(TwirlCompileSpec twirlCompileSpec, TwirlCompiler twirlCompiler) {
        this.twirlCompileSpec = twirlCompileSpec;
        this.twirlCompiler = twirlCompiler;
    }

    @Override
    public void run() {
        Path destinationPath = twirlCompileSpec.getDestinationDir().toPath();
        deleteOutputs(destinationPath);
        twirlCompiler.execute(twirlCompileSpec);
    }

    private void deleteOutputs(Path pathToBeDeleted) {
        try {
            Files.walk(pathToBeDeleted).map(path -> path.toFile()).forEach(file -> file.delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
