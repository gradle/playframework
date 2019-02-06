package org.gradle.playframework.tools.internal.run;

import org.gradle.internal.UncheckedException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

class AssetsClassLoader extends ClassLoader {
    private final List<AssetDir> assetDirs;

    AssetsClassLoader(ClassLoader parent, List<AssetDir> assetDirs) {
        super(parent);
        this.assetDirs = assetDirs;
    }

    @Override
    protected URL findResource(String name) {
        AssetDir assetDir = findResourceInAssetDir(name);
        if (assetDir != null) {
            return assetDir.toURL(name);
        }
        return null;
    }

    private AssetDir findResourceInAssetDir(final String name) {
        return assetDirs.stream()
                .filter(assetDir -> assetDir.exists(name))
                .findFirst()
                .orElse(null);
    }

    public static class AssetDir {
        private final String prefix;
        private final File dir;

        public AssetDir(String prefix, File dir) {
            this.prefix = prefix;
            this.dir = dir;
        }

        boolean exists(String name) {
            return name.startsWith(prefix) && resolve(name).isFile();
        }

        File resolve(String name) {
            return new File(dir, name.substring(prefix.length()));
        }

        URL toURL(String name) {
            try {
                return resolve(name).toURI().toURL();
            } catch (MalformedURLException e) {
                throw UncheckedException.throwAsUncheckedException(e);
            }
        }
    }
}
