package com.lightbend.play.fixtures

import org.gradle.internal.hash.HashCode
import org.gradle.internal.hash.Hashing
import org.gradle.internal.hash.HashingOutputStream

import java.nio.file.Files

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

final class FileFixtures {

    private FileFixtures() {}

    static Snapshot snapshot(File file) {
        assertIsFile(file)
        return new Snapshot(file.lastModified(), md5(file))
    }

    static void assertHasNotChangedSince(Snapshot oldSnapshot, File file) {
        Snapshot now = snapshot(file)
        assertEquals(oldSnapshot.modTime, now.modTime)
        assertEquals(oldSnapshot.hash, now.hash)
    }

    private static File assertIsFile(File file) {
        assertTrue(file.isFile())
    }

    private static HashCode md5(File file) {
        HashingOutputStream hashingStream = Hashing.primitiveStreamHasher()
        try {
            Files.copy(file.toPath(), hashingStream)
        } catch (IOException e) {
            throw new RuntimeException(e)
        }
        return hashingStream.hash()
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
