package com.playframework.gradle.sourcesets;

import com.playframework.gradle.tools.twirl.TwirlImports;
import com.playframework.gradle.tools.twirl.TwirlTemplateFormat;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.Provider;

import java.util.List;

public interface TwirlSourceSet {

    SourceDirectorySet getTwirl();
    TwirlSourceSet twirl(Action<? super SourceDirectorySet> configureAction);
    Provider<TwirlImports> getDefaultImports();
    void setDefaultImports(Provider<TwirlImports> defaultImports);
    void setDefaultImports(TwirlImports defaultImports);
    Provider<List<TwirlTemplateFormat>> getUserTemplateFormats();
    void setUserTemplateFormats(Provider<List<TwirlTemplateFormat>> userTemplateFormats);
    void setUserTemplateFormats(List<TwirlTemplateFormat> userTemplateFormats);
    void addUserTemplateFormat(final String extension, String templateType, String... imports);
    Provider<List<String>> getAdditionalImports();
    void setAdditionalImports(Provider<List<String>> additionalImports);
    void setAdditionalImports(List<String> additionalImports);
}
