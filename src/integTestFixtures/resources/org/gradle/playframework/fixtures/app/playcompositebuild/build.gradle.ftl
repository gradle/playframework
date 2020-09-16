<#if playVersion == "2.7" || playVersion == "2.6">
plugins {
    id 'org.gradle.playframework'
}

dependencies {
    implementation "org.my:java-lib:1.0"
    implementation "com.typesafe.play:play-guice_2.12:2.6.15"
    implementation "ch.qos.logback:logback-classic:1.2.3"
}

<#else>
plugins {
    id 'org.gradle.playframework'
}

dependencies {
    implementation "org.my:java-lib:1.0"
}

</#if>
