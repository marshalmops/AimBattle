package com.mcdead.aimbattle.screen.game.side.server.task;

import java.net.Socket;

public class GameAddPlayerTask extends Task implements GameTaskInterface {
    private Socket m_socket;

    public GameAddPlayerTask(final Socket socket) {
        m_socket = socket;
    }

    public Socket getSocket() {
        return m_socket;
    }

    @Override
    public TaskType getType() {
        return GameTaskType.ADD_PLAYER;
    }
}
