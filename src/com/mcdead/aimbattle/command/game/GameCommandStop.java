package com.mcdead.aimbattle.command.game;

public class GameCommandStop extends GameCommand {
    public GameCommandStop(Origin origin) {
        super(origin);
    }

    @Override
    public GameCommandType getType() {
        return GameCommandType.STOP;
    }
}
