package org.gradle.playframework.fixtures.test

interface TestExecutionResult {
    /**
     * Asserts that the given test classes (and only the given test classes) were executed.
     */
    TestExecutionResult assertTestClassesExecuted(String... testClasses);

    /**
     * Returns the result for the given test class.
     */
    TestClassExecutionResult testClass(String testClass);
}

