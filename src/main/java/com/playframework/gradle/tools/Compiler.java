package com.playframework.gradle.tools;

import org.gradle.api.tasks.WorkResult;

public interface Compiler<T extends CompileSpec> {
    WorkResult execute(T spec);
}
