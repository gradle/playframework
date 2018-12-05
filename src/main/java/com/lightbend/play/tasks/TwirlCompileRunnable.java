package com.lightbend.play.tasks;

import com.lightbend.play.platform.PlayPlatform;
import com.lightbend.play.tools.Compiler;
import com.lightbend.play.tools.twirl.TwirlCompileSpec;
import com.lightbend.play.tools.twirl.TwirlCompilerFactory;
import org.gradle.api.UncheckedIOException;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TwirlCompileRunnable implements Runnable {

    private final TwirlCompileSpec twirlCompileSpec;
    private final PlayPlatform playPlatform;

    @Inject
    public TwirlCompileRunnable(TwirlCompileSpec twirlCompileSpec, PlayPlatform playPlatform) {
        this.twirlCompileSpec = twirlCompileSpec;
        this.playPlatform = playPlatform;
    }

    @Override
    public void run() {
        Path destinationPath = twirlCompileSpec.getDestinationDir().toPath();
        deleteOutputs(destinationPath);
        getCompiler().execute(twirlCompileSpec);
    }

    private void deleteOutputs(Path pathToBeDeleted) {
        try {
            Files.walk(pathToBeDeleted).map(path -> path.toFile()).forEach(file -> file.delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Compiler<TwirlCompileSpec> getCompiler() {
        return TwirlCompilerFactory.create(playPlatform);
    }
}
