package org.gradle.playframework.sourcesets.internal;

import org.gradle.playframework.sourcesets.TwirlTemplateFormat;

import java.io.Serializable;
import java.util.Collection;

public class DefaultTwirlTemplateFormat implements TwirlTemplateFormat, Serializable {
    private final String extension;
    private final String formatType;
    private final Collection<String> imports;

    public DefaultTwirlTemplateFormat(String extension, String formatType, Collection<String> imports) {
        if (extension == null) {
            throw new RuntimeException("Custom template extension cannot be null.");
        }
        if (extension.startsWith(".")) {
            throw new RuntimeException("Custom template extension should not start with a dot.");
        }
        if (formatType == null) {
            throw new RuntimeException("Custom template format type cannot be null.");
        }

        this.extension = extension;
        this.formatType = formatType;
        this.imports = imports;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public String getFormatType() {
        return formatType;
    }

    @Override
    public Collection<String> getTemplateImports() {
        return imports;
    }

    @Override
    public String toString() {
        return "DefaultTwirlTemplateFormat{"
                + "extension='" + extension + '\''
                + ", formatType='" + formatType + '\''
                + ", imports=" + imports
                + '}';
    }
}
