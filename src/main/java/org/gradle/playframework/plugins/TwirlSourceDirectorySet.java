package org.gradle.playframework.plugins;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.playframework.sourcesets.TwirlImports;
import org.gradle.playframework.sourcesets.TwirlTemplateFormat;

/**
 * Represents a source directory set containing Twirl templates.
 * <p>
 * The following example demonstrate the use of the source set in a build script using the Groovy DSL:
 * <pre>
 * sourceSets {
 *     main {
 *         twirl {
 *             userTemplateFormats.add(newUserTemplateFormat('csv', 'views.formats.csv.CsvFormat', 'views.formats.csv._'))
 *             additionalImports = ['my.pkg._', 'my.pkg.MyClass']
 *         }
 *     }
 * }
 * </pre>
 */
public interface TwirlSourceDirectorySet extends SourceDirectorySet {
    /**
     * The default imports that should be added to generated source files.
     *
     * @return The default imports
     */
    Property<TwirlImports> getDefaultImports();

    /**
     * Returns the custom template formats configured for this source set.
     *
     * @return The custom template formats
     */
    ListProperty<TwirlTemplateFormat> getUserTemplateFormats();

    /**
     * Creates a new custom template format.
     *
     * @param extension The file extension this template applies to (e.g., html)
     * @param templateType Fully-qualified type for this template format
     * @param imports Additional imports to add for the custom template format
     * @return The new custom template format
     */
    TwirlTemplateFormat newUserTemplateFormat(final String extension, String templateType, String... imports);

    /**
     * Returns the list of additional imports to add to the generated Scala code.
     *
     * @return List of additional imports
     */
    ListProperty<String> getAdditionalImports();

    /**
     * Gets the list of construtor annotations to support dependency injection
     *
     * @return the list of annotations included in the template constructor
     */
    ListProperty<String> getConstructorAnnotations();
}
