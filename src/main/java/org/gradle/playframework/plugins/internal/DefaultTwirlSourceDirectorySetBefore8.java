package org.gradle.playframework.plugins.internal;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.playframework.plugins.TwirlSourceDirectorySet;
import org.gradle.playframework.sourcesets.TwirlImports;
import org.gradle.playframework.sourcesets.TwirlTemplateFormat;
import org.gradle.playframework.sourcesets.internal.DefaultTwirlTemplateFormat;

import javax.inject.Inject;
import java.util.Arrays;

public class DefaultTwirlSourceDirectorySetBefore8 extends DefaultSourceDirectorySet implements TwirlSourceDirectorySet {
    private final Property<TwirlImports> defaultImports;
    private final ListProperty<TwirlTemplateFormat> userTemplateFormats;
    private final ListProperty<String> additionalImports;
    private final ListProperty<String> constructorAnnotations;

    @Inject
    public DefaultTwirlSourceDirectorySetBefore8(SourceDirectorySet sourceSet, ObjectFactory objectFactory) {
        super(sourceSet);
        defaultImports = objectFactory.property(TwirlImports.class);
        defaultImports.set(TwirlImports.SCALA);
        userTemplateFormats = objectFactory.listProperty(TwirlTemplateFormat.class).empty();
        additionalImports = objectFactory.listProperty(String.class).empty();
        constructorAnnotations = objectFactory.listProperty(String.class).empty();
    }

    @Override
    public Property<TwirlImports> getDefaultImports() {
        return defaultImports;
    }

    @Override
    public ListProperty<TwirlTemplateFormat> getUserTemplateFormats() {
        return userTemplateFormats;
    }

    @Override
    public TwirlTemplateFormat newUserTemplateFormat(String extension, String templateType, String... imports) {
        return new DefaultTwirlTemplateFormat(extension, templateType, Arrays.asList(imports));
    }

    @Override
    public ListProperty<String> getAdditionalImports() {
        return additionalImports;
    }

    @Override
    public ListProperty<String> getConstructorAnnotations() {
        return constructorAnnotations;
    }
}
