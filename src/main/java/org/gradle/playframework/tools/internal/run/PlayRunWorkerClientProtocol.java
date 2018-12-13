package org.gradle.playframework.tools.internal.run;

public interface PlayRunWorkerClientProtocol {
    void update(PlayAppLifecycleUpdate result);
}
