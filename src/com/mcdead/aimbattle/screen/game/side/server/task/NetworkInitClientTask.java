package com.mcdead.aimbattle.screen.game.side.server.task;

import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacket;

import java.net.Socket;
import java.util.List;
import java.util.UUID;

public class NetworkInitClientTask extends NetworkPacketTask {
    private Socket m_socket;
    private UUID m_uuid;

    public NetworkInitClientTask(final Socket socket, final UUID uuid,
                                 final GameNetworkPacket packet)
    {
        super(packet, PacketDestinationType.INITIATOR);

        m_socket = socket;
        m_uuid = uuid;
    }

    public Socket getSocket() {
        return m_socket;
    }

    public UUID getUUID() {
        return m_uuid;
    }

    @Override
    public TaskType getType() {
        return NetworkTaskType.INIT_CLIENT;
    }
}
