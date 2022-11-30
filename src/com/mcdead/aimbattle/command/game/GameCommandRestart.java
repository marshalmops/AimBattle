package com.mcdead.aimbattle.command.game;

public class GameCommandRestart extends GameCommand {
    public GameCommandRestart(Origin origin) {
        super(origin);
    }

    @Override
    public GameCommandType getType() {
        return GameCommandType.RESTART;
    }
}
