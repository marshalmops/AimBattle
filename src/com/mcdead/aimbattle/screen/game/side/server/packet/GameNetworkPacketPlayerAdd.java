package com.mcdead.aimbattle.screen.game.side.server.packet;

import java.util.UUID;

public class GameNetworkPacketPlayerAdd extends GameNetworkPacketPlayer {
    public GameNetworkPacketPlayerAdd() {
        super(GameNetworkPacketType.ADD_PLAYER);
    }

    public GameNetworkPacketPlayerAdd(final UUID playerUUID) {
        super(GameNetworkPacketType.ADD_PLAYER, playerUUID);
    }
}
