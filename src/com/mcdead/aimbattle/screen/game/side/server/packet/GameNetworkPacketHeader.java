package com.mcdead.aimbattle.screen.game.side.server.packet;

public class GameNetworkPacketHeader {
    public static int C_SIZE = Integer.BYTES;

    private int m_length;

    public GameNetworkPacketHeader(final int length) {
        m_length = length;
    }

    public int getLength() {
        return m_length;
    }
}