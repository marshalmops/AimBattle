package com.mcdead.aimbattle.screen.game.side.server.packet;

import com.mcdead.aimbattle.utils.UUIDUtils;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.UUID;

public class GameNetworkPacketInit extends GameNetworkPacket {
    private static int C_BYTES_COUNT = Integer.BYTES + Integer.BYTES * 2 + UUIDUtils.C_UUID_BYTES_COUNT;

    private int m_tps;
    private Dimension m_mapSize;
    private UUID m_localPlayerId;

    public GameNetworkPacketInit() {
        super(GameNetworkPacketType.INIT);

        m_tps = 0;
        m_mapSize = null;
        m_localPlayerId = null;
    }

    public GameNetworkPacketInit(final int tps,
                                 final Dimension mapSize,
                                 final UUID localPlayerId)
    {
        super(GameNetworkPacketType.INIT);

        m_tps = tps;
        m_mapSize = mapSize;
        m_localPlayerId = localPlayerId;
    }

    public int getTPS() {
        return m_tps;
    }

    public Dimension getMapSize() {
        return m_mapSize;
    }

    public UUID getLocalPlayerId() {
        return m_localPlayerId;
    }

    @Override
    public byte[] toBytes() {
        byte[] rawSuperBytes = super.toBytes();

        byte[] rawFieldBytes = ByteBuffer.allocate(C_BYTES_COUNT)
                                    .putInt(m_tps).putInt(m_mapSize.width)
                                    .putInt(m_mapSize.height).put(UUIDUtils.toBytes(m_localPlayerId)).array();

        byte[] rawBytes = new byte[rawSuperBytes.length + rawFieldBytes.length];

        System.arraycopy(rawSuperBytes, 0, rawBytes, 0, rawSuperBytes.length);
        System.arraycopy(rawFieldBytes, 0, rawBytes, rawSuperBytes.length, rawFieldBytes.length);

        return rawBytes;
    }

    @Override
    public boolean fromBytes(final ByteBuffer byteBuffer) {
        if (!super.fromBytes(byteBuffer)) return false;
        if (byteBuffer.remaining() < C_BYTES_COUNT) return false;

        m_tps = byteBuffer.getInt();
        m_mapSize = new Dimension();

        m_mapSize.width = byteBuffer.getInt();
        m_mapSize.height = byteBuffer.getInt();
        m_localPlayerId = UUIDUtils.fromBytes(byteBuffer);

        return true;
    }
}
