package com.mcdead.aimbattle.screen.game.side.server.packet;

import com.mcdead.aimbattle.utils.UUIDUtils;

import java.nio.ByteBuffer;
import java.util.UUID;

public abstract class GameNetworkPacketPlayer extends GameNetworkPacket {
    public static int C_BYTES_COUNT = UUIDUtils.C_UUID_BYTES_COUNT;

    private UUID m_playerUUID;

    public GameNetworkPacketPlayer(final GameNetworkPacketType type) {
        super(type);

        m_playerUUID = null;
    }

    public GameNetworkPacketPlayer(final GameNetworkPacketType type, final UUID playerUUID) {
        super(type);

        m_playerUUID = playerUUID;
    }

    public UUID getUUID() {
        return m_playerUUID;
    }

    @Override
    public byte[] toBytes() {
        byte[] rawSuperBytes = super.toBytes();

        byte[] rawFieldBytes = ByteBuffer.allocate(C_BYTES_COUNT)
                .put(UUIDUtils.toBytes(m_playerUUID)).array();

        byte[] rawBytes = new byte[rawSuperBytes.length + rawFieldBytes.length];

        System.arraycopy(rawSuperBytes, 0, rawBytes, 0, rawSuperBytes.length);
        System.arraycopy(rawFieldBytes, 0, rawBytes, rawSuperBytes.length, rawFieldBytes.length);

        return rawBytes;
    }

    @Override
    public boolean fromBytes(final ByteBuffer byteBuffer) {
        if (!super.fromBytes(byteBuffer)) return false;
        if (byteBuffer.remaining() < C_BYTES_COUNT) return false;

        m_playerUUID = UUIDUtils.fromBytes(byteBuffer);

        return true;
    }
}
