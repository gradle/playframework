package org.gradle.playframework.tools.internal.twirl;

import org.gradle.api.internal.file.RelativeFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

class DefaultTwirlPostProcessor implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTwirlPostProcessor.class);

    private static final String GENERATED_TAG = "-- GENERATED --";
    private static final String ANY_CHAR_INCLUDING_NEW_LINE = "[\\S\\n\\r\\s]";
    private static final String ANY_LINES = ANY_CHAR_INCLUDING_NEW_LINE + "*";
    private static final String ANY_LINE = ".*" + ANY_CHAR_INCLUDING_NEW_LINE;

    // Example of text to match:
    // /*
    //                    -- GENERATED --
    //                    DATE: Mon Apr 03 10:27:51 CEST 2023
    //                    SOURCE: /private/var/folders/79/xmc9yr493y75ptry2_nrx3r00000gn/T/junit4995996226044083355/app/views/test.scala.html
    //                    HASH: 4bbbe5fde39afa0d46da8df7714a136a78170d6a
    //                    MATRIX: 728->1|841->19|869->20|920->45|948->53
    //                    LINES: 21->1|26->1|26->1|26->1|26->1
    //                    -- GENERATED --
    //  */
    private static final String PATTERN_MATCHER = "(/\\*" + ANY_LINES + GENERATED_TAG + ANY_LINES + ")(%s:\\s" + ANY_LINE + ")(" + ANY_LINES + GENERATED_TAG + ANY_LINES + "\\*/)";

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
            String content = new String(Files.readAllBytes(generatedFile), StandardCharsets.UTF_8);
            String regexSource = String.format(PATTERN_MATCHER, "SOURCE");
            Matcher matcherSource = Pattern.compile(regexSource, Pattern.MULTILINE).matcher(content);
            if(matcherSource.find() && matcherSource.groupCount() == 3) {
                content = matcherSource.replaceAll("$1" + sourceReplacementString + "\n$3");
            }
            String regexDate = String.format(PATTERN_MATCHER, "DATE");
            Matcher matcherDate = Pattern.compile(regexDate, Pattern.MULTILINE).matcher(content);
            if(matcherDate.find() && matcherDate.groupCount() == 3) {
                content = matcherDate.replaceAll("$1\n$3");
            }
            Files.write(generatedFile, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.warn(String.format("Unable to post-process file %s", generatedFile.getFileName()), e);
        }
    }
}
