package com.lightbend.play.fixtures.wait

final class ConcurrentTestUtil {
    private ConcurrentTestUtil() {}

    static void poll(double timeout = 10, double initialDelay = 0, Closure assertion) {
        def start = monotonicClockMillis()
        Thread.sleep(toMillis(initialDelay))
        def expiry = start + toMillis(timeout) // convert to ms
        long sleepTime = 100
        while(true) {
            try {
                assertion()
                return
            } catch (Throwable t) {
                if (monotonicClockMillis() > expiry) {
                    throw t
                }
                sleepTime = Math.min(250, (long) (sleepTime * 1.2))
                Thread.sleep(sleepTime)
            }
        }
    }

    private static long monotonicClockMillis() {
        System.nanoTime() / 1000000L
    }

    private static long toMillis(double seconds) {
        (long) (seconds * 1000)
    }
}
