package com.mcdead.aimbattle.event.error;

public class NetworkErrorEventSocketError extends NetworkErrorEvent {
    public NetworkErrorEventSocketError() {
        super("Socket error has been occurred!", true);
    }

    @Override
    public NetworkErrorEventType getType() {
        return NetworkErrorEventType.SOCKET_ERROR;
    }
}
