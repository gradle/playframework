plugins {
    groovy
    `java-gradle-plugin`
    `build-scan`
    id("com.lightbend.play.integration-test-fixtures")
    id("com.lightbend.play.integration-test")
}

group = "com.lightbend"
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
            id = "com.lightbend.play-twirl"
            implementationClass = "com.lightbend.play.plugins.PlayTwirlPlugin"
        }
    }
    plugins {
        register("play-routes-plugin") {
            id = "com.lightbend.play-routes"
            implementationClass = "com.lightbend.play.plugins.PlayRoutesPlugin"
        }
    }
    plugins {
        register("play-application-plugin") {
            id = "com.lightbend.play-application"
            implementationClass = "com.lightbend.play.plugins.PlayApplicationPlugin"
        }
    }
    plugins {
        register("play-javascript-plugin") {
            id = "com.lightbend.play-javascript"
            implementationClass = "com.lightbend.play.plugins.PlayJavaScriptPlugin"
        }
    }
    plugins {
        register("play-coffeescript-plugin") {
            id = "com.lightbend.play-coffeescript"
            implementationClass = "com.lightbend.play.plugins.PlayCoffeeScriptPlugin"
        }
    }
    plugins {
        register("play-test-plugin") {
            id = "com.lightbend.play-test"
            implementationClass = "com.lightbend.play.plugins.PlayTestPlugin"
        }
    }
    plugins {
        register("play-distribution-plugin") {
            id = "com.lightbend.play-distribution"
            implementationClass = "com.lightbend.play.plugins.PlayDistributionPlugin"
        }
    }
    plugins {
        register("play-plugin") {
            id = "com.lightbend.play"
            implementationClass = "com.lightbend.play.plugins.PlayPlugin"
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