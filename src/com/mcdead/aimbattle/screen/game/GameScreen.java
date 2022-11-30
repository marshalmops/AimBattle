package com.mcdead.aimbattle.screen.game;

import com.mcdead.aimbattle.command.game.GameCommandConnect;
import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.command.Command;
import com.mcdead.aimbattle.command.game.GameCommand;
import com.mcdead.aimbattle.screen.ScreenBase;
import com.mcdead.aimbattle.screen.game.processor.GameProcessor;

import java.util.concurrent.BlockingQueue;

public class GameScreen extends ScreenBase {
    private GameProcessor m_gameProcessor;

    public GameScreen(BlockingQueue<Event> appEventQueueRef) {
        super(appEventQueueRef);

        m_gameProcessor = new GameProcessor(this, m_appEventsQueueRef);
    }

    @Override
    public boolean processCommand(Command command) {
        if (!(command instanceof GameCommand))
            return false;

        GameCommand gameCommand = (GameCommand) command;

        boolean result = true;

        switch (gameCommand.getType()) {
            case START   -> {result = m_gameProcessor.startGame();}
            case STOP    -> {result = m_gameProcessor.stopGame();}
            case CONNECT -> {result = m_gameProcessor.connectToGame(((GameCommandConnect)gameCommand).getHost());}
            case CREATE  -> {result = m_gameProcessor.createGame();}
            case RESTART -> {result = m_gameProcessor.restartGame();}
            case RESUME  -> {result = m_gameProcessor.resumeGame();}
            case PAUSE   -> {result = m_gameProcessor.pauseGame();}
            case CLOSE   -> {result = m_gameProcessor.closeGame(GameCommand.Origin.LOCAL);} // ?
            case EXIT    -> {m_gameProcessor.exitGame();}
        }

        return result;
    }

    @Override
    public void setFullEnabled(boolean isEnabled) {

    }

    @Override
    public void prepareToShow() {

    }
}
