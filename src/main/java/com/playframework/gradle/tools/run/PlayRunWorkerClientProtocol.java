package com.playframework.gradle.tools.run;

public interface PlayRunWorkerClientProtocol {
    void update(PlayAppLifecycleUpdate result);
}
