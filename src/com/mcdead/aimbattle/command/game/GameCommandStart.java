package com.mcdead.aimbattle.command.game;

public class GameCommandStart extends GameCommand {
    public GameCommandStart(Origin origin) {
        super(origin);
    }

    @Override
    public GameCommandType getType() {
        return GameCommandType.START;
    }
}
