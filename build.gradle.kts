plugins {
    groovy
    `java-gradle-plugin`
    org.gradle.playframework.`test-setup`
    org.gradle.playframework.`integration-test-fixtures`
    org.gradle.playframework.`integration-test`
    org.gradle.playframework.`user-guide`
    org.gradle.playframework.`github-pages`
    org.gradle.playframework.`documentation-test`
    id("com.gradle.plugin-publish") version "1.1.0"
}

group = "org.gradle.playframework"
version = "0.15"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0") {
        exclude(group = "org.codehaus.groovy")
    }
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    integTestFixturesImplementation("com.google.guava:guava:23.0")
    integTestFixturesImplementation("org.hamcrest:hamcrest-library:1.3")
    integTestFixturesImplementation("org.apache.ant:ant:1.9.3")
    integTestFixturesImplementation("org.freemarker:freemarker:2.3.30")
    docTestImplementation("org.gradle.exemplar:samples-check:1.0.0")
    docTestRuntimeOnly("org.slf4j:slf4j-simple:1.7.16")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

gradlePlugin {
    testSourceSets(sourceSets.integTest.get(), sourceSets.docTest.get())

    plugins {
        register("play-twirl-plugin") {
            id = "org.gradle.playframework-twirl"
            displayName = "Play Twirl Plugin"
            description = "Plugin for compiling Twirl sources in a Play application."
            implementationClass = "org.gradle.playframework.plugins.PlayTwirlPlugin"
        }

        register("play-routes-plugin") {
            id = "org.gradle.playframework-routes"
            displayName = "Play Routes Plugin"
            description = "Plugin for compiling Play routes sources in a Play application."
            implementationClass = "org.gradle.playframework.plugins.PlayRoutesPlugin"
        }

        register("play-application-plugin") {
            id = "org.gradle.playframework-application"
            displayName = "Play Application Plugin"
            description = "Plugin for building a Play application."
            implementationClass = "org.gradle.playframework.plugins.PlayApplicationPlugin"
        }

        register("play-javascript-plugin") {
            id = "org.gradle.playframework-javascript"
            displayName = "Play JavaScript Plugin"
            description = "Plugin for adding JavaScript processing to a Play application."
            implementationClass = "org.gradle.playframework.plugins.PlayJavaScriptPlugin"
        }

        register("play-distribution-plugin") {
            id = "org.gradle.playframework-distribution"
            displayName = "Play Distribution Plugin"
            description = "Plugin for generating distributions for Play applications."
            implementationClass = "org.gradle.playframework.plugins.PlayDistributionPlugin"
        }

        register("play-ide-plugin") {
            id = "org.gradle.playframework-ide"
            displayName = "Play IDE Plugin"
            description = "Plugin for generating IDE project files for Play applications."
            implementationClass = "org.gradle.playframework.plugins.PlayIdePlugin"
        }

        register("play-plugin") {
            id = "org.gradle.playframework"
            displayName = "Play Plugin"
            description = "Plugin that supports building, testing and running Play applications with Gradle."
            implementationClass = "org.gradle.playframework.plugins.PlayPlugin"
        }
    }
}

// Wire in the publishing credentials from the environment or as a project property
setFromEnvOrGradleProperty("gradle.publish.key", "GRADLE_PUBLISH_KEY")
setFromEnvOrGradleProperty("gradle.publish.secret", "GRADLE_PUBLISH_SECRET")

fun Project.setFromEnvOrGradleProperty(gradleProperty: String, environmentVariable: String) {
    val envVar = providers.environmentVariable(environmentVariable).forUseAtConfigurationTime()
    val gradleProp = providers.gradleProperty(gradleProperty).forUseAtConfigurationTime()
    ext[gradleProperty] = envVar.orElse(gradleProp).getOrNull()
}

pluginBundle {
    website = "https://gradle.github.io/playframework/"
    vcsUrl = "https://github.com/gradle/playframework"
    tags = listOf("playframework", "web", "java", "scala")
}

tasks.named<ProcessResources>("processIntegTestFixturesResources") {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.withType<Test>().configureEach {
    if (name != "docTest") {
        useJUnitPlatform() // required for Spock 2+, but Samples use JUnit 4 rule
    }
}
