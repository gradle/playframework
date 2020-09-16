package org.gradle.playframework.fixtures.app

class SourceFile {
    private final String path
    private final String name
    private final String content

    SourceFile(String path, String name, String content) {
        this.content = content
        this.path = path
        this.name = name
    }

    String getPath() {
        return path
    }

    String getName() {
        name
    }

    String getContent() {
        content
    }

    File writeToDir(File base) {
        writeToDir(base, name)
    }

    File writeToDir(File base, String name) {
        File baseFile = new File(base, path)
        createDirectory(baseFile)
        File file = new File(baseFile, name)
        writeToFile(file)
        file
    }

    private void createDirectory(File dir) {
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Unable to create directory '$dir'")
            }
        }
    }

    void writeToFile(File file) {
        if (file.exists()) {
            file.write("")
        }
        file.write(content)
    }

    @Override
    String toString() {
        return path + File.separator + name
    }
}
