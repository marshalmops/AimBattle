package com.mcdead.aimbattle.event.error;

public abstract class AppErrorEvent extends ErrorEvent {
    public AppErrorEvent(String cause, boolean isCritical) {
        super(cause, isCritical);
    }
}
