package org.gradle.playframework.fixtures.ide

import groovy.util.slurpersupport.GPathResult

class IdeaProjectFixture extends IdeWorkspaceFixture {
    private final GPathResult ipr
    private final File file

    IdeaProjectFixture(File file, GPathResult ipr) {
        this.file = file
        this.ipr = ipr
    }

    String getLanguageLevel() {
        if (ipr.component.@languageLevel.size() != 0) {
            return ipr.component.@languageLevel
        }
        return null
    }

    String getJdkName() {
        if (ipr.component."@project-jdk-name".size() != 0) {
            return ipr.component."@project-jdk-name"
        }
        return null
    }

    def getBytecodeTargetLevel() {
        def compilerConfig = ipr.component.find { it.@name == "CompilerConfiguration" }
        return compilerConfig.bytecodeTargetLevel
    }

    def getLibraryTable() {
        return ipr.component.find {
            it.@name == "libraryTable"
        }
    }

    ProjectModules getModules() {
        def projectModuleManager = ipr.component.find { it.@name == "ProjectModuleManager" }
        def moduleNames = projectModuleManager.modules.module.@filepath.collect {it.text()}
        return new ProjectModules(moduleNames)
    }

    @Override
    void assertContains(IdeProjectFixture project) {
        assert project instanceof IdeaModuleFixture
        def path = project.file.relativizeFrom(file.parentFile).path
        modules.modules.contains("\$PROJECT_DIR/$path")
    }

    static class ProjectModules {
        List<String> modules

        private ProjectModules(List<String> modules) {
            this.modules = modules
        }

        int size() {
            return modules.size()
        }

        void assertHasModule(String name) {
            assert modules.any { it.endsWith(name)} : "No module with $name found in ${modules}"
        }

        void assertHasModules(String... name) {
            List<String> modules = Arrays.asList(name)
            assert this.modules.every { modules.contains(it) }
        }
    }

}
