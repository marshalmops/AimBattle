package com.mcdead.aimbattle.screen.game.side.server.task;

public class NetworkExitTask extends Task implements NetworkTaskInterface {
    public NetworkExitTask() {

    }

    @Override
    public NetworkTaskType getType() {
        return NetworkTaskType.EXIT;
    }
}
