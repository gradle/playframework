package org.gradle.playframework.runner

import groovy.transform.CompileStatic
import org.gradle.exemplar.executor.CliCommandExecutor
import org.gradle.exemplar.executor.CommandExecutor
import org.gradle.exemplar.executor.ExecutionMetadata
import org.gradle.exemplar.model.Command
import org.gradle.exemplar.test.runner.GradleSamplesRunner
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.runners.model.InitializationError

/**
 * A samples runner for Gradle plugins "under test". Checks for the plugin classpath and passes it along to TestKit.
 */
@CompileStatic
class GradlePluginSamplesRunner extends GradleSamplesRunner {
    private static final String GRADLE_EXECUTABLE = "gradle"

    private File customGradleInstallation = null

    GradlePluginSamplesRunner(Class<?> testClass) throws InitializationError {
        super(testClass)
    }

    @Override
    protected CommandExecutor selectExecutor(ExecutionMetadata executionMetadata, File workingDir, Command command) {
        File actualWorkingDir = workingDir
        if (command.getExecutionSubdirectory() != null) {
            actualWorkingDir = new File(workingDir, command.getExecutionSubdirectory())
        }

        boolean expectFailure = command.isExpectFailure()
        return new GradleRunnerCommandExecutor(actualWorkingDir, customGradleInstallation, expectFailure)
    }

    private static class GradleRunnerCommandExecutor extends CommandExecutor {
        private final File workingDir
        private final File customGradleInstallation
        private final boolean expectFailure

        private GradleRunnerCommandExecutor(File workingDir, File customGradleInstallation, boolean expectFailure) {
            this.workingDir = workingDir
            this.customGradleInstallation = customGradleInstallation
            this.expectFailure = expectFailure
        }

        @Override
        protected int run(String executable, List<String> args, List<String> flags, OutputStream output) {
            if (executable != GRADLE_EXECUTABLE) {
                return new CliCommandExecutor(workingDir).run(executable, args, flags, output)
            }
            List<String> allArguments = new ArrayList<>(args)
            allArguments.addAll(flags)
            GradleRunner gradleRunner = GradleRunner.create()
                    .withProjectDir(workingDir)
                    .withArguments(allArguments)
                    .withPluginClasspath()
                    .forwardOutput()
            if (customGradleInstallation != null) {
                gradleRunner.withGradleInstallation(customGradleInstallation)
            }
            Writer mergedOutput = new OutputStreamWriter(output)
            try {
                BuildResult buildResult
                if (expectFailure) {
                    buildResult = gradleRunner.buildAndFail()
                } else {
                    buildResult = gradleRunner.build()
                }
                mergedOutput.write(buildResult.getOutput())
                mergedOutput.close()
                return expectFailure ? 1 : 0
            } catch (Exception e) {
                throw new RuntimeException("Could not execute " + executable, e)
            } finally {
                if (mergedOutput != null) {
                    mergedOutput.close()
                }
            }
        }
    }
}
