package com.playframework.gradle.sourcesets;

import com.playframework.gradle.tools.twirl.DefaultTwirlTemplateFormat;
import com.playframework.gradle.tools.twirl.TwirlImports;
import com.playframework.gradle.tools.twirl.TwirlTemplateFormat;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class DefaultTwirlSourceSet implements TwirlSourceSet {

    private final SourceDirectorySet twirl;
    private final Property<TwirlImports> defaultImports;
    private final ListProperty<TwirlTemplateFormat> userTemplateFormats;
    private final ListProperty<String> additionalImports;

    @Inject
    public DefaultTwirlSourceSet(String name, String displayName, ObjectFactory objectFactory) {
        twirl = objectFactory.sourceDirectorySet(name, displayName +  " Twirl source");
        twirl.srcDirs("app");
        twirl.include("**/*.scala.*");
        defaultImports = objectFactory.property(TwirlImports.class);
        defaultImports.set(TwirlImports.SCALA);
        userTemplateFormats = objectFactory.listProperty(TwirlTemplateFormat.class).empty();
        additionalImports = objectFactory.listProperty(String.class).empty();
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
    public Provider<TwirlImports> getDefaultImports() {
        return defaultImports;
    }

    @Override
    public void setDefaultImports(Provider<TwirlImports> defaultImports) {
        this.defaultImports.set(defaultImports);
    }

    @Override
    public void setDefaultImports(TwirlImports defaultImports) {
        this.defaultImports.set(defaultImports);
    }

    @Override
    public Provider<List<TwirlTemplateFormat>> getUserTemplateFormats() {
        return userTemplateFormats;
    }

    @Override
    public void setUserTemplateFormats(Provider<List<TwirlTemplateFormat>> userTemplateFormats) {
        this.userTemplateFormats.set(userTemplateFormats);
    }

    @Override
    public void setUserTemplateFormats(List<TwirlTemplateFormat> userTemplateFormats) {
        this.userTemplateFormats.set(userTemplateFormats);
    }

    @Override
    public void addUserTemplateFormat(final String extension, String templateType, String... imports) {
        userTemplateFormats.add(new DefaultTwirlTemplateFormat(extension, templateType, Arrays.asList(imports)));
    }

    @Override
    public Provider<List<String>> getAdditionalImports() {
        return additionalImports;
    }

    @Override
    public void setAdditionalImports(Provider<List<String>> additionalImports) {
        this.additionalImports.set(additionalImports);
    }

    @Override
    public void setAdditionalImports(List<String> additionalImports) {
        this.additionalImports.set(additionalImports);
    }
}
