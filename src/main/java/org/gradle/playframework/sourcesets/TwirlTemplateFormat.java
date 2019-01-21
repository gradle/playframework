package org.gradle.playframework.sourcesets;

import java.util.Collection;

/**
 * Twirl Template format mapping.
 */
public interface TwirlTemplateFormat {
    /**
     * Extension without the leading '.'.
     *
     * @return file extension
     */
    String getExtension();

    /**
     * Fully qualified class name for the template format.
     *
     * @return class name of the format
     */
    String getFormatType();

    /**
     * Imports that are needed for this template format.
     * <p>
     * These are just the packages or individual classes in Scala format.
     * Use {@code my.package._} and not {@code my.package.*}.
     * </p>
     * @return collection of imports to be added after the default Twirl imports
     */
    Collection<String> getTemplateImports();
}
