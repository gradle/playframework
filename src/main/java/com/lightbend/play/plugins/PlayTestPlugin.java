package com.lightbend.play.plugins;

import com.lightbend.play.extensions.PlayExtension;
import com.lightbend.play.extensions.PlayPluginConfigurations;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.collections.ImmutableFileCollection;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.scala.IncrementalCompileOptions;
import org.gradle.api.tasks.scala.ScalaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.util.PatternSet;

import java.io.File;

import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_CONFIGURATIONS_EXTENSION_NAME;
import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;
import static org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME;
import static org.gradle.api.plugins.JavaPlugin.TEST_TASK_NAME;

public class PlayTestPlugin implements Plugin<Project> {

    private static final String TEST_SCALA_COMPILE_TASK_NAME = "compileTestScala";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(PlayApplicationPlugin.class);

        project.afterEvaluate(prj -> {
            final FileCollection testCompileClasspath = getTestCompileClasspath(project);
            final File testSourceDir = project.file("test");
            final FileCollection testSources = ImmutableFileCollection.of(testSourceDir).getAsFileTree().matching(new PatternSet().include("**/*.scala", "**/*.java"));
            final File testClassesDir = new File(project.getBuildDir(), "testClasses");

            project.getTasks().named(TEST_SCALA_COMPILE_TASK_NAME, ScalaCompile.class, testScalaCompile -> {
                testScalaCompile.setDescription("Compiles the scala and java test sources for the Play application.");
                testScalaCompile.setClasspath(testCompileClasspath);
                testScalaCompile.setDestinationDir(testClassesDir);
                testScalaCompile.setSource(testSources);

                PlayExtension playExtension = (PlayExtension) project.getExtensions().getByName(PLAY_EXTENSION_NAME);
                String targetCompatibility = playExtension.getPlatform().getJavaVersion().get().getMajorVersion();
                testScalaCompile.setSourceCompatibility(targetCompatibility);
                testScalaCompile.setTargetCompatibility(targetCompatibility);

                IncrementalCompileOptions incrementalOptions = testScalaCompile.getScalaCompileOptions().getIncrementalOptions();
                incrementalOptions.getAnalysisFile().set(new File(project.getBuildDir(), "tmp/scala/compilerAnalysis/" + TEST_SCALA_COMPILE_TASK_NAME + ".analysis"));
            });

            project.getTasks().named(TEST_TASK_NAME, Test.class, test -> {
                test.setDescription("Runs tests for Play application.");
                test.setClasspath(test.getClasspath().plus(getRuntimeClasspath(testClassesDir, testCompileClasspath)));
                test.setTestClassesDirs(ImmutableFileCollection.of(testClassesDir));
            });
        });
    }

    private FileCollection getTestCompileClasspath(Project project) {
        TaskProvider<Jar> mainJarTask = project.getTasks().named(JAR_TASK_NAME, Jar.class);
        PlayPluginConfigurations configurations = (PlayPluginConfigurations) project.getExtensions().getByName(PLAY_CONFIGURATIONS_EXTENSION_NAME);
        return ImmutableFileCollection.of(mainJarTask.get().getArchivePath()).plus(configurations.getPlayTest().getAllArtifacts());
    }

    private FileCollection getRuntimeClasspath(File testClassesDir, FileCollection testCompileClasspath) {
        return ImmutableFileCollection.of(testClassesDir).plus(testCompileClasspath);
    }
}
