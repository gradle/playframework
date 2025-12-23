package org.gradle.playframework.plugins;

import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.SourceSet;
import org.gradle.playframework.plugins.internal.PlayPluginHelper;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class PluginResourceDirectoryTests {
    @Test
    public void testResourcesDirectories() {
        // given
        Project project = ProjectBuilder
                .builder()
                .build();

        // when
        project.getPluginManager().apply(PlayPlugin.class);

        SourceSet mainSourceSet = PlayPluginHelper.getMainJavaSourceSet(project);
        SourceDirectorySet mainResourcesDirectorySet = mainSourceSet.getResources();

        // then
        assertEquals(
                Collections.singleton(new File(project.getProjectDir(), "conf")),
                mainResourcesDirectorySet.getSrcDirs()
        );
    }

    @Test
    public void testTestResourcesDirectories() {
        // given
        Project project = ProjectBuilder
                .builder()
                .build();

        // when
        project.getPluginManager().apply(PlayPlugin.class);

        SourceSet testSourceSet = PlayPluginHelper.getTestJavaSourceSet(project);
        SourceDirectorySet testResourcesDirectorySet = testSourceSet.getResources();

        // then
        assertEquals(
                Collections.singleton(new File(project.getProjectDir(), "test/resources")),
                testResourcesDirectorySet.getSrcDirs()
        );
    }
}
