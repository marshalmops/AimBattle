package com.mcdead.aimbattle.command.game;

public class GameCommandPause extends GameCommand {
    public GameCommandPause(final Origin origin) {
        super(origin);

    }

    @Override
    public GameCommandType getType() {
        return GameCommandType.PAUSE;
    }
}
