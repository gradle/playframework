plugins {
    id 'org.gradle.playframework'
}

// repositories added in PlayApp class

sourceSets {
    main {
        twirl {
            defaultImports = org.gradle.playframework.sourcesets.TwirlImports.JAVA
            srcDir "templates"
            include "jva/**/*"
        }
    }
}

<#if playVersion == "2.7" || playVersion == "2.6">
dependencies {
    implementation "com.typesafe.play:play-guice_2.12:2.6.15"
    implementation "ch.qos.logback:logback-classic:1.2.3"
}
</#if>
