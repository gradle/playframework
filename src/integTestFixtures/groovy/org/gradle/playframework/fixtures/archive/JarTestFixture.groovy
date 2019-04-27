package org.gradle.playframework.fixtures.archive

import org.apache.tools.zip.ZipFile

import java.util.jar.JarFile
import java.util.jar.Manifest

class JarTestFixture extends ZipTestFixture {
    File file

    /**
     * Asserts that the Jar file is well-formed
     */
    JarTestFixture(File file, String metadataCharset = 'UTF-8', String contentCharset = null) {
        super(file, metadataCharset, contentCharset)
        this.file = file
        isManifestPresentAndFirstEntry()
    }

    /**
     * Asserts that the manifest file is present and first entry in this jar file.
     */
    void isManifestPresentAndFirstEntry() {
        def zipFile = new ZipFile(file, metadataCharset)
        try {
            def entries = zipFile.getEntries()
            def zipEntry = entries.nextElement();
            if(zipEntry.getName().equalsIgnoreCase('META-INF/')) {
                zipEntry = entries.nextElement()
            }
            def firstEntryName = zipEntry.getName()
            assert firstEntryName.equalsIgnoreCase(JarFile.MANIFEST_NAME)
        } finally {
            zipFile.close()
        }
    }

    @Override
    def hasDescendants(String... relativePaths) {
        String[] allDescendants = relativePaths + JarFile.MANIFEST_NAME
        return super.hasDescendants(allDescendants)
    }

    Manifest getManifest() {
        InputStream stream = new ByteArrayInputStream( content(JarFile.MANIFEST_NAME).bytes )
        new Manifest(stream)
    }
}
