package com.playframework.gradle.tools.twirl;

import org.gradle.api.internal.file.RelativeFile;
import org.gradle.api.tasks.compile.BaseForkOptions;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class DefaultTwirlCompileSpec implements TwirlCompileSpec {
    private final Iterable<RelativeFile> sources;
    private final File destinationDir;
    private final Collection<TwirlTemplateFormat> userTemplateFormats;
    private final List<String> additionalImports;
    private BaseForkOptions forkOptions;
    private TwirlImports defaultImports;

    public DefaultTwirlCompileSpec(Iterable<RelativeFile> sources, File destinationDir, BaseForkOptions forkOptions, TwirlImports defaultImports, Collection<TwirlTemplateFormat> userTemplateFormats, List<String> additionalImports) {
        this.sources = sources;
        this.destinationDir = destinationDir;
        this.forkOptions = forkOptions;
        this.defaultImports = defaultImports;
        this.userTemplateFormats = userTemplateFormats;
        this.additionalImports = additionalImports;
    }

    @Override
    public TwirlImports getDefaultImports() {
        return defaultImports;
    }

    @Override
    public Collection<TwirlTemplateFormat> getUserTemplateFormats() {
        return userTemplateFormats;
    }

    @Override
    public File getDestinationDir() {
        return destinationDir;
    }

    @Override
    public Iterable<RelativeFile> getSources() {
        return sources;
    }

    @Override
    public BaseForkOptions getForkOptions() {
        return forkOptions;
    }

    @Override
    public List<String> getAdditionalImports() {
        return additionalImports;
    }
}
