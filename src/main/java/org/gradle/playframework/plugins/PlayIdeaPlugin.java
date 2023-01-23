package org.gradle.playframework.plugins;

import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.playframework.extensions.PlayExtension;
import org.gradle.playframework.extensions.PlayPlatform;
import org.gradle.playframework.tasks.JavaScriptMinify;
import org.gradle.playframework.tasks.RoutesCompile;
import org.gradle.playframework.tasks.TwirlCompile;
import org.gradle.plugins.ide.idea.GenerateIdeaModule;
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel;
import org.gradle.plugins.ide.idea.model.IdeaModule;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.gradle.playframework.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;
import static org.gradle.playframework.plugins.internal.PlayPluginHelper.getMainScalaSourceDirectorySet;

public class PlayIdeaPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().named("ideaModule", GenerateIdeaModule.class, ideaModuleTask -> {
            IdeaModule module = ideaModuleTask.getModule();
            ConventionMapping conventionMapping = conventionMappingFor(module);

            TaskProvider<JavaScriptMinify> javaScriptMinifyTask = project.getTasks().named(PlayJavaScriptPlugin.JS_MINIFY_TASK_NAME, JavaScriptMinify.class);

            conventionMapping.map("sourceDirs", (Callable<Set<File>>) () -> {
                // TODO: Assets should probably be a source set too
                Set<File> sourceDirs = new HashSet<>();
                sourceDirs.add(new File(project.getProjectDir(), "public"));

                SourceDirectorySet scalaSourceDirectorySet = getMainScalaSourceDirectorySet(project);
                sourceDirs.addAll(scalaSourceDirectorySet.getSrcDirs());
                sourceDirs.add(javaScriptMinifyTask.get().getDestinationDir().get().getAsFile());
                return sourceDirs;
            });

            PlayExtension playExtension = (PlayExtension) project.getExtensions().getByName(PLAY_EXTENSION_NAME);
            conventionMapping.map("targetBytecodeVersion", (Callable<JavaVersion>) () -> getTargetJavaVersion(playExtension.getPlatform()));
            conventionMapping.map("languageLevel", (Callable<IdeaLanguageLevel>) () -> new IdeaLanguageLevel(getTargetJavaVersion(playExtension.getPlatform())));

            ideaModuleTask.dependsOn(javaScriptMinifyTask);
            ideaModuleTask.dependsOn(project.getTasks().withType(TwirlCompile.class));
            ideaModuleTask.dependsOn(project.getTasks().withType(RoutesCompile.class));
        });
    }

    private ConventionMapping conventionMappingFor(IdeaModule module) {
        return new DslObject(module).getConventionMapping();
    }

    private JavaVersion getTargetJavaVersion(PlayPlatform platform) {
        return platform.getJavaVersion().get();
    }
}
