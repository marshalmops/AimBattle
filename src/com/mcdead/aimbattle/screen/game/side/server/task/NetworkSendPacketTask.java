package com.mcdead.aimbattle.screen.game.side.server.task;

import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacket;

import java.util.List;
import java.util.UUID;

public class NetworkSendPacketTask extends NetworkPacketTask {
    public NetworkSendPacketTask(final GameNetworkPacket packet, final List<UUID> dstList) {
        super(packet, dstList);
    }

    public NetworkSendPacketTask(final GameNetworkPacket packet, final PacketDestinationType dstType) {
        super(packet, dstType);
    }

    @Override
    public TaskType getType() {
        return NetworkTaskType.SEND_PACKET;
    }
}
