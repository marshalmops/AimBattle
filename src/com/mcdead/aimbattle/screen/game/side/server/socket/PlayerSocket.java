package com.mcdead.aimbattle.screen.game.side.server.socket;

import java.net.Socket;
import java.util.UUID;

public class PlayerSocket extends ClientSocket {
    private UUID m_uuid;

    public PlayerSocket(final Socket socket, final UUID uuid) {
        super(socket);

        m_uuid = uuid;
    }

    public UUID getUUID() {
        return m_uuid;
    }
}
