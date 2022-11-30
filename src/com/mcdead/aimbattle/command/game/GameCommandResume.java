package com.mcdead.aimbattle.command.game;

public class GameCommandResume extends GameCommand {
    public GameCommandResume(Origin origin) {
        super(origin);
    }

    @Override
    public GameCommandType getType() {
        return GameCommandType.RESUME;
    }
}
