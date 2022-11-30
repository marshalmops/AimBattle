package com.mcdead.aimbattle.event.error;

public class GameErrorEventIncorrectCommandOrigin extends GameErrorEvent {
    public GameErrorEventIncorrectCommandOrigin() {
        super("Command origin is incorrect!", true);
    }

    @Override
    public ErrorEventType getType() {
        return GameErrorEventType.INCORRECT_COMMAND_ORIGIN;
    }
}
