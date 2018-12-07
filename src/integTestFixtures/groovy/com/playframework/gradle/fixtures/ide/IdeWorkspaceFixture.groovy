package com.playframework.gradle.fixtures.ide

abstract class IdeWorkspaceFixture {
    abstract void assertContains(IdeProjectFixture project);
}
