plugins {
    `kotlin-dsl`
    id("org.gradle.kotlin-dsl.ktlint-convention") version "0.9.0"
}

repositories {
    maven {
        url = uri("https://repo.gradle.org/gradle/libs/")
        content {
            includeModule("org.ysb33r.gradle", "grolifant")
        }
    }
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.ajoberstar:gradle-git-publish:0.3.3")

    implementation("org.asciidoctor:asciidoctor-gradle-jvm:4.0.4")
}

gradlePlugin {
    plugins {
        register("integration-test-setup-plugin") {
            id = "org.gradle.playframework.test-setup"
            implementationClass = "org.gradle.playframework.TestSetupPlugin"
        }

        register("integration-test-fixtures-plugin") {
            id = "org.gradle.playframework.integration-test-fixtures"
            implementationClass = "org.gradle.playframework.IntegrationTestFixturesPlugin"
        }

        register("integration-test-plugin") {
            id = "org.gradle.playframework.integration-test"
            implementationClass = "org.gradle.playframework.IntegrationTestPlugin"
        }

        register("user-guide-plugin") {
            id = "org.gradle.playframework.user-guide"
            implementationClass = "org.gradle.playframework.UserGuidePlugin"
        }

        register("github-pages-plugin") {
            id = "org.gradle.playframework.github-pages"
            implementationClass = "org.gradle.playframework.GitHubPagesPlugin"
        }

        register("documentation-test-plugin") {
            id = "org.gradle.playframework.documentation-test"
            implementationClass = "org.gradle.playframework.DocumentationTestPlugin"
        }
    }
}
