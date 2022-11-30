package com.mcdead.aimbattle.screen.game.side.server.packet;

public class GameNetworkPacketRestart extends GameNetworkPacket {
    public GameNetworkPacketRestart() {
        super(GameNetworkPacketType.RESTART);
    }
}
