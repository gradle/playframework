<#if playVersion == "2.7" || playVersion == "2.6">
plugins {
    id 'org.gradle.playframework'
}

dependencies {
    implementation "com.google.guava:guava:17.0"
    implementation "com.typesafe.play:play-guice_2.12:2.6.15"
    implementation "ch.qos.logback:logback-classic:1.2.3"
    testImplementation "commons-lang:commons-lang:2.6"
}

// repositories added in PlayApp class

<#else>
plugins {
    id 'org.gradle.playframework'
}

dependencies {
    implementation "com.google.guava:guava:17.0"
    testImplementation "commons-lang:commons-lang:2.6"
}

// repositories added in PlayApp class

</#if>
