package com.mcdead.aimbattle.event.error;

public abstract class GameErrorEvent extends ErrorEvent {
    public GameErrorEvent(String cause, boolean isCritical) {
        super(cause, isCritical);
    }
}
