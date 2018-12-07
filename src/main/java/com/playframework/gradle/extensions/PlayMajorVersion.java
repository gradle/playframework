package com.playframework.gradle.extensions;

import org.gradle.api.InvalidUserDataException;
import org.gradle.util.CollectionUtils;
import org.gradle.util.VersionNumber;

import java.util.Arrays;
import java.util.List;

public enum PlayMajorVersion {
    PLAY_2_3_X("2.3.x", "2.11", "2.10"),
    PLAY_2_4_X("2.4.x", "2.11", "2.10"),
    PLAY_2_5_X("2.5.x", "2.11"),
    PLAY_2_6_X("2.6.x", "2.12", "2.11");

    private final String name;
    private final List<String> compatibleScalaVersions;

    PlayMajorVersion(String name, String... compatibleScalaVersions) {
        this.name = name;
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
                playVersion, CollectionUtils.join(", ", values())));
    }
}
