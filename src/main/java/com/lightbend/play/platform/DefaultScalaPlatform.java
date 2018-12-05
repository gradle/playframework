package com.lightbend.play.platform;

import org.gradle.util.VersionNumber;

public class DefaultScalaPlatform implements ScalaPlatform {
    public static final String DEFAULT_SCALA_PLATFORM_VERSION = "2.10.7";
    private final String scalaCompatibilityVersion;
    private final String scalaVersion;

    public DefaultScalaPlatform() {
        this(DEFAULT_SCALA_PLATFORM_VERSION); // default Scala version
    }

    public DefaultScalaPlatform(String scalaVersion) {
        this(VersionNumber.parse(scalaVersion));
    }

    public DefaultScalaPlatform(VersionNumber versionNumber) {
        this.scalaVersion = versionNumber.getMajor() + "." + versionNumber.getMinor() + "." + versionNumber.getMicro();
        this.scalaCompatibilityVersion = versionNumber.getMajor() + "." + versionNumber.getMinor();
    }

    @Override
    public String getScalaVersion() {
        return scalaVersion;
    }

    @Override
    public String getScalaCompatibilityVersion() {
        return scalaCompatibilityVersion;
    }

    @Override
    public String getDisplayName() {
        return "Scala Platform (Scala " + scalaVersion + ")";
    }

    @Override
    public String getName() {
        return "ScalaPlatform" + scalaVersion;
    }
}

