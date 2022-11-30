package com.mcdead.aimbattle.screen.game.side.server.packet;

import java.util.UUID;

public abstract class GameNetworkPacketPlayerModify extends GameNetworkPacketPlayer{
    public GameNetworkPacketPlayerModify(final GameNetworkPacketType type) {
        super(type);
    }

    public GameNetworkPacketPlayerModify(final GameNetworkPacketType type, final UUID playerUUID) {
        super(type, playerUUID);
    }
}
