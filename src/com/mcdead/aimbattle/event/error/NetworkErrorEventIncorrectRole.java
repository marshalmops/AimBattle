package com.mcdead.aimbattle.event.error;

import static com.mcdead.aimbattle.event.error.NetworkErrorEventType.INCORRECT_ROLE;

public class NetworkErrorEventIncorrectRole extends NetworkErrorEvent {
    public NetworkErrorEventIncorrectRole() {
        super("Incorrect role in network!", true);
    }

    @Override
    public NetworkErrorEventType getType() {
        return INCORRECT_ROLE;
    }
}
