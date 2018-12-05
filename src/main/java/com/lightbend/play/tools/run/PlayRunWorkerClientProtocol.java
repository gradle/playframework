package com.lightbend.play.tools.run;

public interface PlayRunWorkerClientProtocol {
    void update(PlayAppLifecycleUpdate result);
}
