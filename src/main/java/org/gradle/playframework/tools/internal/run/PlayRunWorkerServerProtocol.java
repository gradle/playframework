package org.gradle.playframework.tools.internal.run;

import org.gradle.internal.concurrent.Stoppable;

public interface PlayRunWorkerServerProtocol extends Stoppable {
    void currentStatus(Boolean hasChanged, Throwable throwable);
}
