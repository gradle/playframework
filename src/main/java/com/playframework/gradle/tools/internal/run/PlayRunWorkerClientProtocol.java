package com.playframework.gradle.tools.internal.run;

public interface PlayRunWorkerClientProtocol {
    void update(PlayAppLifecycleUpdate result);
}
