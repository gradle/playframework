/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.playframework.fixtures.app

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import org.gradle.playframework.extensions.PlayPlatform
import org.gradle.util.RelativePathUtil
import org.gradle.util.VersionNumber

import static org.gradle.playframework.fixtures.Repositories.playRepositories

abstract class PlayApp {
    final VersionNumber playVersion
    final Configuration cfg
    final Map<String, String> model

    PlayApp() {
        this(VersionNumber.parse(PlayPlatform.DEFAULT_PLAY_VERSION))
    }

    PlayApp(VersionNumber version) {
        playVersion = version
        cfg = new Configuration(Configuration.VERSION_2_3_29)
        cfg.setTemplateLoader(new ClassTemplateLoader())
        cfg.setDefaultEncoding("UTF-8")
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
        cfg.setLogTemplateExceptions(false)
        cfg.setWrapUncheckedExceptions(true)
        cfg.setFallbackOnNullLoopVariable(false)
        model = new HashMap<>()
        model.put("playVersion", playVersion.major + "." + playVersion.minor)
    }

    String getName() {
        getClass().getSimpleName().toLowerCase()
    }

    String getResourcePath(String relativePath) {
        String basePath = this.getClass().getCanonicalName().split("\\.").dropRight(1).join("/")
        return basePath + "/" + relativePath
    }

    List<SourceFile> getAllFiles() {
        return appSources + testSources + viewSources + assetSources + confSources + otherSources
    }

    SourceFile getGradleBuild() {
        String gradleBuildContent = renderTemplate(getResourcePath(getName() + "/build.gradle.ftl"))
        def gradleBuildWithRepositories = gradleBuildContent.concat """
            allprojects {
                ${playRepositories()}
            }
        """
        return new SourceFile("", "build.gradle", gradleBuildWithRepositories)
    }

    List<SourceFile> getAssetSources() {
        sourceFiles("public", "shared")
    }

    List<SourceFile> getAppSources() {
        return sourceFiles("app").findAll {
            it.path != "app/views"
        }
    }

    List<SourceFile> getViewSources() {
        return sourceFiles("app/views")
    }

    List<SourceFile> getConfSources() {
        return sourceFiles("conf", "shared") + sourceFiles("conf")
    }

    List<SourceFile> getTestSources() {
        return sourceFiles("test")
    }

    List<SourceFile> getOtherSources() {
        return [ sourceFile("", "README", "shared") ]
    }


    protected SourceFile sourceFile(String path, String name, String baseDir = getName()) {
        String resourcePath = getResourcePath("$baseDir/$path/$name")
        URL resource = getClass().getClassLoader().getResource(resourcePath)

        if(resource == null) {
            throw new IllegalStateException("Could not find resource on the classpath: $resourcePath")
        }

        File source = new File(resource.toURI())
        if(isTemplate(source)) {
            String content = renderTemplate(resourcePath)
            return new SourceFile(path, source.name[0..<-4], content)
        } else {
            return new SourceFile(path, source.name, source.text)
        }
    }

    void writeSources(File sourceDir) {
        gradleBuild.writeToDir(sourceDir)
        for (SourceFile srcFile : allFiles) {
            srcFile.writeToDir(sourceDir)
        }
    }

    /**
     * Generate a list of source files for this app, based on existing files
     * in the project.
     */
    List<SourceFile> sourceFiles(String baseDir, String rootDir = getName()) {
        List sourceFiles = new ArrayList()

        String resourcePath = "$rootDir/$baseDir"
        URL resource = getClass().getResource(resourcePath)
        if(resource != null){
            File baseDirFile = new File(resource.toURI())
            baseDirFile.eachFileRecurse { File source ->
                if(source.isDirectory()){
                    return
                }

                def subpath = RelativePathUtil.relativePath(baseDirFile, source.parentFile)

                if(isTemplate(source)) {
                    String content = renderTemplate(getResourcePath(resourcePath + "/" + subpath + "/" + source.name))
                    SourceFile file = new SourceFile("$baseDir/$subpath", source.name[0..<-4], content)
                    sourceFiles.add(file)
                } else {
                    SourceFile file = new SourceFile("$baseDir/$subpath", source.name, source.text)
                    sourceFiles.add(file)
                }
            }
        }

        return sourceFiles
    }

    static boolean isTemplate(File file) {
        return file.name.endsWith('.ftl')
    }

    String renderTemplate(String templatePath) {
        try {
            Template tmpl = cfg.getTemplate(templatePath)
            StringWriter sw = new StringWriter()
            tmpl.process(model, sw)
            return sw.toString()
        } catch(Exception e) {
            throw e
        }
    }
}
