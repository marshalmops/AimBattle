package com.mcdead.aimbattle.event.error;

public class NetworkErrorEventConnectionFailed extends NetworkErrorEvent {
    public NetworkErrorEventConnectionFailed() {
        super("Connection failed!", false);
    }

    @Override
    public NetworkErrorEventType getType() {
        return NetworkErrorEventType.CONNECTION_FAILED;
    }
}
