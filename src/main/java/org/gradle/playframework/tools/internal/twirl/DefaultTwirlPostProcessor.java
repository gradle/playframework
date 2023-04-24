package org.gradle.playframework.tools.internal.twirl;

import org.gradle.api.internal.file.RelativeFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

// This post processor fixes build / project dependent comments (DATE and SOURCE) from the TwirlCompiler generated files:
//                    -- GENERATED --
//                    DATE: Mon Apr 03 10:27:51 CEST 2023
//                    SOURCE: /private/var/folders/79/xmc9yr493y75ptry2_nrx3r00000gn/T/junit4995996226044083355/app/views/test.scala.html
//                    HASH: 4bbbe5fde39afa0d46da8df7714a136a78170d6a
//                    MATRIX: 728->1|841->19|869->20|920->45|948->53
//                    LINES: 21->1|26->1|26->1|26->1|26->1
//                    -- GENERATED --
class DefaultTwirlPostProcessor implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTwirlPostProcessor.class);

    private static final String GENERATED_TAG = "-- GENERATED --";
    private static final String GENERATED_LINE_PREFIX_DATE = "DATE: ";
    private static final String GENERATED_LINE_PREFIX_SOURCE = "SOURCE: ";

    void execute(TwirlCompileSpec spec) {
        String sourceReplacementString = getSourceReplacementString(spec.getSources());

        try (Stream<Path> stream = Files.find(spec.getDestinationDir().toPath(), Integer.MAX_VALUE, (filePath, fileAttr) -> fileAttr.isRegularFile())) {
            stream.forEach(routeFile -> process(routeFile, sourceReplacementString));
        } catch (IOException e) {
            LOGGER.warn("Unable to post-process files", e);
        }
    }

    private String getSourceReplacementString(Iterable<RelativeFile> sources) {
        String sourceReplacementString = "";

        if(sources.iterator().hasNext()) {
            RelativeFile sourceFile = sources.iterator().next();
            sourceReplacementString = "SOURCE: " + sourceFile.getRelativePath();
        }

        return sourceReplacementString;
    }

    private void process(Path generatedFile, String sourceReplacementString) {
        try {
            List<String> generatedSourceLines = Files.readAllLines(generatedFile, StandardCharsets.UTF_8);
            List<String> updatedSourceLines = new ArrayList<>();

            boolean isInGeneratedSection = false;
            for (String currentLine : generatedSourceLines) {
                if(currentLine.contains(GENERATED_TAG)) {
                    isInGeneratedSection = !isInGeneratedSection;
                }
                if(isInGeneratedSection && currentLine.contains(GENERATED_LINE_PREFIX_SOURCE)) {
                    // update path to relative and keep trailing spaces
                    String updatedLine = currentLine.substring(0, currentLine.indexOf(GENERATED_LINE_PREFIX_SOURCE)) + sourceReplacementString;
                    updatedSourceLines.add(updatedLine);
                } else if(!(isInGeneratedSection && currentLine.contains(GENERATED_LINE_PREFIX_DATE))) {
                    updatedSourceLines.add(currentLine);
                }
            }

            Files.write(generatedFile, updatedSourceLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn(String.format("Unable to post-process file %s", generatedFile.getFileName()), e);
        }
    }
}
