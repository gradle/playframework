package com.lightbend.play.tools.twirl;

import com.lightbend.play.tools.PlayCompileSpec;
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
