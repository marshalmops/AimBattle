package com.mcdead.aimbattle.screen.game.processor;

import com.mcdead.aimbattle.command.game.GameCommand;

public class GameProcessorCallbackStoreServer extends GameProcessorCallbackStore {
    public GameProcessorCallbackStoreServer(final GameProcessor gameProcessorRef) {
        super(gameProcessorRef);
    }

    @Override
    public boolean processCommand(final GameCommand command) {
        if (command == null) return false;

        switch (command.getType()) {
            case START -> {return m_gameProcessorRef.startGameCallback();}
            case STOP -> {return m_gameProcessorRef.stopGameCallback();}
            case PAUSE -> {return m_gameProcessorRef.pauseGameCallback();}
            case RESUME -> {return m_gameProcessorRef.resumeGameCallback();}
            case RESTART -> {return m_gameProcessorRef.restartGameCallback();}
            case CLOSE -> {return m_gameProcessorRef.closeGameCallback(command.getOrigin());}
        }

        return false;
    }
}
