package com.playframework.gradle.tools.internal.twirl;

import com.playframework.gradle.sourcesets.TwirlImports;
import com.playframework.gradle.sourcesets.TwirlTemplateFormat;
import org.gradle.api.internal.file.RelativeFile;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class DefaultTwirlCompileSpec implements TwirlCompileSpec {
    private final Iterable<RelativeFile> sources;
    private final File destinationDir;
    private final Collection<TwirlTemplateFormat> userTemplateFormats;
    private final List<String> additionalImports;
    private TwirlImports defaultImports;

    public DefaultTwirlCompileSpec(Iterable<RelativeFile> sources, File destinationDir, TwirlImports defaultImports, Collection<TwirlTemplateFormat> userTemplateFormats, List<String> additionalImports) {
        this.sources = sources;
        this.destinationDir = destinationDir;
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
    public List<String> getAdditionalImports() {
        return additionalImports;
    }
}
