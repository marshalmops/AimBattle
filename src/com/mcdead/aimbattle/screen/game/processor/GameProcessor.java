package com.mcdead.aimbattle.screen.game.processor;

import com.mcdead.aimbattle.GameContext;
import com.mcdead.aimbattle.command.game.*;
import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.event.error.GameErrorEventExitTimeout;
import com.mcdead.aimbattle.event.game.GameEventResume;
import com.mcdead.aimbattle.event.MenuEvent;
import com.mcdead.aimbattle.screen.game.GameMode;
import com.mcdead.aimbattle.screen.game.GameRole;
import com.mcdead.aimbattle.screen.game.input.GameInputMouseClick;
import com.mcdead.aimbattle.screen.game.objects.GameState;
import com.mcdead.aimbattle.screen.game.objects.Player;
import com.mcdead.aimbattle.screen.game.side.client.GameClient;
import com.mcdead.aimbattle.screen.game.side.server.GameServer;
import com.mcdead.aimbattle.utils.SharedObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class GameProcessor {
    private BlockingQueue<Event> m_appEventQueueRef;
    private JComponent m_screen;
    private GameState m_gameState;

    private SharedObject<GameMode> m_gameMode;
    private SharedObject<GameRole> m_gameRole;

    private GameClient m_gameClient;
    private GameServer m_gameServer;

    public GameProcessor(JComponent screen,
                         BlockingQueue<Event> appEventQueueRef)
    {
        m_appEventQueueRef = appEventQueueRef;
        m_screen = screen;
        m_gameState = new GameState();

        m_gameMode = new SharedObject<>(GameMode.INIT);
        m_gameRole = new SharedObject<>(GameRole.INCORRECT);

        m_gameClient = new GameClient(m_appEventQueueRef,
                new GameProcessorCallbackStoreClient(this),
                m_gameState,
                m_gameMode, m_gameRole,
                screen);
        m_gameServer = new GameServer(m_appEventQueueRef,
                new GameProcessorCallbackStoreServer(this),
                m_gameState,
                m_gameMode, m_gameRole);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event -> {
            if (event.getKeyCode() != KeyEvent.VK_ESCAPE) return false;
            if (event.getID() != KeyEvent.KEY_RELEASED) return false;

            switch (m_gameMode.getValue()) {
                case PAUSED -> m_appEventQueueRef.offer(new GameEventResume());
                case EXEC_GAME -> {
                    pauseGameCallback();
                }
                default -> {return false;}
            }

            return true;
        });

        m_screen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (m_gameMode.getValue() != GameMode.EXEC_GAME) return;

                Dimension mapSize = m_gameState.getMap().getSize();

                float xCoef = (float)(m_screen.getWidth()) / mapSize.width;
                float yCoef = (float)(m_screen.getHeight() - GameClient.C_SCORE_BAR_HEIGHT) / mapSize.height;

                m_gameServer.addGameInputToProcess(
                    new GameInputMouseClick(
                        (int)(e.getX() / xCoef),
                        (int)((e.getY() - GameClient.C_SCORE_BAR_HEIGHT) / yCoef)));
            }
        });

    }

    public boolean createGame() {
        m_gameMode.setValue(GameMode.WAITING_PLAYERS);
        m_gameRole.setValue(GameRole.MASTER);

        GameCommandCreate createCommand = new GameCommandCreate(GameCommand.Origin.LOCAL);

        if (!m_gameServer.processCommand(createCommand))
            return false;

        m_gameClient.processCommand(createCommand);

        m_gameState.setLocalPlayer(new Player(UUID.randomUUID()));

        return true;
    }

    public boolean startGame() {
        m_gameState.getMap().clearObjects();
        m_gameState.clearPlayerScore();

        m_gameMode.setValue(GameMode.EXEC_GAME);

        GameCommandStart startCommand = new GameCommandStart(GameCommand.Origin.LOCAL);

        m_gameClient.processCommand(startCommand);
        m_gameServer.processCommand(startCommand);

        return true;
    }

    public boolean connectToGame(final String host) {
        m_gameMode.setValue(GameMode.WAITING_PLAYERS);
        m_gameRole.setValue(GameRole.SLAVE);

        GameCommandConnect connectCommand = new GameCommandConnect(GameCommand.Origin.LOCAL, host);

        if (!m_gameServer.processCommand(connectCommand))
            return false;

        m_gameClient.processCommand(connectCommand);

        return true;
    }

    public boolean stopGame() {
        m_gameMode.setValue(GameMode.STOP);

        GameCommandStop stopCommand = new GameCommandStop(GameCommand.Origin.LOCAL);

        m_gameClient.processCommand(stopCommand);
        m_gameServer.processCommand(stopCommand);

        return true;
    }

    public boolean pauseGame() {
        m_gameMode.setValue(GameMode.PAUSED);

        GameCommandPause pauseCommand = new GameCommandPause(GameCommand.Origin.LOCAL);

        m_gameClient.processCommand(pauseCommand);
        m_gameServer.processCommand(pauseCommand);

        return true;
    }

    public boolean restartGame() {
        m_gameMode.setValue(GameMode.WAITING_PLAYERS);

        GameCommandRestart restartCommand = new GameCommandRestart(GameCommand.Origin.LOCAL);

        m_gameClient.processCommand(restartCommand);
        m_gameServer.processCommand(restartCommand);

        return true;
    }

    public boolean resumeGame() {
//        if (m_gameRole.getValue() != GameRole.MASTER)
//            return false;

        m_gameMode.setValue(GameMode.EXEC_GAME);

        GameCommandResume resumeCommand = new GameCommandResume(GameCommand.Origin.LOCAL);

        m_gameClient.processCommand(resumeCommand);
        m_gameServer.processCommand(resumeCommand);

//        if (m_gameRole.getValue() == GameRole.MASTER)
//            m_appEventQueueRef.offer(new GameEventResume());

        return true;
    }

    public boolean closeGame(final GameCommand.Origin initiatorOrigin) {
        if (m_gameMode.getValue() == GameMode.INIT) return true;

        Logger.getGlobal().info("CLOSE with " + initiatorOrigin.name());

        m_gameMode.setValue(GameMode.INIT);

        GameCommandClose closeCommand = new GameCommandClose(initiatorOrigin);

        m_gameClient.processCommand(closeCommand);
        m_gameServer.processCommand(closeCommand);

        m_gameState.clearMap();
        m_gameState.clearPlayerList();

        return true;
    }

    public void exitGame() {
        GameCommandExit exitCommand = new GameCommandExit();

        m_gameServer.processCommand(exitCommand);
        m_gameClient.processCommand(exitCommand);

        final long startTimePoint = System.currentTimeMillis();

        while (m_gameServer.isAlive() || m_gameClient.isAlive()) {
            if (startTimePoint + GameContext.C_THREAD_EXIT_TIMEOUT >= System.currentTimeMillis()) {
                m_appEventQueueRef.offer(new GameErrorEventExitTimeout());

                break;
            }
        }
    }

    protected boolean createGameCallback() {
        if (!createGame()) return false;

        return true;
    }

    protected boolean startGameCallback() {
        if (!startGame()) return false;

        return true;
    }

    protected boolean connectToGameCallback(final String host) {
        if (!connectToGame(host)) return false;

        return true;
    }

    protected boolean stopGameCallback() {
        if (!stopGame()) return false;

        return true;
    }

    protected boolean pauseGameCallback() {
        if (!pauseGame()) return false;

        if (m_gameRole.getValue() == GameRole.MASTER)
            m_appEventQueueRef.offer(MenuEvent.OPEN_PAUSE);

        return true;
    }

    protected boolean restartGameCallback() {
        if (!restartGame()) return false;

        return true;
    }

    protected boolean resumeGameCallback() {
        if (!resumeGame()) return false;

        return true;
    }

    protected boolean closeGameCallback(final GameCommand.Origin origin) {
        if (!closeGame(origin)) return false;

        m_appEventQueueRef.offer(MenuEvent.OPEN);

        return true;
    }

    protected void exitGameCallback() {
        exitGame();
    }


//    public boolean processGameInput(final GameInput gameInput) {
//        if (gameInput == null) return false;
//
//        m_gameServer.addGameInputToProcess(gameInput);
//
//        return true;
//    }
}
