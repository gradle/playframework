package org.gradle.playframework.tools.internal.twirl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.gradle.playframework.sourcesets.TwirlImports;

class PlayTwirlAdapterV28X implements VersionedPlayTwirlAdapter {

    // Based on https://github.com/playframework/playframework/blob/2.8.x/dev-mode/build-link/src/main/java/play/TemplateImports.java
    private static List<String> defaultTemplateImports = Collections.unmodifiableList(
            Arrays.asList(
                    "models._",
                    "controllers._",
                    "play.api.i18n._",
                    "play.api.templates.PlayMagic._"
            ));

    private static final List<String> DEFAULT_JAVA_IMPORTS;
    private static final List<String> DEFAULT_SCALA_IMPORTS;
    static {
        List<String> minimalJavaImports = new ArrayList<String>();
        minimalJavaImports.addAll(defaultTemplateImports);
        minimalJavaImports.add("java.lang._");
        minimalJavaImports.add("java.util._");
        minimalJavaImports.add("play.core.j.PlayMagicForJava._");
        minimalJavaImports.add("play.mvc._");
        minimalJavaImports.add("play.api.data.Field");

        List<String> defaultJavaImports = new ArrayList<String>();
        defaultJavaImports.addAll(minimalJavaImports);
        defaultJavaImports.add("play.data._");
        defaultJavaImports.add("play.core.j.PlayFormsMagicForJava._");
        DEFAULT_JAVA_IMPORTS = Collections.unmodifiableList(defaultJavaImports);

        List<String> scalaImports = new ArrayList<String>();
        scalaImports.addAll(defaultTemplateImports);
        scalaImports.add("play.api.mvc._");
        scalaImports.add("play.api.data._");
        DEFAULT_SCALA_IMPORTS = Collections.unmodifiableList(scalaImports);
    }

    @Override
    public List<String> getDefaultImports(TwirlImports language) {
        return language == TwirlImports.JAVA ? DEFAULT_JAVA_IMPORTS : DEFAULT_SCALA_IMPORTS;
    }
}

