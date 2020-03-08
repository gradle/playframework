plugins {
    id 'org.gradle.playframework'
    id 'org.gradle.playframework-less'
    id 'org.gradle.playframework-webjars'
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

dependencies {
    webJar 'org.webjars.bower:css-reset:2.5.1'
}

<#if playVersion == "2.7" || playVersion == "2.6">
dependencies {
    implementation "com.typesafe.play:play-guice_2.12:2.6.15"
    implementation "ch.qos.logback:logback-classic:1.2.3"
}
</#if>
