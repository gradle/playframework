package org.gradle.playframework.tools.internal.twirl;

import org.gradle.playframework.sourcesets.TwirlImports;
import org.gradle.playframework.sourcesets.TwirlTemplateFormat;
import org.gradle.playframework.tools.internal.scala.ScalaMethod;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

public abstract class VersionedTwirlCompilerAdapter implements Serializable {
    public abstract List<String> getDependencyNotation();

    public abstract ScalaMethod getCompileMethod(ClassLoader cl) throws ClassNotFoundException;

    public abstract Object[] createCompileParameters(ClassLoader cl, File file, File sourceDirectory, File destinationDirectory, TwirlImports defaultImports, TwirlTemplateFormat templateFormat, List<String> additionalImports, List<String> constructorAnnotations) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    public abstract Iterable<String> getClassLoaderPackages();

    public abstract Collection<TwirlTemplateFormat> getDefaultTemplateFormats();

    protected String getImportsFor(TwirlTemplateFormat templateFormat, Collection<String> defaultImports, Collection<String> additionalImports) {
        StringBuilder sb = new StringBuilder();
        addImports(sb, defaultImports);
        addImports(sb, templateFormat.getTemplateImports());
        addImports(sb, additionalImports);

        return sb.toString();
    }

    private void addImports(StringBuilder sb, Collection<String> imports) {
        for(String importPackage : imports) {
            sb.append("import ").append(importPackage).append(";\n");
        }
    }
}
