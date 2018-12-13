package org.gradle.playframework.fixtures.test

import groovy.util.slurpersupport.GPathResult

class JUnitTestClassExecutionResult implements TestClassExecutionResult {
    GPathResult testClassNode
    String testClassName
    TestResultOutputAssociation outputAssociation

    JUnitTestClassExecutionResult(GPathResult testClassNode, String testClassName, TestResultOutputAssociation outputAssociation) {
        this.outputAssociation = outputAssociation
        this.testClassNode = testClassNode
        this.testClassName = testClassName
    }

    TestClassExecutionResult assertTestCount(int tests, int failures, int errors) {
        assert testClassNode.@tests == tests
        assert testClassNode.@failures == failures
        assert testClassNode.@errors == errors
        this
    }
}
