package org.gradle.playframework.fixtures.archive

import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipFile

import java.nio.charset.Charset

class ZipTestFixture extends ArchiveTestFixture {
    protected final String metadataCharset;
    protected final String contentCharset;

    ZipTestFixture(File file, String metadataCharset = null, String contentCharset = null) {
        this.metadataCharset = metadataCharset ?: Charset.defaultCharset().name()
        this.contentCharset = contentCharset ?: Charset.defaultCharset().name()
        def zipFile = new ZipFile(file, this.metadataCharset)
        try {
            def entries = zipFile.getEntries()
            while (entries.hasMoreElements()) {
                def entry = entries.nextElement()
                String content = getContentForEntry(entry, zipFile)
                if (!entry.directory) {
                    add(entry.name, content)
                }
                addMode(entry.name, entry.getUnixMode())
            }
        } finally {
            zipFile.close();
        }
    }

    private String getContentForEntry(ZipEntry entry, ZipFile zipFile) {
        def extension = entry.name.tokenize(".").last()
        if (!(extension in ["jar", "zip"])) {
            return zipFile.getInputStream(entry).getText(contentCharset)
        }
        return ""
    }
}
