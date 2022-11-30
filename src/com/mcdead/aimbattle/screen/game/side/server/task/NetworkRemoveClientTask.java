package com.mcdead.aimbattle.screen.game.side.server.task;

import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacket;

import java.util.List;
import java.util.UUID;

public class NetworkRemoveClientTask extends NetworkPacketTask {
    private UUID m_clientToRemoveUUID;

    public NetworkRemoveClientTask(final UUID clientToRemoveUUID,
                                   final GameNetworkPacket packet)
    {
        super(packet, PacketDestinationType.BROADCAST);

        m_clientToRemoveUUID = clientToRemoveUUID;
    }

    public UUID getClientToRemoveUUID() {
        return m_clientToRemoveUUID;
    }

    @Override
    public TaskType getType() {
        return NetworkTaskType.REMOVE_CLIENT;
    }
}
