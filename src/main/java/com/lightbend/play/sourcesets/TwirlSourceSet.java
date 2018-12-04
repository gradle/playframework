package com.lightbend.play.sourcesets;

import com.lightbend.play.tools.twirl.TwirlImports;
import com.lightbend.play.tools.twirl.TwirlTemplateFormat;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;

import java.util.List;

public interface TwirlSourceSet {

    SourceDirectorySet getTwirl();
    TwirlSourceSet twirl(Action<? super SourceDirectorySet> configureAction);
    TwirlImports getDefaultImports();
    void setDefaultImports(TwirlImports defaultImports);
    List<TwirlTemplateFormat> getUserTemplateFormats();
    void setUserTemplateFormats(List<TwirlTemplateFormat> userTemplateFormats);
    void addUserTemplateFormat(final String extension, String templateType, String... imports);
    List<String> getAdditionalImports();
    void setAdditionalImports(List<String> additionalImports);
}
