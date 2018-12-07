package com.playframework.gradle.tools.run;

import org.gradle.internal.reflect.JavaReflectionUtil;
import org.gradle.scala.internal.reflect.ScalaMethod;
import org.gradle.scala.internal.reflect.ScalaReflectionUtil;

import java.net.InetSocketAddress;

public class PlayRunAdapterV24X extends PlayRunAdapterV23X {
    @Override
    public InetSocketAddress runDevHttpServer(ClassLoader classLoader, ClassLoader docsClassLoader, Object buildLink, Object buildDocHandler, int httpPort) throws ClassNotFoundException {
        ScalaMethod runMethod = ScalaReflectionUtil.scalaMethod(classLoader, "play.core.server.DevServerStart", "mainDevHttpMode", getBuildLinkClass(classLoader), getBuildDocHandlerClass(docsClassLoader), int.class, String.class);
        Object reloadableServer = runMethod.invoke(buildLink, buildDocHandler, httpPort, "0.0.0.0");
        return JavaReflectionUtil.method(reloadableServer, InetSocketAddress.class, "mainAddress").invoke(reloadableServer);
    }
}