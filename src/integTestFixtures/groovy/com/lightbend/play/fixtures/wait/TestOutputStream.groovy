package com.lightbend.play.fixtures.wait

class TestOutputStream extends OutputStream {
    private final buffer = new ByteArrayOutputStream()

    @Override
    void write(int b) throws IOException {
        synchronized (buffer) {
            buffer.write(b)
        }
    }

    @Override
    String toString() {
        synchronized (buffer) {
            return buffer.toString()
        }
    }

    void reset() {
        synchronized (buffer) {
            buffer.reset()
        }
    }
}
