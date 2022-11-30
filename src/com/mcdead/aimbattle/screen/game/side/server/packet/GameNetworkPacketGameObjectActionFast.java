package com.mcdead.aimbattle.screen.game.side.server.packet;

import java.nio.ByteBuffer;

public abstract class GameNetworkPacketGameObjectActionFast extends GameNetworkPacketGameObjectAction {
    public static int C_BYTES_COUNT = Integer.BYTES;

    private int m_objectId;

    public GameNetworkPacketGameObjectActionFast(final GameNetworkPacketType type) {
        super(type);

        m_objectId = 0;
    }

    public GameNetworkPacketGameObjectActionFast(final GameNetworkPacketType type, final int objectId) {
        super(type);

        m_objectId = objectId;
    }

    public int getObjectId() {
        return m_objectId;
    }

    @Override
    public byte[] toBytes() {
        byte[] rawSuperBytes = super.toBytes();

        byte[] rawFieldBytes = ByteBuffer.allocate(C_BYTES_COUNT)
                .putInt(m_objectId).array();

        byte[] rawBytes = new byte[rawSuperBytes.length + rawFieldBytes.length];

        System.arraycopy(rawSuperBytes, 0, rawBytes, 0, rawSuperBytes.length);
        System.arraycopy(rawFieldBytes, 0, rawBytes, rawSuperBytes.length, rawFieldBytes.length);

        return rawBytes;
    }

    @Override
    public boolean fromBytes(ByteBuffer byteBuffer) {
        if (!super.fromBytes(byteBuffer)) return false;
        if (byteBuffer.remaining() < C_BYTES_COUNT) return false;

        m_objectId = byteBuffer.getInt();

        return true;
    }
}
