package org.gradle.playframework.tools.internal.run;

import org.gradle.deployment.internal.Deployment;
import org.gradle.deployment.internal.DeploymentHandle;

import javax.inject.Inject;
import java.net.InetSocketAddress;

public class PlayApplicationDeploymentHandle implements DeploymentHandle {
    private final PlayRunSpec spec;
    private final PlayApplicationRunner playApplicationRunner;
    private PlayApplication playApplication;

    @Inject
    public PlayApplicationDeploymentHandle(PlayRunSpec spec, PlayApplicationRunner playApplicationRunner) {
        this.spec = spec;
        this.playApplicationRunner = playApplicationRunner;
    }

    @Override
    public boolean isRunning() {
        return playApplication != null && playApplication.isRunning();
    }

    @Override
    public void start(Deployment deployment) {
        playApplication = playApplicationRunner.start(spec, deployment);
    }

    public InetSocketAddress getPlayAppAddress() {
        if (isRunning()) {
            return playApplication.getPlayAppAddress();
        }
        return null;
    }

    @Override
    public void stop() {
        playApplication.stop();
    }
}

