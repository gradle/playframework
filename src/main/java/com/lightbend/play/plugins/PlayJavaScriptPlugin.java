package com.lightbend.play.plugins;

import com.lightbend.play.extensions.PlayExtension;
import com.lightbend.play.sourcesets.DefaultJavaScriptSourceSet;
import com.lightbend.play.sourcesets.JavaScriptSourceSet;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.play.platform.PlayPlatform;
import org.gradle.play.tasks.JavaScriptMinify;

import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;

/**
 * Plugin for adding javascript processing to a Play application.
 */
public class PlayJavaScriptPlugin implements PlayGeneratedSourcePlugin {

    public static final String JS_MINIFY_TASK_NAME = "minifyPlayJavaScript";

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(BasePlugin.class);

        JavaScriptSourceSet javaScriptSourceSet = createJavaScriptSourceSet(project);
        createDefaultJavaScriptMinifyTask(project, javaScriptSourceSet.getJavaScript());
    }

    private JavaScriptSourceSet createJavaScriptSourceSet(Project project) {
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        SourceSet mainSourceSet = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);

        JavaScriptSourceSet javaScriptSourceSet = project.getObjects().newInstance(DefaultJavaScriptSourceSet.class, "javaScript", ((DefaultSourceSet) mainSourceSet).getDisplayName(), project.getObjects());
        new DslObject(mainSourceSet).getConvention().getPlugins().put("javaScript", javaScriptSourceSet);
        return javaScriptSourceSet;
    }

    private JavaScriptMinify createDefaultJavaScriptMinifyTask(Project project, SourceDirectorySet sourceDirectory) {
        PlayPlatform playPlatform = ((PlayExtension)project.getExtensions().getByName(PLAY_EXTENSION_NAME)).getPlatform().asPlayPlatform();

        return project.getTasks().create(JS_MINIFY_TASK_NAME, JavaScriptMinify.class, javaScriptMinify -> {
            javaScriptMinify.setDescription("Minifies javascript for the " + sourceDirectory.getDisplayName() + ".");
            javaScriptMinify.setDestinationDir(getOutputDir(project, sourceDirectory));
            javaScriptMinify.setPlayPlatform(playPlatform);
            javaScriptMinify.setSource(sourceDirectory);
            javaScriptMinify.dependsOn(sourceDirectory);
        });
    }
}
