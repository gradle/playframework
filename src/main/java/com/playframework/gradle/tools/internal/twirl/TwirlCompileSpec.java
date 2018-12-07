package com.playframework.gradle.tools.internal.twirl;

import com.playframework.gradle.sourcesets.TwirlImports;
import com.playframework.gradle.sourcesets.TwirlTemplateFormat;
import com.playframework.gradle.tools.internal.PlayCompileSpec;
import org.gradle.api.internal.file.RelativeFile;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface TwirlCompileSpec extends PlayCompileSpec, Serializable {
    Iterable<RelativeFile> getSources();

    TwirlImports getDefaultImports();

    Collection<TwirlTemplateFormat> getUserTemplateFormats();

    List<String> getAdditionalImports();
}
