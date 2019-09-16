package org.gradle.playframework.tools.internal.twirl;

import org.gradle.playframework.sourcesets.TwirlImports;
import org.gradle.playframework.sourcesets.TwirlTemplateFormat;
import org.gradle.playframework.sourcesets.internal.DefaultTwirlTemplateFormat;
import org.gradle.playframework.tools.internal.scala.ScalaCodecMapper;
import org.gradle.playframework.tools.internal.scala.ScalaMethod;
import org.gradle.playframework.tools.internal.scala.ScalaReflectionUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class TwirlCompilerAdapterV10X extends VersionedTwirlCompilerAdapter {

    private static final Iterable<String> SHARED_PACKAGES = Arrays.asList("play.twirl.compiler", "scala.io"); //scala.io is for Codec which is a parameter to twirl

    protected final String scalaVersion;
    protected final String twirlVersion;
    protected final VersionedPlayTwirlAdapter playTwirlAdapter;

    public TwirlCompilerAdapterV10X(String twirlVersion, String scalaVersion, VersionedPlayTwirlAdapter playTwirlAdapter) {
        this.scalaVersion = scalaVersion;
        this.twirlVersion = twirlVersion;
        this.playTwirlAdapter = playTwirlAdapter;
    }

    @Override
    public ScalaMethod getCompileMethod(ClassLoader cl) throws ClassNotFoundException {
        return ScalaReflectionUtil.scalaMethod(
                cl,
                "play.twirl.compiler.TwirlCompiler",
                "compile",
                File.class,
                File.class,
                File.class,
                String.class,
                String.class,
                cl.loadClass(ScalaCodecMapper.getClassName()),
                boolean.class,
                boolean.class
        );
    }

    @Override
    public Object[] createCompileParameters(ClassLoader cl, File file, File sourceDirectory, File destinationDirectory, TwirlImports defaultPlayImports, TwirlTemplateFormat templateFormat, List<String> additionalImports, List<String> constructorAnnotations) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        assert constructorAnnotations.isEmpty();
        final Collection<String> defaultImports = playTwirlAdapter.getDefaultImports(defaultPlayImports);
        return new Object[] {
                file,
                sourceDirectory,
                destinationDirectory,
                templateFormat.getFormatType(),
                getImportsFor(templateFormat, defaultImports, additionalImports),
                ScalaCodecMapper.create(cl, "UTF-8"),
                isInclusiveDots(),
                isUseOldParser()
        };
    }

    protected boolean isInclusiveDots() {
        return false;
    }

    private boolean isUseOldParser() {
        return false;
    }

    @Override
    public Iterable<String> getClassLoaderPackages() {
        return SHARED_PACKAGES;
    }

    @Override
    public List<String> getDependencyNotation() {
        return Collections.singletonList("com.typesafe.play:twirl-compiler_" + scalaVersion + ":" + twirlVersion);
    }

    @Override
    public Collection<TwirlTemplateFormat> getDefaultTemplateFormats() {
        return Arrays.<TwirlTemplateFormat>asList(
                new DefaultTwirlTemplateFormat("html", "play.twirl.api.HtmlFormat", Collections.singleton("views.html._")),
                new DefaultTwirlTemplateFormat("txt", "play.twirl.api.TxtFormat", Collections.singleton("views.txt._")),
                new DefaultTwirlTemplateFormat("xml", "play.twirl.api.XmlFormat", Collections.singleton("views.xml._")),
                new DefaultTwirlTemplateFormat("js", "play.twirl.api.JavaScriptFormat", Collections.singleton("views.js._"))
        );
    }
}
