package com.mcdead.aimbattle.screen.game.side.server.packet;

import java.util.UUID;

public class GameNetworkPacketPlayerRemove extends GameNetworkPacketPlayer {
    public GameNetworkPacketPlayerRemove() {
        super(GameNetworkPacketType.REMOVE_PLAYER);
    }

    public GameNetworkPacketPlayerRemove(final UUID playerUUID) {
        super(GameNetworkPacketType.REMOVE_PLAYER, playerUUID);
    }
}
