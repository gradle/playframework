package org.gradle.playframework.tools.internal.twirl;

import org.gradle.playframework.extensions.PlayPlatform;
import org.gradle.playframework.extensions.internal.PlayMajorVersion;

public class TwirlCompilerFactory {

    public static TwirlCompiler create(PlayPlatform playPlatform) {
        return new TwirlCompiler(createAdapter(playPlatform));
    }

    public static VersionedTwirlCompilerAdapter createAdapter(PlayPlatform playPlatform) {
        String scalaCompatibilityVersion = playPlatform.getScalaCompatibilityVersion().get();
        VersionedPlayTwirlAdapter playTwirlAdapter = createPlayTwirlAdapter(playPlatform);
        switch (PlayMajorVersion.forPlatform(playPlatform)) {
            case PLAY_2_3_X:
                return new TwirlCompilerAdapterV10X("1.0.4", scalaCompatibilityVersion, playTwirlAdapter);
            case PLAY_2_4_X:
            case PLAY_2_5_X:
                return new TwirlCompilerAdapterV10X("1.1.1", scalaCompatibilityVersion, playTwirlAdapter);
            case PLAY_2_6_X:
                return new TwirlCompilerAdapterV13X("1.3.13", scalaCompatibilityVersion, playTwirlAdapter);
            case PLAY_2_7_X:
            case PLAY_2_8_X:
                return new TwirlCompilerAdapterV13X("1.5.1", scalaCompatibilityVersion, playTwirlAdapter);
            case PLAY_2_9_X:
            default:
                // https://www.playframework.com/documentation/3.0.x/Migration29#Updated-libraries
                return new TwirlCompilerAdapterV13X("1.6.0", scalaCompatibilityVersion, playTwirlAdapter);
        }
    }

    public static VersionedPlayTwirlAdapter createPlayTwirlAdapter(PlayPlatform playPlatform) {
        switch (PlayMajorVersion.forPlatform(playPlatform)) {
            case PLAY_2_3_X:
            case PLAY_2_4_X:
            case PLAY_2_5_X:
                return new PlayTwirlAdapterV23X();
            case PLAY_2_6_X:
            case PLAY_2_7_X:
                return new PlayTwirlAdapterV26X();
            case PLAY_2_8_X:
            case PLAY_2_9_X:
            default:
                return new PlayTwirlAdapterV28X();
        }
    }

}
