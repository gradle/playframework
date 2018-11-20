package com.lightbend.play.toolchain;

import org.gradle.util.TreeVisitor;

public interface ToolSearchResult {
    boolean isAvailable();

    /**
     * Writes some diagnostics about why the tool is not available.
     */
    void explain(TreeVisitor<? super String> visitor);
}
