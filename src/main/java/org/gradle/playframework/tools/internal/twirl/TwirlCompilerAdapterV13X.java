package org.gradle.playframework.tools.internal.twirl;

import org.gradle.playframework.sourcesets.TwirlImports;
import org.gradle.playframework.sourcesets.TwirlTemplateFormat;
import org.gradle.playframework.tools.internal.scala.ScalaCodecMapper;
import org.gradle.playframework.tools.internal.scala.ScalaListBuffer;
import org.gradle.playframework.tools.internal.scala.ScalaMethod;
import org.gradle.playframework.tools.internal.scala.ScalaReflectionUtil;
import org.gradle.playframework.tools.internal.scala.ScalaSeq;
import org.gradle.util.CollectionUtils;
import org.gradle.util.VersionNumber;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class TwirlCompilerAdapterV13X extends TwirlCompilerAdapterV10X {

    private static final Iterable<String> SHARED_PACKAGES = Arrays.asList("play.twirl.compiler", "scala.io", "scala.util.parsing.input", "scala.collection");

    // Also available via play.japi.twirl.compiler.TwirlCompiler.DEFAULT_IMPORTS but we would have to grab it via reflection
    private static final List<String> DEFAULT_TEMPLATE_IMPORTS = Collections.unmodifiableList(
            Arrays.asList(
                    // Based on https://github.com/playframework/twirl/blob/1.3.13/compiler/src/main/scala/play/twirl/compiler/TwirlCompiler.scala#L156
                    "_root_.play.twirl.api.TwirlFeatureImports._",
                    "_root_.play.twirl.api.TwirlHelperImports._",
                    "_root_.play.twirl.api.Html",
                    "_root_.play.twirl.api.JavaScript",
                    "_root_.play.twirl.api.Txt",
                    "_root_.play.twirl.api.Xml"
            ));

    public TwirlCompilerAdapterV13X(String twirlVersion, String scalaVersion, VersionedPlayTwirlAdapter playTwirlAdapter) {
        super(twirlVersion, scalaVersion, playTwirlAdapter);
    }

    @Override
    public ScalaMethod getCompileMethod(ClassLoader cl) throws ClassNotFoundException {
        // We could do this in Java, which would be easier. However, Twirl only has a Java interface in version 1.3+
        // If we used Java here then Gradle's TwirlCompiler would need to support both ScalaMethod for Twirl 1.0-1.2 and Java's Method for Twirl 1.3+
        // Method definition: https://github.com/playframework/twirl/blob/1.3.12/compiler/src/main/scala/play/twirl/compiler/TwirlCompiler.scala#L167
        return ScalaReflectionUtil.scalaMethod(
                cl,
                "play.twirl.compiler.TwirlCompiler",
                "compile",
                File.class,
                File.class,
                File.class,
                String.class,
                cl.loadClass("scala.collection.Seq"),
                cl.loadClass("scala.collection.Seq"),
                cl.loadClass(ScalaCodecMapper.getClassName()),
                boolean.class
        );
    }

    @Override
    public Object[] createCompileParameters(ClassLoader cl, File file, File sourceDirectory, File destinationDirectory, TwirlImports defaultPlayImports, TwirlTemplateFormat templateFormat, List<String> additionalImports, List<String> constructorAnnotations) {
        final List<String> defaultImports = new ArrayList<String>(DEFAULT_TEMPLATE_IMPORTS);
        defaultImports.addAll(playTwirlAdapter.getDefaultImports(defaultPlayImports));
        return new Object[]{
                file,
                sourceDirectory,
                destinationDirectory,
                templateFormat.getFormatType(),
                toScalaSeq(CollectionUtils.flattenCollections(defaultImports, additionalImports, templateFormat.getTemplateImports()), cl),
                toScalaSeq(constructorAnnotations, cl),
                ScalaCodecMapper.create(cl, "UTF-8"),
                isInclusiveDots(),
        };
    }

    private Object toScalaSeq(Collection<?> list, ClassLoader classLoader) {
        return ScalaSeq.fromList(classLoader, list);
    }

    @Override
    public Iterable<String> getClassLoaderPackages() {
        return SHARED_PACKAGES;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getDependencyNotation() {
        VersionNumber scalaAsVersion = VersionNumber.parse(scalaVersion);
        if (scalaAsVersion.compareTo(VersionNumber.parse("2.12")) >= 0) {
            // We need scala.util.parsing.input.Positional
            return (List<String>) CollectionUtils.flattenCollections(super.getDependencyNotation(), "org.scala-lang.modules:scala-parser-combinators_" + scalaVersion + ":1.1.2");
        } else {
            return super.getDependencyNotation();
        }
    }

}
