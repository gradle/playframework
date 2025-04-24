import java.io.FileNotFoundException

plugins {
    groovy
    `java-gradle-plugin`
    org.gradle.playframework.`test-setup`
    org.gradle.playframework.`integration-test-fixtures`
    org.gradle.playframework.`integration-test`
    org.gradle.playframework.`user-guide`
    org.gradle.playframework.`github-pages`
    org.gradle.playframework.`documentation-test`
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "org.gradle.playframework"
version = "0.16.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0") {
        exclude(group = "org.codehaus.groovy")
    }
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    integTestFixturesImplementation("com.google.guava:guava:32.0.1-jre")
    integTestFixturesImplementation("org.hamcrest:hamcrest-library:1.3")
    integTestFixturesImplementation("org.apache.ant:ant:1.10.11")
    integTestFixturesImplementation("org.freemarker:freemarker:2.3.30")
    docTestImplementation("org.gradle.exemplar:samples-check:1.0.3")
    docTestRuntimeOnly("org.slf4j:slf4j-simple:1.7.16")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

gradlePlugin {
    testSourceSets(sourceSets.integTest.get(), sourceSets.docTest.get())
    website = "https://gradle.github.io/playframework/"
    vcsUrl = "https://github.com/gradle/playframework"

    plugins {
        register("play-twirl-plugin") {
            id = "org.gradle.playframework-twirl"
            displayName = "Play Twirl Plugin"
            description = "Plugin for compiling Twirl sources in a Play application."
            implementationClass = "org.gradle.playframework.plugins.PlayTwirlPlugin"
            tags.set(listOf("playframework", "web", "java", "scala"))
        }

        register("play-routes-plugin") {
            id = "org.gradle.playframework-routes"
            displayName = "Play Routes Plugin"
            description = "Plugin for compiling Play routes sources in a Play application."
            implementationClass = "org.gradle.playframework.plugins.PlayRoutesPlugin"
            tags.set(listOf("playframework", "web", "java", "scala"))
        }

        register("play-application-plugin") {
            id = "org.gradle.playframework-application"
            displayName = "Play Application Plugin"
            description = "Plugin for building a Play application."
            implementationClass = "org.gradle.playframework.plugins.PlayApplicationPlugin"
            tags.set(listOf("playframework", "web", "java", "scala"))
        }

        register("play-javascript-plugin") {
            id = "org.gradle.playframework-javascript"
            displayName = "Play JavaScript Plugin"
            description = "Plugin for adding JavaScript processing to a Play application."
            implementationClass = "org.gradle.playframework.plugins.PlayJavaScriptPlugin"
            tags.set(listOf("playframework", "web", "java", "scala"))
        }

        register("play-distribution-plugin") {
            id = "org.gradle.playframework-distribution"
            displayName = "Play Distribution Plugin"
            description = "Plugin for generating distributions for Play applications."
            implementationClass = "org.gradle.playframework.plugins.PlayDistributionPlugin"
            tags.set(listOf("playframework", "web", "java", "scala"))
        }

        register("play-ide-plugin") {
            id = "org.gradle.playframework-ide"
            displayName = "Play IDE Plugin"
            description = "Plugin for generating IDE project files for Play applications."
            implementationClass = "org.gradle.playframework.plugins.PlayIdePlugin"
            tags.set(listOf("playframework", "web", "java", "scala"))
        }

        register("play-plugin") {
            id = "org.gradle.playframework"
            displayName = "Play Plugin"
            description = "Plugin that supports building, testing and running Play applications with Gradle."
            implementationClass = "org.gradle.playframework.plugins.PlayPlugin"
            tags.set(listOf("playframework", "web", "java", "scala"))
        }
    }
}

// Wire in the publishing credentials from the environment or as a project property
setFromEnvOrGradleProperty("gradle.publish.key", "GRADLE_PUBLISH_KEY")
setFromEnvOrGradleProperty("gradle.publish.secret", "GRADLE_PUBLISH_SECRET")

fun Project.setFromEnvOrGradleProperty(gradleProperty: String, environmentVariable: String) {
    val envVar = providers.environmentVariable(environmentVariable)
    val gradleProp = providers.gradleProperty(gradleProperty)
    ext[gradleProperty] = envVar.orElse(gradleProp).getOrNull()
}

tasks.named<ProcessResources>("processIntegTestFixturesResources") {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.withType<Test>().configureEach {
    if (name != "docTest") {
        useJUnitPlatform() // required for Spock 2+, but Samples use JUnit 4 rule
    }
}

val readReleaseNotes by tasks.registering {
    description = "Ensure we've got some release notes handy"
    doLast {
        val releaseNotesFile = file("release-notes-$version.txt")
        if (!releaseNotesFile.exists()) {
            throw FileNotFoundException("Couldn't find release notes file ${releaseNotesFile.absolutePath}")
        }
        val releaseNotes = releaseNotesFile.readText().trim()
        require(!releaseNotes.isBlank()) { "Release notes file ${releaseNotesFile.absolutePath} is empty" }
        gradlePlugin.plugins["play-twirl-plugin"].description = releaseNotes
        gradlePlugin.plugins["play-routes-plugin"].description = releaseNotes
        gradlePlugin.plugins["play-application-plugin"].description = releaseNotes
        gradlePlugin.plugins["play-javascript-plugin"].description = releaseNotes
        gradlePlugin.plugins["play-distribution-plugin"].description = releaseNotes
        gradlePlugin.plugins["play-ide-plugin"].description = releaseNotes
        gradlePlugin.plugins["play-plugin"].description = releaseNotes
    }
}

tasks.publishPlugins {
    dependsOn(readReleaseNotes)
}
