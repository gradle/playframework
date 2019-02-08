package org.gradle.playframework.tools.internal.routes;

import org.gradle.playframework.tools.internal.scala.ScalaMethod;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public interface VersionedRoutesCompilerAdapter extends Serializable {
    String getDependencyNotation();

    ScalaMethod getCompileMethod(ClassLoader cl) throws ClassNotFoundException;

    Object[] createCompileParameters(ClassLoader cl, File file, File destinationDir, boolean javaProject, boolean namespaceReverseRouter, boolean generateReverseRoutes, boolean injectedRoutesGenerator, Collection<String> additionalImports) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    Iterable<String> getClassLoaderPackages();

    Boolean interpretResult(Object result) throws ClassNotFoundException;
}
