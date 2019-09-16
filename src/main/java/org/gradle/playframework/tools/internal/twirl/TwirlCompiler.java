package org.gradle.playframework.tools.internal.twirl;

import org.gradle.playframework.sourcesets.TwirlTemplateFormat;
import org.gradle.playframework.tools.internal.Compiler;
import org.gradle.api.internal.file.RelativeFile;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.WorkResults;
import org.gradle.internal.FileUtils;
import org.gradle.playframework.tools.internal.scala.ScalaMethod;
import org.gradle.playframework.tools.internal.scala.ScalaOptionInvocationWrapper;
import org.gradle.util.CollectionUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Twirl compiler uses reflection to load and invoke the actual compiler classes/methods.
 */
public class TwirlCompiler implements Compiler<TwirlCompileSpec>, Serializable {

    private final VersionedTwirlCompilerAdapter adapter;

    public TwirlCompiler(VersionedTwirlCompilerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public WorkResult execute(TwirlCompileSpec spec) {
        List<File> outputFiles = new ArrayList<>();
        ClassLoader cl = getClass().getClassLoader();
        ScalaMethod compile = getCompileMethod(cl);
        Iterable<RelativeFile> sources = spec.getSources();

        for (RelativeFile sourceFile : sources) {
            TwirlTemplateFormat format = findTemplateFormat(spec, sourceFile.getFile());
            try {
                Object result = compile.invoke(buildCompileArguments(spec, cl, sourceFile, format));
                ScalaOptionInvocationWrapper<File> maybeFile = new ScalaOptionInvocationWrapper<File>(result);
                if (maybeFile.isDefined()) {
                    File outputFile = maybeFile.get();
                    outputFiles.add(outputFile);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error invoking Play Twirl template compiler.", e);
            }
        }

        return WorkResults.didWork(!outputFiles.isEmpty());
    }

    private ScalaMethod getCompileMethod(ClassLoader cl) {
        try {
            return adapter.getCompileMethod(cl);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error invoking Play Twirl template compiler.", e);
        }
    }

    private Object[] buildCompileArguments(TwirlCompileSpec spec, ClassLoader cl, RelativeFile sourceFile, TwirlTemplateFormat format) {
        try {
            return adapter.createCompileParameters(cl, sourceFile.getFile(), sourceFile.getBaseDir(), spec.getDestinationDir(), spec.getDefaultImports(), format, spec.getAdditionalImports(), spec.getConstructorAnnotations());
        } catch (Exception e) {
            throw new RuntimeException("Error invoking Play Twirl template compiler.", e);
        }
    }

    private TwirlTemplateFormat findTemplateFormat(TwirlCompileSpec spec, final File sourceFile) {
        Spec<TwirlTemplateFormat> hasExtension = format -> FileUtils.hasExtensionIgnoresCase(sourceFile.getName(), "." + format.getExtension());

        TwirlTemplateFormat format = CollectionUtils.findFirst(adapter.getDefaultTemplateFormats(), hasExtension);
        if (format == null) {
            format = CollectionUtils.findFirst(spec.getUserTemplateFormats(), hasExtension);
        }

        if (format == null) {
            throw new RuntimeException("Twirl compiler could not find a matching template for '" + sourceFile.getName() + "'.");
        }

        return format;
    }

    public Object getDependencyNotation() {
        return adapter.getDependencyNotation();
    }

    public Iterable<String> getClassLoaderPackages() {
        return adapter.getClassLoaderPackages();
    }
}
