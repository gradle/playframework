package org.gradle.playframework.fixtures.ide

abstract class IdeWorkspaceFixture {
    abstract void assertContains(IdeProjectFixture project);
}
