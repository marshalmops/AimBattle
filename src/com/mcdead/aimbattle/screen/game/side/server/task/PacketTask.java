package com.mcdead.aimbattle.screen.game.side.server.task;

import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacket;

public abstract class PacketTask extends Task {
    private GameNetworkPacket m_packet;

    public PacketTask(final GameNetworkPacket packet) {
        m_packet = packet;
    }

    public GameNetworkPacket getPacket() {
        return m_packet;
    }
}
