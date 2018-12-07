package com.playframework.gradle.tools.internal.twirl;

import com.playframework.gradle.sourcesets.TwirlImports;

import java.io.Serializable;
import java.util.List;

/**
 * Returns template info for a specifc Play version.
 */
public interface VersionedPlayTwirlAdapter extends Serializable {

    List<String> getDefaultImports(TwirlImports language);

}
