package com.mcdead.aimbattle.screen.menu;

import com.mcdead.aimbattle.command.game.GameCommand;
import com.mcdead.aimbattle.event.AppEvent;
import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.event.MenuEvent;
import com.mcdead.aimbattle.event.game.GameEventCreate;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

public class MenuPanelMain extends MenuPanelBase {
    public static String C_MAIN_PANEL_LABEL = "AIM BATTLE.";

    public MenuPanelMain(BlockingQueue<Event> appEventsQueueRef) {
        super(appEventsQueueRef);

        EventQueue.invokeLater(() -> {
            JLabel mainLabel = new JLabel(C_MAIN_PANEL_LABEL, JLabel.CENTER);
            JButton createGameButton = new JButton("Create game");
            JButton connectGameButton = new JButton("Connect to game");
            JButton settingsButton = new JButton("Settings");
            JButton exitButton = new JButton("Exit");

            createGameButton.addActionListener((e) -> newGame());
            connectGameButton.addActionListener((e) -> connectToGame());
            settingsButton.addActionListener((e) -> openSettings());
            exitButton.addActionListener((e) -> processExit());

            settingsButton.setEnabled(false);

            JPanel controlsPanel = generatePanelWithComponents(mainLabel, createGameButton, connectGameButton, settingsButton, exitButton);

            add(controlsPanel);
            //setFlexibleGridLayoutForCentralElem(controlsPanel);
        });
    }

//    public static void main(String[] argv) {
//        JFrame testFrame = new JFrame();
//
//        EventQueue.invokeLater(() -> {
//            Container container = testFrame.getContentPane();
//
//            container.add(new MenuPanelMain(null), BorderLayout.CENTER);
//
//            testFrame.setTitle("Test");
//            testFrame.setSize(640, 480);
//            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            testFrame.setVisible(true);
//        });
//    }

    public void newGame() {
        m_appEventsQueueRef.offer((Event) new GameEventCreate());
    }

    public void connectToGame() {
        m_appEventsQueueRef.offer(MenuEvent.OPEN_CONNECT);
    }

    public void openSettings() {
        // not enabled for now.
    }

    public void processExit() {
        m_appEventsQueueRef.offer(AppEvent.EXIT);
    }
}
