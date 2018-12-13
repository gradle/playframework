package org.gradle.playframework.fixtures.test

import static org.hamcrest.Matchers.*
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

class JUnitXmlTestExecutionResult implements TestExecutionResult {
    private final File testResultsDir
    private final TestResultOutputAssociation outputAssociation

    JUnitXmlTestExecutionResult(File projectDir, String testResultsDir = 'build/test-results/test') {
        this(projectDir, TestResultOutputAssociation.WITH_SUITE, testResultsDir)
    }

    JUnitXmlTestExecutionResult(File projectDir, TestResultOutputAssociation outputAssociation, String testResultsDir = 'build/test-results/test') {
        this.outputAssociation = outputAssociation
        this.testResultsDir = new File(projectDir, testResultsDir)
    }

    TestExecutionResult assertTestClassesExecuted(String... testClasses) {
        Map<String, File> classes = findClasses()
        assertThat(classes.keySet(), equalTo(testClasses as Set))
        this
    }

    String fromFileToTestClass(File junitXmlFile) {
        def xml = new XmlSlurper().parse(junitXmlFile)
        xml.@'name'.text()
    }

    TestClassExecutionResult testClass(String testClass) {
        return new JUnitTestClassExecutionResult(findTestClass(testClass), testClass, outputAssociation)
    }

    private def findTestClass(String testClass) {
        def classes = findClasses()
        assertThat(classes.keySet(), hasItem(testClass))
        def classFile = classes.get(testClass)
        assertThat(classFile, notNullValue())
        return new XmlSlurper().parse(classFile)
    }

    private def findClasses() {
        assertTrue(testResultsDir.isDirectory())

        Map<String, File> classes = [:]
        testResultsDir.eachFile { File file ->
            def matcher = (file.name=~/TEST-(.+)\.xml/)
            if (matcher.matches()) {
                classes[fromFileToTestClass(file)] = file
            }
        }
        return classes
    }
}
