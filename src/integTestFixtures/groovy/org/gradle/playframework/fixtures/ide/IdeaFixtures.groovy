package org.gradle.playframework.fixtures.ide

class IdeaFixtures {
    static parseFile(File file) {
        assert file.isFile()
        new XmlSlurper().parse(file)
    }

    static IdeaProjectFixture parseIpr(File projectFile) {
        return new IdeaProjectFixture(projectFile, parseFile(projectFile))
    }

    static IdeaModuleFixture parseIml(File moduleFile) {
        return new IdeaModuleFixture(moduleFile, parseFile(moduleFile))
    }

    private IdeaFixtures() {}
}
