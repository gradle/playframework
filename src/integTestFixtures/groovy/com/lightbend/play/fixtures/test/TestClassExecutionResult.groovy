package com.lightbend.play.fixtures.test

interface TestClassExecutionResult {
    TestClassExecutionResult assertTestCount(int tests, int failures, int errors);
}

