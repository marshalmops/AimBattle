package com.mcdead.aimbattle.command.menu;

import com.mcdead.aimbattle.command.Command;

public abstract class MenuCommand extends Command {
    public MenuCommand() {

    }

    public abstract MenuCommandType getType();
}
