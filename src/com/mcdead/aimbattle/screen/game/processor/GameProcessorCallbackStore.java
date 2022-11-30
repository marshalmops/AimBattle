package com.mcdead.aimbattle.screen.game.processor;

import com.mcdead.aimbattle.command.game.GameCommand;

public abstract class GameProcessorCallbackStore {
    protected GameProcessor m_gameProcessorRef;

    public GameProcessorCallbackStore(final GameProcessor gameProcessorRef) {
        m_gameProcessorRef = gameProcessorRef;
    }

    public abstract boolean processCommand(final GameCommand command);
}
