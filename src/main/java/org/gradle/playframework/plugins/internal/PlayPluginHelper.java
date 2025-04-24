package org.gradle.playframework.plugins.internal;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.util.GradleVersion;


public final class PlayPluginHelper {

    private PlayPluginHelper() {
    }

    public static SourceSet getMainJavaSourceSet(Project project) {
        return getSourceSet(project, SourceSet.MAIN_SOURCE_SET_NAME);
    }

    public static SourceSet getTestJavaSourceSet(Project project) {
        return getSourceSet(project, SourceSet.TEST_SOURCE_SET_NAME);
    }

    private static SourceSet getSourceSet(Project project, String sourceSetName) {
        JavaPluginExtension javaPluginExtension = project.getExtensions().getByType(JavaPluginExtension.class);
        return javaPluginExtension.getSourceSets().getByName(sourceSetName);
    }

    public static SourceDirectorySet getMainScalaSourceDirectorySet(Project project) {
        return getScalaSourceDirectorySet(getMainJavaSourceSet(project));
    }

    public static SourceDirectorySet getTestScalaSourceDirectorySet(Project project) {
        return getScalaSourceDirectorySet(getTestJavaSourceSet(project));
    }

    private static SourceDirectorySet getScalaSourceDirectorySet(SourceSet sourceSet) {
        try {
            // >= Gradle 7.1. Gradle 9.0 removed the scala convention on source sets.
            return (SourceDirectorySet) sourceSet.getExtensions().getByName("scala");
        } catch (Exception e) {
            // < Gradle 7.1
            return ((SourceDirectorySet) InvokerHelper.invokeMethod(sourceSet, "getScala", null));
        }
    }

    /**
     * Creates a custom source directory set compatible with the current Gradle version.
     *
     * @param project The Gradle project
     * @param name Name of the source directory set
     * @param sourceDirectorySetType8 Source directory set type for Gradle 8.0+
     * @param sourceDirectorySetTypeBefore8 Source directory set type for Gradle before 8.0
     * @return The created source directory set
     */
    public static <T extends SourceDirectorySet> T createCustomSourceDirectorySet(
        Project project,
        String name,
        Class<? extends T> sourceDirectorySetType8,
        Class<? extends T> sourceDirectorySetTypeBefore8
    ) {
        SourceSet mainJavaSourceSet = getMainJavaSourceSet(project);

        Class<? extends T> sourceDirectorySetType = selectGradleVersionCompatibleType(
            sourceDirectorySetType8,
            sourceDirectorySetTypeBefore8
        );

        T sourceDirectorySet = project.getObjects().newInstance(
            sourceDirectorySetType,
            project.getObjects().sourceDirectorySet(
                name,
                ((DefaultSourceSet) mainJavaSourceSet).getDisplayName()
            )
        );

        mainJavaSourceSet.getExtensions().add(name, sourceDirectorySet);

        return sourceDirectorySet;
    }

    /**
     * Selects the appropriate source directory set type based on the Gradle version.
     *
     * @param typeForGradle8Plus Type to use for Gradle 8.0 and newer
     * @param typeForOlderGradle Type to use for Gradle versions before 8.0
     * @return The appropriate source directory set type
     */
    private static <T> Class<? extends T> selectGradleVersionCompatibleType(
        Class<? extends T> typeForGradle8Plus,
        Class<? extends T> typeForOlderGradle
    ) {
        boolean isGradle8OrNewer = GradleVersion.current().compareTo(GradleVersion.version("8.0")) >= 0;
        return isGradle8OrNewer ? typeForGradle8Plus : typeForOlderGradle;
    }
}
