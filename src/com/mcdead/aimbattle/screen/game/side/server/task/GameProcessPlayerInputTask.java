package com.mcdead.aimbattle.screen.game.side.server.task;

import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacketPlayerInput;

import java.util.UUID;

public class GameProcessPlayerInputTask extends PacketTask implements GameTaskInterface {
    private UUID m_clientUUID;

    public GameProcessPlayerInputTask(final GameNetworkPacketPlayerInput packet, final UUID clientUUID) {
        super(packet);

        m_clientUUID = clientUUID;
    }

    public UUID getClientUUID() {
        return m_clientUUID;
    }

    @Override
    public GameTaskType getType() {
        return GameTaskType.PROCESS_PLAYER_INPUT;
    }
}
