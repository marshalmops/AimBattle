package com.mcdead.aimbattle;

import com.mcdead.aimbattle.command.game.*;
import com.mcdead.aimbattle.command.menu.MenuCommandBack;
import com.mcdead.aimbattle.command.menu.MenuCommandShowConnection;
import com.mcdead.aimbattle.command.menu.MenuCommandShowMain;
import com.mcdead.aimbattle.command.menu.MenuCommandShowPause;
import com.mcdead.aimbattle.event.AppEvent;
import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.event.error.ErrorEvent;
import com.mcdead.aimbattle.event.game.GameEvent;
import com.mcdead.aimbattle.event.game.GameEventConnect;
import com.mcdead.aimbattle.event.MenuEvent;
import com.mcdead.aimbattle.screen.game.GameScreen;
import com.mcdead.aimbattle.screen.game.side.client.GameClient;
import com.mcdead.aimbattle.screen.menu.MenuScreen;
import com.mcdead.aimbattle.screen.ScreenBase;
import com.mcdead.aimbattle.settings.GameSettings;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MainFrame extends JFrame {
    public static String C_FRAME_TITLE = "AimBattle";
    public static Dimension C_DEFAULT_FRAME_SIZE = new Dimension(640, 480 + GameClient.C_SCORE_BAR_HEIGHT);

    GameScreen m_gameScreen;
    MenuScreen m_menuScreen;
    BlockingQueue<Event> m_appEventQueue;

    public static void main(String[] argv) {
        MainFrame mainFrame = new MainFrame();

        if (!GameSettings.createInstance()) {
            JOptionPane.showMessageDialog(null, "GameSettings instance creation error!");

            System.exit(-1);
        }

        try {
            mainFrame.startEventHandling();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public MainFrame() {
        m_appEventQueue = new LinkedBlockingQueue<>();

        m_gameScreen = new GameScreen(m_appEventQueue);
        m_menuScreen = new MenuScreen(m_appEventQueue);

        EventQueue.invokeLater(() -> {
            setTitle(C_FRAME_TITLE);
            setSize(C_DEFAULT_FRAME_SIZE);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
        });

        showScreen(m_menuScreen);

        m_menuScreen.processCommand(new MenuCommandShowMain());
    }

    public void startEventHandling() throws InterruptedException, ExceptionEventHandling {
        while (true) {
            com.mcdead.aimbattle.event.Event curEvent;

            if ((curEvent = m_appEventQueue.take()) == null) continue;

            if (!handleEvent(curEvent)) throw new ExceptionEventHandling();
        }
    }

    public boolean handleEvent(final Event event) {
        if (event instanceof MenuEvent)  return handleMenuEvent((MenuEvent) event);
        if (event instanceof GameEvent)  return handleGameEvent((GameEvent) event);
        if (event instanceof AppEvent)   return handleAppEvent((AppEvent) event);
        if (event instanceof ErrorEvent) return handleErrorEvent((ErrorEvent) event);

        return false;
    }

    private boolean handleErrorEvent(final ErrorEvent errorEvent) {
        JOptionPane.showMessageDialog(this, errorEvent.getCause(), "Error", JOptionPane.ERROR_MESSAGE);

        if (errorEvent.isCritical()) {
            System.exit(-1);
        }

        return true;
    }

    private boolean handleMenuEvent(final MenuEvent menuEvent) {
        switch (menuEvent) {
            case OPEN ->         {
                showScreen(m_menuScreen);

                m_menuScreen.processCommand(new MenuCommandShowMain());
                m_gameScreen.processCommand(new GameCommandClose(GameCommand.Origin.LOCAL));
            }
            case BACK ->         {m_menuScreen.processCommand(new MenuCommandBack());}
            case OPEN_PAUSE ->   {showScreen(m_menuScreen); m_menuScreen.processCommand(new MenuCommandShowPause());}
            case OPEN_CONNECT -> {m_menuScreen.processCommand(new MenuCommandShowConnection());}
            default -> {return false;}
        }

        return true;
    }

    private boolean handleGameEvent(final GameEvent gameEvent) {
        switch (gameEvent.getType()) {
            case CREATE ->  {
                if (!m_gameScreen.processCommand(new GameCommandCreate(GameCommand.Origin.LOCAL)))
                    return true;

                showScreen(m_gameScreen);
            }
//            case STOP ->    {m_gameScreen.processCommand(new GameCommandStop(GameCommand.Origin.LOCAL));}
            case RESUME ->  {showScreen(m_gameScreen); m_gameScreen.processCommand(new GameCommandResume(GameCommand.Origin.LOCAL));}
            case CONNECT -> {
                if (!m_gameScreen.processCommand(new GameCommandConnect(GameCommand.Origin.LOCAL, ((GameEventConnect)gameEvent).getHost())))
                    return true;

                showScreen(m_gameScreen);
            }
            case RESTART -> {showScreen(m_gameScreen); m_gameScreen.processCommand(new GameCommandRestart(GameCommand.Origin.LOCAL));}
            default ->      {return false;}
        }

        return true;
    }

    private boolean handleAppEvent(final AppEvent appEvent) {
        switch (appEvent) {
            case EXIT -> System.exit(0);
            default -> {return false;}
        }

        return true;
    }

    public void showScreen(ScreenBase screen) {
        screen.prepareToShow();

        EventQueue.invokeLater(() -> {
            getContentPane().removeAll();

            add(screen, BorderLayout.CENTER);

            repaint();
            revalidate();
        });
    }

}
