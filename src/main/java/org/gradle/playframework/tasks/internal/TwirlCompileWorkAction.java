package org.gradle.playframework.tasks.internal;

import org.gradle.api.UncheckedIOException;
import org.gradle.playframework.tools.internal.twirl.TwirlCompileSpec;
import org.gradle.workers.WorkAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class TwirlCompileWorkAction implements WorkAction<TwirlCompileParameters> {

    @Override
    public void execute() {
        TwirlCompileSpec twirlCompileSpec = getParameters().getTwirlCompileSpec().get();
        Path destinationPath = twirlCompileSpec.getDestinationDir().toPath();
        deleteOutputs(destinationPath);
        getParameters().getCompiler().get().execute(twirlCompileSpec);
    }

    private void deleteOutputs(Path pathToBeDeleted) {
        try {
            Files.walk(pathToBeDeleted).map(path -> path.toFile()).forEach(file -> file.delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
