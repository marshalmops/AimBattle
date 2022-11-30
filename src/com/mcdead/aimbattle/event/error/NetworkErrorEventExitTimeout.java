package com.mcdead.aimbattle.event.error;

public class NetworkErrorEventExitTimeout extends NetworkErrorEvent {
    public NetworkErrorEventExitTimeout() {
        super("Network exit timeout!", true);
    }

    @Override
    public NetworkErrorEventType getType() {
        return NetworkErrorEventType.EXIT_TIMEOUT;
    }
}
