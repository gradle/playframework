package org.gradle.playframework.tools.internal.run;

public interface Reloader {
    Result requireUpToDate() throws InterruptedException;

    class Result {
        Throwable failure;
        boolean changed;
        Result(boolean changed, Throwable failure) {
            this.changed = changed;
            this.failure = failure;
        }

        @Override
        public String toString() {
            return "Result{"
                    + "failure=" + failure
                    + ", changed=" + changed
                    + '}';
        }
    }
}