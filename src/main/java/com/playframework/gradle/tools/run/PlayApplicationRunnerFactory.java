package com.playframework.gradle.tools.run;

import com.playframework.gradle.extensions.PlayPlatform;
import com.playframework.gradle.extensions.internal.PlayMajorVersion;
import org.gradle.internal.fingerprint.classpath.ClasspathFingerprinter;
import org.gradle.process.internal.worker.WorkerProcessFactory;

public class PlayApplicationRunnerFactory {
    public static PlayApplicationRunner create(PlayPlatform playPlatform, WorkerProcessFactory workerFactory, ClasspathFingerprinter fingerprinter) {
        return new PlayApplicationRunner(workerFactory, createPlayRunAdapter(playPlatform), fingerprinter);
    }

    public static VersionedPlayRunAdapter createPlayRunAdapter(PlayPlatform playPlatform) {
        switch (PlayMajorVersion.forPlatform(playPlatform)) {
            case PLAY_2_4_X:
                return new PlayRunAdapterV24X();
            case PLAY_2_5_X:
                return new PlayRunAdapterV25X();
            case PLAY_2_6_X:
                return new PlayRunAdapterV26X();
            case PLAY_2_3_X:
            default:
                return new PlayRunAdapterV23X();
        }
    }
}
