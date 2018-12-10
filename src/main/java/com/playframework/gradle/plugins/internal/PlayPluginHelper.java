package com.playframework.gradle.plugins.internal;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.internal.tasks.DefaultSourceSet;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

public final class PlayPluginHelper {

    private PlayPluginHelper() {}

    public static SourceSet getMainJavaSourceSet(Project project) {
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        return javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    }

    public static SourceDirectorySet getScalaSourceDirectorySet(Project project) {
        return ((SourceDirectorySet) InvokerHelper.invokeMethod(getMainJavaSourceSet(project), "getScala", null));
    }

    public static <T> T createCustomSourceSet(Project project, Class<? extends T> t, String name) {
        SourceSet mainSourceSet = getMainJavaSourceSet(project);
        T customSourceSet = project.getObjects().newInstance(t, name, ((DefaultSourceSet) mainSourceSet).getDisplayName(), project.getObjects());
        new DslObject(mainSourceSet).getConvention().getPlugins().put(name, customSourceSet);
        return customSourceSet;
    }
}
