plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
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
    }
    plugins {
        register("integration-test-fixtures-plugin") {
            id = "com.playframework.gradle.integration-test-fixtures"
            implementationClass = "com.playframework.gradle.IntegrationTestFixturesPlugin"
        }
    }
    plugins {
        register("integration-test-plugin") {
            id = "com.playframework.gradle.integration-test"
            implementationClass = "com.playframework.gradle.IntegrationTestPlugin"
        }
    }
}