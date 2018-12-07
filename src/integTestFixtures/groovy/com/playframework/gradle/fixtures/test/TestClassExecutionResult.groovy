package com.playframework.gradle.fixtures.test

interface TestClassExecutionResult {
    TestClassExecutionResult assertTestCount(int tests, int failures, int errors);
}

