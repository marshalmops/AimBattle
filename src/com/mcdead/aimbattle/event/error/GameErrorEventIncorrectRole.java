package com.mcdead.aimbattle.event.error;

public class GameErrorEventIncorrectRole extends GameErrorEvent {
    public GameErrorEventIncorrectRole() {
        super("Incorrect role has been occurred!", true);
    }

    @Override
    public GameErrorEventType getType() {
        return GameErrorEventType.INCORRECT_ROLE;
    }
}
