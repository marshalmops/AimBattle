package com.mcdead.aimbattle.screen.menu;

import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.event.MenuEvent;

import java.util.concurrent.BlockingQueue;

public class MenuPanelSettings extends MenuPanelBase implements SubmenuInterface {
    public MenuPanelSettings(BlockingQueue<Event> appEventsQueueRef) {
        super(appEventsQueueRef);

        // layout creation...


    }

    @Override
    public void moveBack() throws InterruptedException {
        m_appEventsQueueRef.put(MenuEvent.BACK);
    }
}
