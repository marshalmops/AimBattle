package com.mcdead.aimbattle.screen.game.side.server.packet;

import com.mcdead.aimbattle.screen.game.objects.object.GameObject;

public class GameNetworkPacketGameObjectActionSolidModify extends GameNetworkPacketGameObjectActionSolid {
    public GameNetworkPacketGameObjectActionSolidModify() {
        super(GameNetworkPacketType.MODIFY_OBJECT);
    }

    public GameNetworkPacketGameObjectActionSolidModify(final GameObject obj) {
        super(GameNetworkPacketType.MODIFY_OBJECT, obj);
    }
}
