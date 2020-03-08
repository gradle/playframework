package org.gradle.playframework.tools.internal.less;

import org.gradle.api.file.RelativePath;
import org.gradle.api.internal.file.RelativeFile;
import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.WorkResults;
import org.gradle.playframework.tools.internal.Compiler;
import org.gradle.playframework.tools.internal.reflection.DirectInstantiator;
import org.gradle.playframework.tools.internal.reflection.JavaMethod;
import org.gradle.playframework.tools.internal.reflection.JavaReflectionUtil;
import org.gradle.util.GFileUtils;

import java.io.File;
import java.io.Serializable;

public class Less4jCompiler implements Compiler<LessCompileSpec>, Serializable {

    private Class<Object> sourceClass;
    private Class<Object> multiSourceClass;
    private Class<Object> configurationClass;
    private Class<Object> resultClass;
    private Class<Object> compilerClass;

    public static Object getDependencyNotation() {
        return "com.github.sommeri:less4j:1.17.2";
    }

    @Override
    public WorkResult execute(LessCompileSpec spec) {
        boolean didWork = false;
        File[] includePaths = spec.getIncludePaths().toArray(new File[0]);
        for (RelativeFile lessFile : spec.getSources()) {
            File cssFile = new File(
                    spec.getDestinationDir(),
                    toCss(lessFile.getRelativePath()).getPathString());

            didWork |= compile(lessFile.getFile(), cssFile, includePaths);
        }
        return WorkResults.didWork(didWork);
    }

    private boolean compile(File lessFile, File cssFile, File[] includePaths) {
        loadCompilerClasses(getClass().getClassLoader());

        Object lessSource = DirectInstantiator.INSTANCE.newInstance(multiSourceClass, lessFile, includePaths);
        Object options = DirectInstantiator.INSTANCE.newInstance(configurationClass);

        JavaMethod<Object, Void> setCssResultLocation = JavaReflectionUtil.method(configurationClass, Void.class, "setCssResultLocation", File.class);
        setCssResultLocation.invoke(options, cssFile);

        JavaMethod<Object, Object> setCompressing = JavaReflectionUtil.method(configurationClass, configurationClass, "setCompressing", boolean.class);
        setCompressing.invoke(options, true);

        Object compiler = DirectInstantiator.INSTANCE.newInstance(compilerClass);

        JavaMethod<Object, Object> doCompile = JavaReflectionUtil.method(compilerClass, Object.class, "compile", sourceClass, configurationClass);
        Object result = doCompile.invoke(compiler, lessSource, options);

        JavaMethod<Object, String> getCss = JavaReflectionUtil.method(resultClass, String.class, "getCss");
        String css = getCss.invoke(result);

        GFileUtils.writeFile(css, cssFile);
        return true;
    }

    private void loadCompilerClasses(ClassLoader cl) {
        try {
            if (sourceClass == null) {
                @SuppressWarnings("unchecked")
                Class<Object> clazz = (Class<Object>) cl.loadClass("com.github.sommeri.less4j.LessSource");
                sourceClass = clazz;
            }
            if (multiSourceClass == null) {
                @SuppressWarnings("unchecked")
                Class<Object> clazz = (Class<Object>) cl.loadClass("com.github.sommeri.less4j.MultiPathFileSource");
                multiSourceClass = clazz;
            }
            if (configurationClass == null) {
                @SuppressWarnings("unchecked")
                Class<Object> clazz = (Class<Object>) cl.loadClass("com.github.sommeri.less4j.LessCompiler$Configuration");
                configurationClass = clazz;
            }
            if (resultClass == null) {
                @SuppressWarnings("unchecked")
                Class<Object> clazz = (Class<Object>) cl.loadClass("com.github.sommeri.less4j.LessCompiler$CompilationResult");
                resultClass = clazz;
            }
            if (compilerClass == null) {
                @SuppressWarnings("unchecked")
                Class<Object> clazz = (Class<Object>) cl.loadClass("com.github.sommeri.less4j.core.ThreadUnsafeLessCompiler");
                compilerClass = clazz;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load classes for Less4jCompiler", e);
        }
    }

    private static RelativePath toCss(RelativePath path) {
        String lessFilename = path.getLastName();
        String cssFilename;
        if (lessFilename.endsWith(".less")) {
            cssFilename = lessFilename.substring(0, lessFilename.length() - ".less".length()) + ".css";
        } else {
            cssFilename = lessFilename + ".css";
        }
        return path.replaceLastName(cssFilename);
    }
}
