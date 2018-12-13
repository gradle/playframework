package org.gradle.playframework.sourcesets;

import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

public interface TwirlSourceSet {

    SourceDirectorySet getTwirl();
    TwirlSourceSet twirl(Action<? super SourceDirectorySet> configureAction);
    Property<TwirlImports> getDefaultImports();
    ListProperty<TwirlTemplateFormat> getUserTemplateFormats();
    TwirlTemplateFormat newUserTemplateFormat(final String extension, String templateType, String... imports);
    ListProperty<String> getAdditionalImports();
}
