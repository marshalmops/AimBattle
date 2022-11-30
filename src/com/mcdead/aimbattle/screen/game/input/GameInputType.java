package com.mcdead.aimbattle.screen.game.input;

public enum GameInputType {
    INVALID((byte) 0), MOUSE_CLICK((byte) 1);

    public static int C_BYTES_COUNT = Byte.BYTES;

    private byte m_id;

    private GameInputType(final byte id) {
        m_id = id;
    }

    public byte getId() {
        return m_id;
    }

    public static GameInputType getTypeById(final byte id) {
        if (id < 0) return INVALID;

        for (final GameInputType type : values())
            if (type.getId() == id) return type;

        return INVALID;
    }
}
