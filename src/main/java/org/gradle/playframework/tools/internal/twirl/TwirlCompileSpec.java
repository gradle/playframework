package org.gradle.playframework.tools.internal.twirl;

import org.gradle.playframework.sourcesets.TwirlImports;
import org.gradle.playframework.sourcesets.TwirlTemplateFormat;
import org.gradle.playframework.tools.internal.PlayCompileSpec;
import org.gradle.api.internal.file.RelativeFile;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface TwirlCompileSpec extends PlayCompileSpec, Serializable {
    Iterable<RelativeFile> getSources();

    TwirlImports getDefaultImports();

    Collection<TwirlTemplateFormat> getUserTemplateFormats();

    List<String> getAdditionalImports();

    List<String> getConstructorAnnotations();
}
