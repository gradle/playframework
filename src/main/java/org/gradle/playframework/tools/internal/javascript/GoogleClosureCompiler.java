package org.gradle.playframework.tools.internal.javascript;

import org.gradle.api.UncheckedIOException;
import org.gradle.api.internal.file.RelativeFile;
import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.WorkResults;
import org.gradle.playframework.tools.internal.Compiler;
import org.gradle.playframework.tools.internal.reflection.DirectInstantiator;
import org.gradle.playframework.tools.internal.reflection.JavaMethod;
import org.gradle.playframework.tools.internal.reflection.JavaReflectionUtil;
import org.gradle.playframework.tools.internal.reflection.PropertyAccessor;
import org.gradle.plugins.javascript.base.SourceTransformationException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleClosureCompiler implements Compiler<JavaScriptCompileSpec>, Serializable {
    private static final Iterable<String> SHARED_PACKAGES = Arrays.asList("com.google.javascript");
    private static final String DEFAULT_GOOGLE_CLOSURE_VERSION = "v20141215";
    private Class<?> sourceFileClass;
    private Class<?> compilerOptionsClass;
    private Class<Enum> compilationLevelClass;
    private Class<Object> compilerClass;

    public Iterable<String> getClassLoaderPackages() {
        return SHARED_PACKAGES;
    }

    public static Object getDependencyNotation() {
        return "com.google.javascript:closure-compiler:" + DEFAULT_GOOGLE_CLOSURE_VERSION;
    }

    @Override
    public WorkResult execute(JavaScriptCompileSpec spec) {
        JavaScriptCompileDestinationCalculator destinationCalculator = new JavaScriptCompileDestinationCalculator(spec.getDestinationDir());
        List<String> allErrors = new ArrayList<>();

        for (RelativeFile sourceFile : spec.getSources()) {
            allErrors.addAll(compile(sourceFile, spec, destinationCalculator));
        }

        if (allErrors.isEmpty()) {
            return WorkResults.didWork(true);
        } else {
            throw new SourceTransformationException(String.format("Minification failed with the following errors:\n\t%s", String.join( "\n\t", allErrors)), null);
        }
    }

    List<String> compile(RelativeFile javascriptFile, JavaScriptCompileSpec spec, JavaScriptCompileDestinationCalculator destinationCalculator) {
        List<String> errors = new ArrayList<>();

        loadCompilerClasses(getClass().getClassLoader());

        // Create a SourceFile object to represent an "empty" extern
        JavaMethod<?, Object> fromCodeJavaMethod = JavaReflectionUtil.staticMethod(sourceFileClass, Object.class, "fromCode", String.class, String.class);
        Object extern = fromCodeJavaMethod.invokeStatic("/dev/null", "");

        // Create a SourceFile object to represent the javascript file to compile
        JavaMethod<?, Object> fromFileJavaMethod = JavaReflectionUtil.staticMethod(sourceFileClass, Object.class, "fromFile", File.class);
        Object sourceFile = fromFileJavaMethod.invokeStatic(javascriptFile.getFile());

        // Construct a new CompilerOptions class
        Object compilerOptions = DirectInstantiator.INSTANCE.newInstance(compilerOptionsClass);

        // Get the CompilationLevel.SIMPLE_OPTIMIZATIONS class and set it on the CompilerOptions class
        @SuppressWarnings({ "rawtypes", "unchecked" }) Enum simpleLevel = Enum.valueOf(compilationLevelClass, "SIMPLE_OPTIMIZATIONS");
        @SuppressWarnings("rawtypes") JavaMethod<Enum, Void> setOptionsForCompilationLevelMethod = JavaReflectionUtil.method(compilationLevelClass, Void.class, "setOptionsForCompilationLevel", compilerOptionsClass);
        setOptionsForCompilationLevelMethod.invoke(simpleLevel, compilerOptions);

        // Construct a new Compiler class
        Object compiler = DirectInstantiator.INSTANCE.newInstance(compilerClass, getDummyPrintStream());

        // Compile the javascript file with the options we've created
        JavaMethod<Object, Object> compileMethod = JavaReflectionUtil.method(compilerClass, Object.class, "compile", sourceFileClass, sourceFileClass, compilerOptionsClass);
        Object result = compileMethod.invoke(compiler, extern, sourceFile, compilerOptions);

        // Get any errors from the compiler result
        PropertyAccessor<Object, Object[]> jsErrorsField = JavaReflectionUtil.readableField(result, Object[].class, "errors");
        Object[] jsErrors = jsErrorsField.getValue(result);

        if (jsErrors.length == 0) {
            // If no errors, get the compiled source and write it to the destination file
            JavaMethod<Object, String> toSourceMethod = JavaReflectionUtil.method(compilerClass, String.class, "toSource");
            String compiledSource = toSourceMethod.invoke(compiler);
            try {
                Files.write(destinationCalculator.transform(javascriptFile).toPath(), compiledSource.getBytes());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            for (Object error : jsErrors) {
                errors.add(error.toString());
            }
        }

        return errors;
    }

    private void loadCompilerClasses(ClassLoader cl) {
        try {
            if (sourceFileClass == null) {
                sourceFileClass = cl.loadClass("com.google.javascript.jscomp.SourceFile");
            }
            if (compilerOptionsClass == null) {
                compilerOptionsClass = cl.loadClass("com.google.javascript.jscomp.CompilerOptions");
            }
            if (compilationLevelClass == null) {
                @SuppressWarnings("unchecked") Class<Enum> clazz = (Class<Enum>) cl.loadClass("com.google.javascript.jscomp.CompilationLevel");
                compilationLevelClass = clazz;
            }
            if (compilerClass == null) {
                @SuppressWarnings("unchecked") Class<Object> clazz = (Class<Object>) cl.loadClass("com.google.javascript.jscomp.Compiler");
                compilerClass = clazz;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private PrintStream getDummyPrintStream() {
        OutputStream os = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                // do nothing
            }
        };
        return new PrintStream(os);
    }
}
