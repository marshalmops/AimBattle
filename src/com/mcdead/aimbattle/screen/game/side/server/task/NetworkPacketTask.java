package com.mcdead.aimbattle.screen.game.side.server.task;

import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class NetworkPacketTask extends PacketTask implements NetworkTaskInterface {
    private List<UUID> m_dstList;
    private PacketDestinationType m_dstType;

    public NetworkPacketTask(final GameNetworkPacket packet, final List<UUID> dstList) {
        super(packet);

        m_dstList = dstList;
        m_dstType = PacketDestinationType.SPECIFIED_BY_LIST;
    }

    public NetworkPacketTask(final GameNetworkPacket packet, final PacketDestinationType dstType) {
        super(packet);

        m_dstList = null;
        m_dstType = dstType;
    }

    public List<UUID> getDestinationList() {
        return m_dstList;
    }

    public PacketDestinationType getDestinationType() {
        return m_dstType;
    }
}
