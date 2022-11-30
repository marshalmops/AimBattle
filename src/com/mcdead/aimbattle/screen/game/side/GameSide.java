package com.mcdead.aimbattle.screen.game.side;

import com.mcdead.aimbattle.command.game.GameCommand;
import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.screen.game.GameMode;
import com.mcdead.aimbattle.screen.game.GameRole;
import com.mcdead.aimbattle.screen.game.objects.GameState;
import com.mcdead.aimbattle.screen.game.processor.GameProcessorCallbackStore;
import com.mcdead.aimbattle.utils.SharedObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class GameSide {
    protected GameProcessorCallbackStore m_callbackStore;
    protected GameState m_gameStateRef;
    protected BlockingQueue<Event> m_appEventQueueRef; // for error handling!
    protected SharedObject<GameMode> m_gameModeRef;
    protected SharedObject<GameRole> m_gameRoleRef;
    protected AtomicBoolean m_isSideCycleAlive;
    protected AtomicBoolean m_isSideCyclePaused;

    public GameSide(BlockingQueue<Event> appEventQueueRef,
                    GameProcessorCallbackStore callbackStore,
                    GameState gameStateRef,
                    SharedObject<GameMode> gameModeRef,
                    SharedObject<GameRole> gameRoleRef)
    {
        m_appEventQueueRef = appEventQueueRef;
        m_gameStateRef = gameStateRef;
        m_callbackStore = callbackStore;
        m_gameModeRef = gameModeRef;
        m_gameRoleRef = gameRoleRef;
        m_isSideCycleAlive = new AtomicBoolean(false);
        m_isSideCyclePaused = new AtomicBoolean(false);
    }

    public boolean isAlive() {
        return m_isSideCycleAlive.get();
    }

    public abstract boolean processCommand(final GameCommand command);

    protected abstract void processGameEnd();
}
