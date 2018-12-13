package org.gradle.playframework.tools.internal.twirl;

import org.gradle.playframework.sourcesets.TwirlImports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class PlayTwirlAdapterV23X implements VersionedPlayTwirlAdapter {

    // Based on https://github.com/playframework/playframework/blob/2.4.0/framework/src/build-link/src/main/java/play/TemplateImports.java
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
        List<String> javaImports = new ArrayList<String>();
        javaImports.addAll(defaultTemplateImports);
        javaImports.add("java.lang._");
        javaImports.add("java.util._");
        javaImports.add("scala.collection.JavaConversions._");
        javaImports.add("scala.collection.JavaConverters._");
        javaImports.add("play.core.j.PlayMagicForJava._");
        javaImports.add("play.mvc._");
        javaImports.add("play.data._");
        javaImports.add("play.api.data.Field");
        javaImports.add("play.mvc.Http.Context.Implicit._");
        DEFAULT_JAVA_IMPORTS = Collections.unmodifiableList(javaImports);

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
