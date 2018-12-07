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
            implementationClass = "com.playframework.gradle.plugins.PlayTwirlPlugin"
        }
    }
    plugins {
        register("play-routes-plugin") {
            id = "com.playframework.play-routes"
            implementationClass = "com.playframework.gradle.plugins.PlayRoutesPlugin"
        }
    }
    plugins {
        register("play-application-plugin") {
            id = "com.playframework.play-application"
            implementationClass = "com.playframework.gradle.plugins.PlayApplicationPlugin"
        }
    }
    plugins {
        register("play-javascript-plugin") {
            id = "com.playframework.play-javascript"
            implementationClass = "com.playframework.gradle.plugins.PlayJavaScriptPlugin"
        }
    }
    plugins {
        register("play-coffeescript-plugin") {
            id = "com.playframework.play-coffeescript"
            implementationClass = "com.playframework.gradle.plugins.PlayCoffeeScriptPlugin"
        }
    }
    plugins {
        register("play-test-plugin") {
            id = "com.playframework.play-test"
            implementationClass = "com.playframework.gradle.plugins.PlayTestPlugin"
        }
    }
    plugins {
        register("play-distribution-plugin") {
            id = "com.playframework.play-distribution"
            implementationClass = "com.playframework.gradle.plugins.PlayDistributionPlugin"
        }
    }
    plugins {
        register("play-ide-plugin") {
            id = "com.playframework.play-ide"
            implementationClass = "com.playframework.gradle.plugins.PlayIdePlugin"
        }
    }
    plugins {
        register("play-plugin") {
            id = "com.playframework.play"
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

gradlePlugin {
    plugins {
        create("playPlugin") {
            id = "com.playframework.play"
            displayName = "Play Plugin"
            description = "Play plugin that supports building, testing and running Play applications with Gradle."
            implementationClass = "com.playframework.gradle.plugins.PlayPlugin"
        }
    }
}