plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.asciidoctor:asciidoctor-gradle-plugin:1.5.9.1")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

gradlePlugin {
    plugins {
        register("integration-test-setup-plugin") {
            id = "com.playframework.gradle.test-setup"
            implementationClass = "com.playframework.gradle.TestSetupPlugin"
        }

        register("integration-test-fixtures-plugin") {
            id = "com.playframework.gradle.integration-test-fixtures"
            implementationClass = "com.playframework.gradle.IntegrationTestFixturesPlugin"
        }

        register("integration-test-plugin") {
            id = "com.playframework.gradle.integration-test"
            implementationClass = "com.playframework.gradle.IntegrationTestPlugin"
        }

        register("user-guide-plugin") {
            id = "com.playframework.gradle.user-guide"
            implementationClass = "com.playframework.gradle.UserGuidePlugin"
        }
    }
}