package org.gradle.playframework.sourcesets.internal;

import org.gradle.playframework.sourcesets.TwirlSourceSet;
import org.gradle.playframework.sourcesets.TwirlImports;
import org.gradle.playframework.sourcesets.TwirlTemplateFormat;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import javax.inject.Inject;
import java.util.Arrays;

public class DefaultTwirlSourceSet implements TwirlSourceSet {

    private final SourceDirectorySet twirl;
    private final Property<TwirlImports> defaultImports;
    private final ListProperty<TwirlTemplateFormat> userTemplateFormats;
    private final ListProperty<String> additionalImports;
    private final ListProperty<String> constructorAnnotations;

    @Inject
    public DefaultTwirlSourceSet(String name, String displayName, ObjectFactory objectFactory) {
        twirl = objectFactory.sourceDirectorySet(name, displayName + " Twirl source");
        twirl.srcDirs("app");
        twirl.include("**/*.scala.*");
        defaultImports = objectFactory.property(TwirlImports.class);
        defaultImports.set(TwirlImports.SCALA);
        userTemplateFormats = objectFactory.listProperty(TwirlTemplateFormat.class).empty();
        additionalImports = objectFactory.listProperty(String.class).empty();
        constructorAnnotations = objectFactory.listProperty(String.class).empty();
    }

    @Override
    public SourceDirectorySet getTwirl() {
        return twirl;
    }

    @Override
    public TwirlSourceSet twirl(Action<? super SourceDirectorySet> configureAction) {
        configureAction.execute(getTwirl());
        return this;
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
    public TwirlTemplateFormat newUserTemplateFormat(final String extension, String templateType, String... imports) {
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
