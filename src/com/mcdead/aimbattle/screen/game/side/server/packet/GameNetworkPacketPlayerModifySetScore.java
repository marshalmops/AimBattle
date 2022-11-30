package com.mcdead.aimbattle.screen.game.side.server.packet;

import java.nio.ByteBuffer;
import java.util.UUID;

public class GameNetworkPacketPlayerModifySetScore extends GameNetworkPacketPlayerModify {
    public static int C_BYTES_COUNT = Integer.BYTES;

    private int m_score;

    public GameNetworkPacketPlayerModifySetScore() {
        super(GameNetworkPacketType.SET_PLAYER_SCORE);

        m_score = 0;
    }

    public GameNetworkPacketPlayerModifySetScore(final UUID playerUUID, final int score) {
        super(GameNetworkPacketType.SET_PLAYER_SCORE, playerUUID);

        m_score = score;
    }

    public int getScore() {
        return m_score;
    }

    @Override
    public byte[] toBytes() {
        byte[] rawSuperBytes = super.toBytes();

        byte[] rawFieldBytes = ByteBuffer.allocate(C_BYTES_COUNT).putInt(m_score).array();

        byte[] rawBytes = new byte[rawSuperBytes.length + rawFieldBytes.length];

        System.arraycopy(rawSuperBytes, 0, rawBytes, 0, rawSuperBytes.length);
        System.arraycopy(rawFieldBytes, 0, rawBytes, rawSuperBytes.length, rawFieldBytes.length);

        return rawBytes;
    }

    @Override
    public boolean fromBytes(ByteBuffer byteBuffer) {
        if (!super.fromBytes(byteBuffer)) return false;
        if (byteBuffer.remaining() < C_BYTES_COUNT) return false;

        m_score = byteBuffer.getInt();

        return true;
    }
}
