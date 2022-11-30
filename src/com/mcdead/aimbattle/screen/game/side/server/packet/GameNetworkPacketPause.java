package com.mcdead.aimbattle.screen.game.side.server.packet;

public class GameNetworkPacketPause extends GameNetworkPacket {
    public GameNetworkPacketPause() {
        super(GameNetworkPacketType.PAUSE);
    }
}
