package com.mcdead.aimbattle.screen.game.side.server.packet;

public enum GameNetworkPacketType {
    INVALID((byte) -1),
    INIT((byte)0), START((byte)1), SPAWN_OBJECT((byte)2),
    MODIFY_OBJECT((byte)3), PLAYER_INPUT((byte)4), ADD_PLAYER((byte)5),
    REMOVE_PLAYER((byte)6), LEAVE((byte)7), END((byte)8),
    RESTART((byte)9), CLOSE((byte)10),
    REMOVE_OBJECT((byte)11), SET_PLAYER_SCORE((byte)12), PAUSE((byte)13),
    RESUME((byte)14);

    public static int C_BYTES_COUNT = Byte.BYTES * 1;

    private byte m_id;

    private GameNetworkPacketType(final byte id) {
        m_id = id;
    }

    public byte getId() {
        return m_id;
    }

    public static GameNetworkPacketType getTypeById(final byte id) {
        if (id < 0) return INVALID;

        for (final GameNetworkPacketType type : values())
            if (type.getId() == id) return type;

        return INVALID;
    }
}
