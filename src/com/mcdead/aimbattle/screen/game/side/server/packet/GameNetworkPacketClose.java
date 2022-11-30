package com.mcdead.aimbattle.screen.game.side.server.packet;

public class GameNetworkPacketClose extends GameNetworkPacket {
    public GameNetworkPacketClose() {
        super(GameNetworkPacketType.CLOSE);
    }
}
