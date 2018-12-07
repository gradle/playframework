plugins {
    groovy
    `java-gradle-plugin`
    `build-scan`
    id("com.gradle.plugin-publish") version "0.10.0"
    id("com.playframework.gradle.test-setup")
    id("com.playframework.gradle.integration-test-fixtures")
    id("com.playframework.gradle.integration-test")
}

group = "com.playframework"
version = "0.1"

repositories {
    jcenter()
}

dependencies {
    testImplementation("org.spockframework:spock-core:1.2-groovy-2.4") {
        exclude(group = "org.codehaus.groovy")
    }
    integTestFixturesImplementation("com.google.guava:guava:23.0")
    integTestFixturesImplementation("org.hamcrest:hamcrest-library:1.3")
    integTestFixturesImplementation("org.apache.ant:ant:1.9.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
    testSourceSets(sourceSets["integTest"])

    plugins {
        register("play-twirl-plugin") {
            id = "com.playframework.play-twirl"
            displayName = "Play Twirl Plugin"
            description = "Plugin for compiling Twirl sources in a Play application."
            implementationClass = "com.playframework.gradle.plugins.PlayTwirlPlugin"
        }

        register("play-routes-plugin") {
            id = "com.playframework.play-routes"
            displayName = "Play Routes Plugin"
            description = "Plugin for compiling Play routes sources in a Play application."
            implementationClass = "com.playframework.gradle.plugins.PlayRoutesPlugin"
        }

        register("play-application-plugin") {
            id = "com.playframework.play-application"
            displayName = "Play Application Plugin"
            description = "Plugin for building a Play application."
            implementationClass = "com.playframework.gradle.plugins.PlayApplicationPlugin"
        }

        register("play-javascript-plugin") {
            id = "com.playframework.play-javascript"
            displayName = "Play JavaScript Plugin"
            description = "Plugin for adding JavaScript processing to a Play application."
            implementationClass = "com.playframework.gradle.plugins.PlayJavaScriptPlugin"
        }

        register("play-test-plugin") {
            id = "com.playframework.play-test"
            displayName = "Play Test Plugin"
            description = "Plugin for executing tests for Play applications."
            implementationClass = "com.playframework.gradle.plugins.PlayTestPlugin"
        }

        register("play-distribution-plugin") {
            id = "com.playframework.play-distribution"
            displayName = "Play Distribution Plugin"
            description = "Plugin for generating distributions for Play applications."
            implementationClass = "com.playframework.gradle.plugins.PlayDistributionPlugin"
        }

        register("play-ide-plugin") {
            id = "com.playframework.play-ide"
            displayName = "Play IDE Plugin"
            description = "Plugin for generating IDE project files for Play applications."
            implementationClass = "com.playframework.gradle.plugins.PlayIdePlugin"
        }

        register("play-plugin") {
            id = "com.playframework.play"
            displayName = "Play Plugin"
            description = "Plugin that supports building, testing and running Play applications with Gradle."
            implementationClass = "com.playframework.gradle.plugins.PlayPlugin"
        }
    }
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"

    if (!System.getenv("CI").isNullOrEmpty()) {
        publishAlways()
        tag("CI")
    }
}

pluginBundle {
    website = "https://www.playframework.com/"
    vcsUrl = "https://github.com/gradle/play"
    tags = listOf("playframework", "web", "java", "scala")
}