package com.mcdead.aimbattle.screen.game.objects.object;

import com.mcdead.aimbattle.screen.game.objects.shape.GameShapeType;

public enum GameObjectType {
    INVALID((byte)-1), AIM((byte)0);

    public static int C_BYTES_COUNT = Byte.BYTES;

    private byte m_id;

    private GameObjectType(final byte id) {
        m_id = id;
    }

    public byte getId() {
        return m_id;
    }

    public static GameObjectType getTypeById(final byte id) {
        if (id < 0) return INVALID;

        for (final GameObjectType type : values())
            if (type.m_id == id) return type;

        return INVALID;
    }
}
