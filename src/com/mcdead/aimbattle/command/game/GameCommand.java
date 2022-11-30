package com.mcdead.aimbattle.command.game;

import com.mcdead.aimbattle.command.Command;

public abstract class GameCommand extends Command {
    private Origin m_origin;

    public enum Origin {
        REMOTE(), LOCAL();
    }

    public GameCommand(final Origin origin) {
        m_origin = origin;
    }

    public abstract GameCommandType getType();

    public Origin getOrigin() {
        return m_origin;
    }
}
