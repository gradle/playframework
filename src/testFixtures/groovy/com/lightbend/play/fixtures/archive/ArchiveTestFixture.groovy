package com.lightbend.play.fixtures.archive

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.ListMultimap

import static org.hamcrest.Matchers.equalTo
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThat

class ArchiveTestFixture {
    private final ListMultimap<String, String> filesByRelativePath = LinkedListMultimap.create()
    private final ListMultimap<String, Integer> fileModesByRelativePath = ArrayListMultimap.create()

    protected void add(String relativePath, String content) {
        filesByRelativePath.put(relativePath, content)
    }

    protected void addMode(String relativePath, int mode) {
        fileModesByRelativePath.put(relativePath, mode & 0777)
    }

    def assertContainsFile(String relativePath) {
        assert filesByRelativePath.keySet().contains(relativePath)
        this
    }

    def hasDescendants(String... relativePaths) {
        hasDescendants(relativePaths as List)
    }

    def hasDescendants(Collection<String> relativePaths) {
        assertThat(filesByRelativePath.keySet(), equalTo(relativePaths as Set))
        def expectedCounts = ArrayListMultimap.create()
        for (String fileName : relativePaths) {
            expectedCounts.put(fileName, fileName)
        }
        for (String fileName : relativePaths) {
            assertEquals(expectedCounts.get(fileName).size(), filesByRelativePath.get(fileName).size())
        }
        this
    }

    def containsDescendants(String... relativePaths) {
        for (String path : relativePaths) {
            assertContainsFile(path)
        }
        this
    }
}
