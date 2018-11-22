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
        register("test-fixtures-plugin") {
            id = "com.lightbend.play.test-fixtures"
            implementationClass = "com.lightbend.play.TestFixturesPlugin"
        }
    }
    plugins {
        register("integration-test-plugin") {
            id = "com.lightbend.play.integration-test"
            implementationClass = "com.lightbend.play.IntegrationTestPlugin"
        }
    }
}