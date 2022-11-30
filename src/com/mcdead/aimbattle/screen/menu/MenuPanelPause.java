package com.mcdead.aimbattle.screen.menu;

import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.event.MenuEvent;
import com.mcdead.aimbattle.event.game.GameEventRestart;
import com.mcdead.aimbattle.event.game.GameEventResume;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

public class MenuPanelPause extends MenuPanelBase {
    public MenuPanelPause(BlockingQueue<Event> appEventsQueueRef) {
        super(appEventsQueueRef);

        EventQueue.invokeLater(() -> {
            JButton resumeGameButton = new JButton("Resume");
            JButton restartGameButton = new JButton("Restart");
            JButton settingsButton = new JButton("Settings");
            JButton openMainMenuButton = new JButton("Go to Main Menu");

            resumeGameButton.addActionListener((e) -> resumeGame());
            restartGameButton.addActionListener((e) -> restartGame());
            settingsButton.addActionListener((e) -> openSettings());
            openMainMenuButton.addActionListener((e) -> openMainMenu());

            settingsButton.setEnabled(false);

            JPanel controlsPanel = generatePanelWithComponents(resumeGameButton, restartGameButton, settingsButton, openMainMenuButton);

            add(controlsPanel);
        });
    }

    public void resumeGame() {
        m_appEventsQueueRef.offer(new GameEventResume());
    }

    public void restartGame() {
        m_appEventsQueueRef.offer(new GameEventRestart());
    }

    public void openSettings() {
        // nothing to do...
    }

    public void openMainMenu() {
        m_appEventsQueueRef.offer(MenuEvent.OPEN);
    }
}
