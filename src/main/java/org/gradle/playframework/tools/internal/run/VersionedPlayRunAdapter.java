package org.gradle.playframework.tools.internal.run;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public interface VersionedPlayRunAdapter {
    Object getBuildLink(ClassLoader classLoader, Reloader reloader, File projectPath, File applicationJar, Iterable<File> changingClasspath, File assetsJar, Iterable<File> assetsDirs) throws ClassNotFoundException;

    Object getBuildDocHandler(ClassLoader docsClassLoader, Iterable<File> classpath) throws NoSuchMethodException, ClassNotFoundException, IOException, IllegalAccessException;

    InetSocketAddress runDevHttpServer(ClassLoader classLoader, ClassLoader docsClassLoader, Object buildLink, Object buildDocHandler, int httpPort) throws ClassNotFoundException;
}
