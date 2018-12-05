package com.lightbend.play.fixtures.wait

class ExecutionOutput {
    String stdout
    String stderr

    ExecutionOutput(String stdout, String stderr) {
        this.stdout = stdout
        this.stderr = stderr
    }
}
