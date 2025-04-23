package org.gradle.playframework.plugins.internal;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;


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

    public static <T extends SourceDirectorySet> T createCustomSourceDirectorySet(Project project, Class<T> sourceDirectorySetType, String name) {
        SourceSet mainJavaSourceSet = getMainJavaSourceSet(project);
        T sourceDirectorySet = project.getObjects().newInstance(sourceDirectorySetType, project.getObjects().sourceDirectorySet(name, ((DefaultSourceSet) mainJavaSourceSet).getDisplayName()));
        mainJavaSourceSet.getExtensions().add(name, sourceDirectorySet);
        return sourceDirectorySet;
//        if (GradleVersion.current().compareTo(GradleVersion.version("8.13")) >= 0) {
//            // instantiate DefaultScalaSourceDirectorySet with non-deprecated constructor
//        } else {
//            // instantiate DefaultScalaSourceDirectorySet with old constructor
//            return project.getObjects().newInstance(sourceDirectorySetType, project.getObjects().sourceDirectorySet(name, ((DefaultSourceSet) mainJavaSourceSet).getDisplayName())); // TODO (donat) does this even work with old versions not knowing about TaskDependencyFactory
//        }
    }
}
