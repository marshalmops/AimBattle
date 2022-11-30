package com.mcdead.aimbattle.screen;

import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.command.Command;

import javax.swing.*;
import java.util.concurrent.BlockingQueue;

public abstract class ScreenBase extends JPanel {
    protected BlockingQueue<Event> m_appEventsQueueRef;

    public ScreenBase(BlockingQueue<Event> appEventsQueueRef) {
        m_appEventsQueueRef = appEventsQueueRef;
    }

    public abstract boolean processCommand(final Command command);
    public abstract void setFullEnabled(final boolean isEnabled);
    public abstract void prepareToShow();
}
