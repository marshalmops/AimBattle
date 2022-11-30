package com.mcdead.aimbattle.event.error;

public class GameErrorEventExitTimeout extends GameErrorEvent {
    public GameErrorEventExitTimeout() {
        super("Game exit timeout!", true);
    }

    @Override
    public GameErrorEventType getType() {
        return GameErrorEventType.EXIT_TIMEOUT;
    }
}
