package com.mcdead.aimbattle.event.error;

public abstract class NetworkErrorEvent extends ErrorEvent {
    public NetworkErrorEvent(String cause, boolean isCritical) {
        super(cause, isCritical);
    }
}
