plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
}

gradlePlugin {
    plugins {
        register("integration-test-plugin") {
            id = "com.lightbend.play.integration-test"
            implementationClass = "com.lightbend.play.IntegrationTestPlugin"
        }
    }
}