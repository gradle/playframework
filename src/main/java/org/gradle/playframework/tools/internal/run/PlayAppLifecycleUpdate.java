package org.gradle.playframework.tools.internal.run;

import java.io.Serializable;
import java.net.InetSocketAddress;

abstract class PlayAppLifecycleUpdate implements Serializable {
    static PlayAppLifecycleUpdate stopped() {
        return new PlayAppStop();
    }

    static PlayAppLifecycleUpdate running(InetSocketAddress address) {
        return new PlayAppStart(address);
    }

    static PlayAppLifecycleUpdate failed(Exception exception) {
        return new PlayAppStart(exception);
    }

    static PlayAppLifecycleUpdate reloadRequested() {
        return new PlayAppReload();
    }
}
