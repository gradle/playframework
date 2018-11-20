package com.lightbend.play.toolchain;

import org.gradle.language.base.internal.compile.CompileSpec;
import org.gradle.language.base.internal.compile.Compiler;

public interface ToolProvider extends ToolSearchResult {
    <T extends CompileSpec> Compiler<T> newCompiler(Class<T> spec);

    <T> T get(Class<T> toolType);
}
