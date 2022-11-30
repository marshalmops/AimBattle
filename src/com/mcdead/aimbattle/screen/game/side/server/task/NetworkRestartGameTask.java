package com.mcdead.aimbattle.screen.game.side.server.task;

import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacket;

import java.util.List;
import java.util.UUID;

public class NetworkRestartGameTask extends NetworkPacketTask {
    public NetworkRestartGameTask(final GameNetworkPacket packet) {
        super(packet, PacketDestinationType.BROADCAST);
    }

    @Override
    public TaskType getType() {
        return NetworkTaskType.RESTART_GAME;
    }
}
