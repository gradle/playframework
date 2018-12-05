package com.lightbend.play.fixtures.app

import com.lightbend.play.fixtures.wait.ConcurrentTestUtil

import static com.lightbend.play.fixtures.wait.UrlValidator.*

class RunningPlayApp {

    static final int UNASSIGNED = -1
    int httpPort = UNASSIGNED
    private final File testDirectory

    RunningPlayApp(File testDirectory) {
        this.testDirectory = testDirectory
    }

    URL playUrl(String path='') {
        requireHttpPort()
        return new URL("http://localhost:$httpPort/${path}")
    }

    void requireHttpPort() {
        requireHttpPort(0)
    }

    void requireHttpPort(int occurence) {
        if (httpPort == UNASSIGNED) {
            if (parseHttpPort(occurence) == UNASSIGNED) {
                throw new IllegalStateException("Could not parse Play http port from gradle output!")
            }
        }
    }

    static int parseHttpPort(output, regex, int occurrence) {
        def matcher = output =~ regex
        if (matcher.count >= occurrence + 1) {
            return matcher[occurrence][1] as int
        }
        return UNASSIGNED
    }

    void waitForStarted(int occurrence = 0) {
        int timeout = 120
        ConcurrentTestUtil.poll(timeout) {
            assert parseHttpPort(occurrence) != UNASSIGNED : "Could not parse Play http port from spec output after ${timeout} seconds"
        }
    }

    void verifyStarted(String path = '', int occurrence = 0) {
        waitForStarted(occurrence)
        assert playUrl(path).text.contains("Your new application is ready.")
    }

    void verifyContent() {
        // Check all static assets from the shared content
        assertUrlContent playUrl("assets/stylesheets/main.css"), new File(testDirectory, "public/stylesheets/main.css")
        assertUrlContent playUrl("assets/javascripts/hello.js"), new File(testDirectory, "public/javascripts/hello.js")
        assertBinaryUrlContent playUrl("assets/images/favicon.svg"), new File(testDirectory, "public/images/favicon.svg")
    }
}
