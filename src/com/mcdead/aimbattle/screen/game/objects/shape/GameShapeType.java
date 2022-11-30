package com.mcdead.aimbattle.screen.game.objects.shape;

public enum GameShapeType {
    INVALID((byte)-1), CIRCLE((byte)0);

    public static int C_BYTES_COUNT = Byte.BYTES;

    private byte m_id;

    private GameShapeType(final byte id) {
        m_id = id;
    }

    public byte getId() {
        return m_id;
    }

    public static GameShapeType getTypeById(final byte id) {
        if (id < 0) return INVALID;

        for (final GameShapeType type : values())
            if (type.getId() == id) return type;

        return INVALID;
    }
}
