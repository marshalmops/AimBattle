package com.mcdead.aimbattle.screen.game.input;

import java.nio.ByteBuffer;

public class GameInputMouseClick extends GameInput {
    public static int C_BYTES_COUNT = Integer.BYTES * 2;

    private int m_x;
    private int m_y;

    public GameInputMouseClick() {
        super(GameInputType.MOUSE_CLICK);

        m_x = -1;
        m_y = -1;
    }

    public GameInputMouseClick(final int x, final int y) {
        super(GameInputType.MOUSE_CLICK);

        m_x = x;
        m_y = y;
    }

    public int getX() {
        return m_x;
    }

    public int getY() {
        return m_y;
    }

    @Override
    public byte[] toBytes() {
        byte[] rawSuperBytes = super.toBytes();

        byte[] rawFieldBytes = ByteBuffer.allocate(C_BYTES_COUNT)
                .putInt(m_x).putInt(m_y).array();

        byte[] rawBytes = new byte[rawSuperBytes.length + rawFieldBytes.length];

        System.arraycopy(rawSuperBytes, 0, rawBytes, 0, rawSuperBytes.length);
        System.arraycopy(rawFieldBytes, 0, rawBytes, rawSuperBytes.length, rawFieldBytes.length);

        return rawBytes;
    }

    @Override
    public boolean fromBytes(ByteBuffer byteBuffer) {
        if (!super.fromBytes(byteBuffer)) return false;
        if (byteBuffer.remaining() < C_BYTES_COUNT) return false;

        int rawX = byteBuffer.getInt();
        int rawY = byteBuffer.getInt();

        if (rawX < 0 || rawY < 0) return false;

        m_x = rawX;
        m_y = rawY;

        return true;
    }
}
