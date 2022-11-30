package com.mcdead.aimbattle.event.error;

public class GameErrorEventIllegalState extends GameErrorEvent {
    public GameErrorEventIllegalState() {
        super("Illegal game state has been occurred!", true);
    }

    @Override
    public GameErrorEventType getType() {
        return GameErrorEventType.ILLEGAL_STATE;
    }
}
