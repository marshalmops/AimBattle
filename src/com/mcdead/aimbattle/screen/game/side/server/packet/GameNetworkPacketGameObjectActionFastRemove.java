package com.mcdead.aimbattle.screen.game.side.server.packet;

public class GameNetworkPacketGameObjectActionFastRemove extends GameNetworkPacketGameObjectActionFast {
    public GameNetworkPacketGameObjectActionFastRemove() {
        super(GameNetworkPacketType.REMOVE_OBJECT);
    }

    public GameNetworkPacketGameObjectActionFastRemove(final int objectId) {
        super(GameNetworkPacketType.REMOVE_OBJECT, objectId);
    }
}
