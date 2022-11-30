package com.mcdead.aimbattle.screen.game.side.server.task;

public class NetworkCloseProcessingTask extends Task implements NetworkTaskInterface {
    public NetworkCloseProcessingTask() {
        //super(packet, PacketDestinationType.BROADCAST);
    }

    @Override
    public TaskType getType() {
        return NetworkTaskType.CLOSE;
    }
}
