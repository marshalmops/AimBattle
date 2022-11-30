package com.mcdead.aimbattle.screen.game.side.server.packet;

public class GameNetworkPacketLeave extends GameNetworkPacket {
    public GameNetworkPacketLeave() {
        super(GameNetworkPacketType.LEAVE);
    }
}
