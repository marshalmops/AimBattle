package com.mcdead.aimbattle.screen.game.side.server.packet;

import com.mcdead.aimbattle.screen.game.objects.object.GameObject;

public class GameNetworkPacketGameObjectActionSolidSpawn extends GameNetworkPacketGameObjectActionSolid {
    public GameNetworkPacketGameObjectActionSolidSpawn() {
        super(GameNetworkPacketType.SPAWN_OBJECT);
    }

    public GameNetworkPacketGameObjectActionSolidSpawn(final GameObject obj) {
        super(GameNetworkPacketType.SPAWN_OBJECT, obj);
    }
}
