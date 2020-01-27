package org.gradle.playframework.extensions.internal;

import org.gradle.playframework.extensions.PlayPlatform;
import org.gradle.api.InvalidUserDataException;
import org.gradle.util.VersionNumber;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PlayMajorVersion {
    PLAY_2_3_X("2.3.x", true, "2.11", "2.10"),
    PLAY_2_4_X("2.4.x", true, "2.11", "2.10"),
    PLAY_2_5_X("2.5.x", true,"2.11"),
    PLAY_2_6_X("2.6.x", true, "2.12", "2.11"),
    PLAY_2_7_X("2.7.x", false, "2.13", "2.12", "2.11");
    // Not supported yet
    // PLAY_2_8_X("2.8.x", false, "2.13", "2.12");

    private final String name;
    private final boolean supportForStaticRoutes;
    private final List<String> compatibleScalaVersions;

    PlayMajorVersion(String name, boolean supportForStaticRoutes, String... compatibleScalaVersions) {
        this.name = name;
        this.supportForStaticRoutes = supportForStaticRoutes;
        this.compatibleScalaVersions = Arrays.asList(compatibleScalaVersions);
    }

    public void validateCompatible(String scalaCompatibilityVersion) {
        if (!compatibleScalaVersions.contains(scalaCompatibilityVersion)) {
            throw new InvalidUserDataException(
                    String.format("Play versions %s are not compatible with Scala platform %s. Compatible Scala platforms are %s.",
                            name, scalaCompatibilityVersion, compatibleScalaVersions));
        }
    }

    public String getDefaultScalaPlatform() {
        return compatibleScalaVersions.get(0);
    }

    public static PlayMajorVersion forPlatform(PlayPlatform targetPlatform) {
        String playVersion = targetPlatform.getPlayVersion().get();
        return forPlayVersion(playVersion);
    }

    public static PlayMajorVersion forPlayVersion(String playVersion) {
        VersionNumber versionNumber = VersionNumber.parse(playVersion);
        if (versionNumber.getMajor() == 2) {
            int index = versionNumber.getMinor() - 3;
            if (index < 0 || index >= values().length) {
                throw invalidVersion(playVersion);
            }
            return values()[index];
        }
        throw invalidVersion(playVersion);
    }

    private static InvalidUserDataException invalidVersion(String playVersion) {
        return new InvalidUserDataException(String.format("Not a supported Play version: %s. This plugin is compatible with: [%s].",
                playVersion, Arrays.stream(values()).map(Object::toString).collect(Collectors.joining(", "))));
    }

    public boolean hasSupportForStaticRoutesGenerator() {
        return supportForStaticRoutes;
    }
}
