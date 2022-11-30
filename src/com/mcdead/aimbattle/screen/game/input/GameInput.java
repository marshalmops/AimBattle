package com.mcdead.aimbattle.screen.game.input;

import com.mcdead.aimbattle.ByteConvertable;

import java.nio.ByteBuffer;

public abstract class GameInput implements ByteConvertable {
    public static int C_BYTES_COUNT = GameInputType.C_BYTES_COUNT;
    private GameInputType m_type;

    public GameInput(final GameInputType type) {
        m_type = type;
    }

    public GameInputType getType() {
        return m_type;
    }

    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(C_BYTES_COUNT)
                .put(m_type.getId()).array();
    }

    @Override
    public boolean fromBytes(ByteBuffer byteBuffer) {
        if (byteBuffer.remaining() < C_BYTES_COUNT - GameInputType.C_BYTES_COUNT) return false;

        return true;
    }
}
