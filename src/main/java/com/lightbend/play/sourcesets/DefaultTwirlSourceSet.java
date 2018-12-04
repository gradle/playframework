package com.lightbend.play.sourcesets;

import com.lightbend.play.tools.twirl.DefaultTwirlTemplateFormat;
import com.lightbend.play.tools.twirl.TwirlImports;
import com.lightbend.play.tools.twirl.TwirlTemplateFormat;
import org.gradle.api.Action;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultTwirlSourceSet implements TwirlSourceSet {

    private final SourceDirectorySet twirl;
    private TwirlImports defaultImports = TwirlImports.SCALA;
    private List<TwirlTemplateFormat> userTemplateFormats = new ArrayList<>();
    private List<String> additionalImports = new ArrayList<>();

    @Inject
    public DefaultTwirlSourceSet(String name, String displayName, ObjectFactory objectFactory) {
        twirl = objectFactory.sourceDirectorySet(name, displayName +  " Twirl source");
        twirl.srcDirs("app");
        twirl.include("**/*.scala.*");
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
    public TwirlImports getDefaultImports() {
        return defaultImports;
    }

    @Override
    public void setDefaultImports(TwirlImports defaultImports) {
        this.defaultImports = defaultImports;
    }

    @Override
    public List<TwirlTemplateFormat> getUserTemplateFormats() {
        return userTemplateFormats;
    }

    @Override
    public void setUserTemplateFormats(List<TwirlTemplateFormat> userTemplateFormats) {
        this.userTemplateFormats = userTemplateFormats;
    }

    @Override
    public void addUserTemplateFormat(final String extension, String templateType, String... imports) {
        userTemplateFormats.add(new DefaultTwirlTemplateFormat(extension, templateType, Arrays.asList(imports)));
    }

    @Override
    public List<String> getAdditionalImports() {
        return additionalImports;
    }

    @Override
    public void setAdditionalImports(List<String> additionalImports) {
        this.additionalImports = additionalImports;
    }
}
