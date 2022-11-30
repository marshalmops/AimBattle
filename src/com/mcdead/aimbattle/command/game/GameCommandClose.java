package com.mcdead.aimbattle.command.game;

public class GameCommandClose extends GameCommand {
    public GameCommandClose(Origin origin) {
        super(origin);
    }

    @Override
    public GameCommandType getType() {
        return GameCommandType.CLOSE;
    }
}
