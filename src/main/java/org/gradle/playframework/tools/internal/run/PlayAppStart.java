package org.gradle.playframework.tools.internal.run;

import java.net.InetSocketAddress;

public class PlayAppStart extends PlayAppLifecycleUpdate {
    private final InetSocketAddress address;
    private final Exception exception;

    @Override
    public String toString() {
        return "PlayAppStart{"
                + "address=" + address
                + ", exception=" + exception
                + '}';
    }

    public PlayAppStart(InetSocketAddress address) {
        this(address, null);
    }

    public PlayAppStart(Exception exception) {
        this(null, exception);
    }

    private PlayAppStart(InetSocketAddress address, Exception exception) {
        this.address = address;
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public boolean isRunning() {
        return address!=null && exception == null;
    }

    public boolean isFailed() {
        return exception != null;
    }
}
