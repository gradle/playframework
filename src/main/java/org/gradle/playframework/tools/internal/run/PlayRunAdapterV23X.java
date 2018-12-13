package org.gradle.playframework.tools.internal.run;

import org.gradle.util.CollectionUtils;

import java.io.File;
import java.util.List;

public class PlayRunAdapterV23X extends DefaultVersionedPlayRunAdapter {
    @Override
    protected Class<?> getBuildLinkClass(ClassLoader classLoader) throws ClassNotFoundException {
        return classLoader.loadClass("play.core.BuildLink");
    }

    @Override
    protected Class<?> getBuildDocHandlerClass(ClassLoader classLoader) throws ClassNotFoundException {
        return classLoader.loadClass("play.core.BuildDocHandler");
    }

    @Override
    protected Class<?> getDocHandlerFactoryClass(ClassLoader docsClassLoader) throws ClassNotFoundException {
        return docsClassLoader.loadClass("play.docs.BuildDocHandlerFactory");
    }

    @Override
    protected ClassLoader createAssetsClassLoader(File assetsJar, Iterable<File> assetsDirs, ClassLoader classLoader) {
        List<AssetsClassLoader.AssetDir> assetDirs = CollectionUtils.collect(assetsDirs, file -> {
            // TODO: This prefix shouldn't be hardcoded
            return new AssetsClassLoader.AssetDir("public", file);
        });
        return new AssetsClassLoader(classLoader, assetDirs);
    }
}
