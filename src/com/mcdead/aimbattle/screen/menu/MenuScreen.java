package com.mcdead.aimbattle.screen.menu;

import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.command.Command;
import com.mcdead.aimbattle.command.menu.MenuCommand;
import com.mcdead.aimbattle.screen.ScreenBase;
import com.mcdead.aimbattle.screen.menu.MenuPanelBase;
import com.mcdead.aimbattle.screen.menu.MenuPanelMain;
import com.mcdead.aimbattle.screen.menu.MenuPanelPause;

import java.awt.*;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;

public class MenuScreen extends ScreenBase {
    public static int C_COUNT_OF_PANELS_TO_BACK = 2;

    private Stack<MenuPanelBase> m_menuPanelStack;

    public MenuScreen(BlockingQueue<Event> appEventsQueueRef) {
        super(appEventsQueueRef);

        m_menuPanelStack = new Stack<>();

        EventQueue.invokeLater(() -> {
            setFlexibleGridLayout();
        });
    }

    @Override
    public boolean processCommand(Command command) {
        if (!(command instanceof MenuCommand))
            return false;

        MenuCommand menuCommand = (MenuCommand) command;

        switch (menuCommand.getType()) {
            case SHOW_MAIN     -> {showPanel(new MenuPanelMain(m_appEventsQueueRef));}
            case SHOW_PAUSE    -> {showPanel(new MenuPanelPause(m_appEventsQueueRef));}
            case SHOW_SETTINGS -> {}
            case SHOW_CONNECTION -> {showPanel(new MenuPanelConnect(m_appEventsQueueRef));}
            case BACK -> {goBack();}
        }

        return false;
    }

    private void goBack() {
        if (m_menuPanelStack.size() < C_COUNT_OF_PANELS_TO_BACK) return;

        m_menuPanelStack.pop();

        showPanel(m_menuPanelStack.pop());
    }

    private void showPanel(MenuPanelBase panel) {
        m_menuPanelStack.push(panel);

        EventQueue.invokeLater(() -> {
            removeAll();

            GridBagConstraints centralContaints = new GridBagConstraints(
                    1,1,1,1,
                    0, 0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0,0,0,0),0,0);

            add(panel, centralContaints);

            repaint();
            revalidate();
        });
    }

    /*
     * ATTENTION: call this function from EVENT HANDLING THREAD!
     */
    private void setFlexibleGridLayout() {
        GridBagLayout mainLayout = new GridBagLayout();

        setLayout(mainLayout);

        GridBagConstraints spacerContaints = new GridBagConstraints(
                0,0,1,1,
                100, 100,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0,0,0,0),0,0);

        for (int i = 0; i < 3; ++i) {
            spacerContaints.gridx = i;

            for (int j = 0; j < 3; ++j) {
                if (i == 1 && j == 1) continue;

                spacerContaints.gridy = j;

                add(new Container(), spacerContaints);
            }
        }
    }

    @Override
    public void setFullEnabled(boolean isEnabled) {

    }

    @Override
    public void prepareToShow() {
        m_menuPanelStack.clear();
    }
}
