package org.gradle.playframework.tools.internal.routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

class DefaultRoutesPostProcessor implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRoutesPostProcessor.class);

    void execute(RoutesCompileSpec spec) {
        if(spec.isStripRoutesComments()) {
            try (Stream<Path> stream = Files.find(spec.getDestinationDir().toPath(), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())) {
                stream.forEach(this::process);
            } catch (IOException e) {
                LOGGER.warn("Unable to post-process routes", e);
            }
        }
    }

    private void process(Path routeFile) {
        try {
            String content = new String(Files.readAllBytes(routeFile), StandardCharsets.UTF_8);
            content = content.replaceAll("(?m)^// @(SOURCE):.*", "");
            content = content.replaceAll("(?m)^// @(DATE):.*", "");
            Files.write(routeFile, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.warn(String.format("Unable to post-process route file %s", routeFile.getFileName()), e);
        }
    }
}
