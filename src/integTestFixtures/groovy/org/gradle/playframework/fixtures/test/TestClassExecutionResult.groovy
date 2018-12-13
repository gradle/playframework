package org.gradle.playframework.fixtures.test

interface TestClassExecutionResult {
    TestClassExecutionResult assertTestCount(int tests, int failures, int errors);
}

