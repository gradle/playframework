package com.lightbend.play.fixtures.wait

import org.gradle.internal.hash.HashUtil
import org.gradle.util.TextUtil

class UrlValidator {

    static void available(String theUrl, String application = "service", int timeout = 30) {
        URL url = new URL(theUrl)
        try {
            ConcurrentTestUtil.poll(timeout) {
                assertUrlIsAvailable(url)
            }
        } catch(Throwable t) {
            throw new RuntimeException(String.format("Timeout waiting for %s to become available at [%s].", application, theUrl), t);
        }
    }

    // Throws IOException if URL is unavailable
    private static assertUrlIsAvailable(URL url) {
        assert url.text != null
    }

    /**
     * Asserts that the content at the specified url matches the content in the provided String
     */
    static void assertUrlContentContains(URL url, String contents) {
        assert url.text.contains(contents)
    }

    /**
     * Asserts that the content at the specified url matches the content in the provided String
     */
    static void assertUrlContent(URL url, String contents) {
        assert TextUtil.normaliseLineSeparators(url.text) == TextUtil.normaliseLineSeparators(contents)
    }

    /**
     * Asserts that the content at the specified url matches the content in the specified File
     */
    static void assertUrlContent(URL url, File file) {
        assertUrlContent(url, file.text)
    }

    /**
     * Asserts that the binary content at the specified url matches the content in the specified File
     */
    static void assertBinaryUrlContent(URL url, File file) {
        assert compareHashes(url.openStream(), file.newInputStream())
    }

    private static boolean compareHashes(InputStream a, InputStream b) {
        return HashUtil.createHash(a, "MD5").equals(HashUtil.createHash(b, "MD5"))
    }
}
