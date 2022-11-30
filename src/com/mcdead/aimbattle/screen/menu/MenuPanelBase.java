package com.mcdead.aimbattle.screen.menu;

import com.mcdead.aimbattle.event.Event;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

public abstract class MenuPanelBase extends JPanel {
    protected BlockingQueue<Event> m_appEventsQueueRef;

    public MenuPanelBase(BlockingQueue<Event> appEventsQueueRef) {
        m_appEventsQueueRef = appEventsQueueRef;
    }

    /*
     * ATTENTION: call this function from EVENT HANDLING THREAD!
     */
    protected JPanel generatePanelWithComponents(JComponent... components) {
        JPanel componentsPanel = new JPanel();

        componentsPanel.setLayout(new GridLayout(components.length, 1, 0, 2));

        for (final JComponent component : components)
            componentsPanel.add(component);

        return componentsPanel;
    }
}
