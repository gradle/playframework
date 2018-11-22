package com.lightbend.play.fixtures

final class AssertionHelper {

    private AssertionHelper() {}

    static File findFile(File[] files, String fileName) {
        files.find { it.name == fileName }
    }
}
