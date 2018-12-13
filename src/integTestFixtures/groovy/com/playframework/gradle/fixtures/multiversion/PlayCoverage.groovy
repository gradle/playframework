package com.playframework.gradle.fixtures.multiversion

final class PlayCoverage {

    private PlayCoverage() {}

    public static final TargetPlatform DEFAULT = TargetPlatform.PLAY_2_6
    public static final List<TargetPlatform> ALL = [TargetPlatform.PLAY_2_4, TargetPlatform.PLAY_2_5, DEFAULT]
}
