package com.lightbend.play.fixtures.ide

abstract class IdeWorkspaceFixture {
    abstract void assertContains(IdeProjectFixture project);
}
