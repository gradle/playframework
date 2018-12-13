package org.gradle.playframework.fixtures.archive

import org.apache.tools.tar.TarEntry
import org.apache.tools.tar.TarInputStream

import java.nio.charset.Charset
import java.util.zip.GZIPInputStream

class TarTestFixture extends ArchiveTestFixture {
    private final File tarFile

    TarTestFixture(File tarFile, String metadataCharset = null, String contentCharset = null) {
        this.tarFile = tarFile

        boolean gzip = !tarFile.name.endsWith("tar")
        tarFile.withInputStream { inputStream ->
            TarInputStream tarInputStream = new TarInputStream(gzip ? new GZIPInputStream(inputStream) : inputStream, metadataCharset)
            for (TarEntry tarEntry = tarInputStream.nextEntry; tarEntry != null; tarEntry = tarInputStream.nextEntry) {
                addMode(tarEntry.name, tarEntry.mode)
                if (tarEntry.directory) {
                    continue
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream()
                tarInputStream.copyEntryContents(stream)
                add(tarEntry.name, new String(stream.toByteArray(), contentCharset ?: Charset.defaultCharset().name()))
            }
        }
    }
}
