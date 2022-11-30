package com.mcdead.aimbattle.screen.game.side.server.packet;

import com.mcdead.aimbattle.ByteConvertable;

import java.nio.ByteBuffer;

public abstract class GameNetworkPacket implements ByteConvertable {
    public static int C_BYTES_COUNT = GameNetworkPacketType.C_BYTES_COUNT;

    private GameNetworkPacketType m_type;

    public GameNetworkPacket(final GameNetworkPacketType type) {
        m_type = type;
    }

    public byte[] toBytes() {
        return ByteBuffer.allocate(C_BYTES_COUNT)
                .put(m_type.getId()).array();
    }

    public boolean fromBytes(final ByteBuffer byteBuffer) {
        if (byteBuffer.remaining() < C_BYTES_COUNT - GameNetworkPacketType.C_BYTES_COUNT)
            return false;

        return true;
    }

    public GameNetworkPacketType getType() {
        return m_type;
    }
}
