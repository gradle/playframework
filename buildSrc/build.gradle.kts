plugins {
    `kotlin-dsl`
    id("org.gradle.kotlin-dsl.ktlint-convention") version "0.2.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.asciidoctor:asciidoctor-gradle-plugin:1.5.9.1")
    implementation("org.ajoberstar:gradle-git-publish:0.3.3")
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

        register("github-pages-plugin") {
            id = "com.playframework.gradle.github-pages"
            implementationClass = "com.playframework.gradle.GitHubPagesPlugin"
        }

        register("documentation-test-plugin") {
            id = "com.playframework.gradle.documentation-test"
            implementationClass = "com.playframework.gradle.DocumentationTestPlugin"
        }
    }
}