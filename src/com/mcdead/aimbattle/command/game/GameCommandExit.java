package com.mcdead.aimbattle.command.game;

public class GameCommandExit extends GameCommand {
    public GameCommandExit() {
        super(Origin.LOCAL);
    }

    @Override
    public GameCommandType getType() {
        return GameCommandType.EXIT;
    }
}
