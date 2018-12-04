package com.lightbend.play.tools;

import org.gradle.api.tasks.WorkResult;

public interface Compiler<T extends CompileSpec> {
    WorkResult execute(T spec);
}
