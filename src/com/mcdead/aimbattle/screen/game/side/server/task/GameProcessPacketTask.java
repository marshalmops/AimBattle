package com.mcdead.aimbattle.screen.game.side.server.task;

import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacket;

public class GameProcessPacketTask extends PacketTask implements GameTaskInterface {
    public GameProcessPacketTask(final GameNetworkPacket packet) {
        super(packet);
    }

    @Override
    public TaskType getType() {
        return GameTaskType.PROCESS_PACKET;
    }
}
