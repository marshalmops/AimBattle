package com.mcdead.aimbattle.command.game;

public class GameCommandCreate extends GameCommand {
    public GameCommandCreate(final Origin origin) {
        super(origin);
    }


    @Override
    public GameCommandType getType() {
        return GameCommandType.CREATE;
    }
}
