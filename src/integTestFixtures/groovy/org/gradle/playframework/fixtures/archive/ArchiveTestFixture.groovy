package org.gradle.playframework.fixtures.archive

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.LinkedListMultimap
import com.google.common.collect.ListMultimap
import org.hamcrest.Matcher

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

    String content(String relativePath) {
        List<String> files = filesByRelativePath.get(relativePath)
        assert files.size() == 1
        files.get(0)
    }

    Integer countFiles(String relativePath) {
        filesByRelativePath.get(relativePath).size()
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

    def doesNotContainDescendants(String... relativePaths) {
        for (String path : relativePaths) {
            assertNotContainsFile(path)
        }
        this
    }

    def assertNotContainsFile(String relativePath) {
        assert !filesByRelativePath.keySet().contains(relativePath)
        this
    }

    /**
     * Asserts that there is exactly one file present with the given path, and that this file has the given content.
     */
    def assertFileContent(String relativePath, String fileContent) {
        assertFileContent(relativePath, equalTo(fileContent))
    }

    def assertFileContent(String relativePath, Matcher contentMatcher) {
        assertThat(content(relativePath), contentMatcher)
        this
    }
}
