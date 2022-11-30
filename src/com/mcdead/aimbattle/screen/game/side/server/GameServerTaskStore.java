package com.mcdead.aimbattle.screen.game.side.server;

import com.mcdead.aimbattle.screen.game.side.server.task.GameTaskInterface;
import com.mcdead.aimbattle.screen.game.side.server.task.NetworkTaskInterface;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameServerTaskStore {
    private BlockingQueue<NetworkTaskInterface> m_netTaskQueue;
    private BlockingQueue<GameTaskInterface> m_gameTaskQueue;

    public GameServerTaskStore() {
        m_netTaskQueue = new LinkedBlockingQueue<>();
        m_gameTaskQueue = new LinkedBlockingQueue<>();
    }

    public BlockingQueue<NetworkTaskInterface> getNetTaskQueue() {
        return m_netTaskQueue;
    }

    public BlockingQueue<GameTaskInterface> getGameTaskQueue() {
        return m_gameTaskQueue;
    }
}
