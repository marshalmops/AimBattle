package com.mcdead.aimbattle.screen.menu;

import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.event.MenuEvent;
import com.mcdead.aimbattle.event.game.GameEventConnect;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

public class MenuPanelConnect extends MenuPanelBase {
    private JTextField m_hostTextField;

    public MenuPanelConnect(BlockingQueue<Event> appEventsQueueRef) {
        super(appEventsQueueRef);

        EventQueue.invokeLater(() -> {
            m_hostTextField = new JTextField();
            JButton connectGameButton = new JButton("Connect");
            JButton backButton = new JButton("Back");

            connectGameButton.addActionListener((e) -> connectToGame());
            backButton.addActionListener((e) -> goBack());

            JPanel controlsPanel = generatePanelWithComponents(m_hostTextField, connectGameButton, backButton);

            add(controlsPanel);
        });
    }

    public void connectToGame() {
        String hostString = m_hostTextField.getText();

        if (!checkHostValidity(hostString)) {
            JOptionPane.showMessageDialog(this,
                    "Provided host address is not valid!",
                    "Error!",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        m_appEventsQueueRef.offer((Event) new GameEventConnect(hostString));
    }

    public void goBack() {
        m_appEventsQueueRef.offer(MenuEvent.BACK);
    }

    private boolean checkHostValidity(final String hostString) {
        final String hostPattern =  "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        return hostString.matches(hostPattern);
    }
}
