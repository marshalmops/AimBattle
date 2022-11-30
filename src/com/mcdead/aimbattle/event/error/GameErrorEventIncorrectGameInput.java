package com.mcdead.aimbattle.event.error;

public class GameErrorEventIncorrectGameInput extends GameErrorEvent {
    public GameErrorEventIncorrectGameInput() {
        super("Incorrect game input has been obtained!", true);
    }

    @Override
    public ErrorEventType getType() {
        return GameErrorEventType.INCORRECT_GAME_INPUT;
    }
}
