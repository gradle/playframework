package org.gradle.playframework.tasks.internal;

import org.gradle.playframework.tools.internal.webjars.WebJarsExtractSpec;
import org.gradle.playframework.tools.internal.webjars.WebJarsExtractor;
import org.gradle.util.GFileUtils;

import javax.inject.Inject;

public class WebJarsExtractRunnable implements Runnable {
    private final WebJarsExtractSpec spec;

    @Inject
    public WebJarsExtractRunnable(WebJarsExtractSpec spec) {
        this.spec = spec;
    }

    @Override
    public void run() {
        GFileUtils.forceDelete(spec.getDestinationDir());
        new WebJarsExtractor().execute(spec);
    }
}
