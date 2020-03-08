package org.gradle.playframework.tools.internal.webjars;

import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.WorkResults;
import org.gradle.playframework.tools.internal.reflection.DirectInstantiator;
import org.gradle.playframework.tools.internal.reflection.JavaMethod;
import org.gradle.playframework.tools.internal.reflection.JavaReflectionUtil;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class WebJarsExtractor implements Serializable  {

    private Class<Object> extractorClass;

    public static Object getDependencyNotation() {
        return "org.webjars:webjars-locator-core:0.32";
    }

    public WorkResult execute(WebJarsExtractSpec spec) {
        List<URL> urls = new ArrayList<>();

        spec.getClasspath().forEach(file -> {
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });

        URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));

        loadCompilerClasses(getClass().getClassLoader());

        Object extractor = DirectInstantiator.INSTANCE.newInstance(extractorClass, classLoader);

        JavaMethod<Object, Void> extractAllWebJarsTo = JavaReflectionUtil.method(extractorClass, Void.class, "extractAllWebJarsTo", File.class);
        extractAllWebJarsTo.invoke(extractor, new File(spec.getDestinationDir(), "lib"));

        JavaMethod<Object, Void> extractAllNodeModulesTo = JavaReflectionUtil.method(extractorClass, Void.class, "extractAllNodeModulesTo", File.class);
        extractAllNodeModulesTo.invoke(extractor, new File(spec.getDestinationDir(), "lib"));

        return WorkResults.didWork(true);
    }

    private void loadCompilerClasses(ClassLoader cl) {
        try {
            if (extractorClass == null) {
                @SuppressWarnings("unchecked")
                Class<Object> clazz = (Class<Object>) cl.loadClass("org.webjars.WebJarExtractor");
                extractorClass = clazz;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load classes for WebJarsExtractor", e);
        }
    }
}
