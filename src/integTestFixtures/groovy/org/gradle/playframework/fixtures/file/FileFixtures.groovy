package org.gradle.playframework.fixtures.file

import org.gradle.internal.hash.HashCode
import org.gradle.internal.hash.Hashing

import static org.junit.jupiter.api.Assertions.*

final class FileFixtures {

    private FileFixtures() {}

    static File findFile(File[] files, String fileName) {
        files.find { it.name == fileName }
    }

    static Snapshot snapshot(File file) {
        assertIsFile(file)
        return new Snapshot(file.lastModified(), md5(file))
    }

    static void assertHasNotChangedSince(Snapshot oldSnapshot, File file) {
        Snapshot now = snapshot(file)
        assertEquals(oldSnapshot.modTime, now.modTime)
        assertEquals(oldSnapshot.hash, now.hash)
    }

    static void assertContentsHaveChangedSince(Snapshot oldSnapshot, File file) {
        Snapshot now = snapshot(file)
        assertNotEquals(oldSnapshot.hash, now.hash)
    }

    static void assertModificationTimeHasChangedSince(Snapshot oldSnapshot, File file) {
        Snapshot now = snapshot(file)
        assertNotEquals(oldSnapshot.modTime, now.modTime)
    }

    private static File assertIsFile(File file) {
        assertTrue(file.isFile())
    }

    private static HashCode md5(File file) {
        return Hashing.md5().hashFile(file)
    }

    static class Snapshot {
        private final long modTime
        private final HashCode hash

        Snapshot(long modTime, HashCode hash) {
            this.modTime = modTime
            this.hash = hash
        }

        long getModTime() {
            return modTime
        }

        HashCode getHash() {
            return hash
        }
    }
}
