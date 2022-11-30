package com.mcdead.aimbattle.event.error;

public class GameErrorEventIncorrectPacketType extends GameErrorEvent {
    public GameErrorEventIncorrectPacketType() {
        super("Incorrect packet type has been occurred!", true);
    }

    @Override
    public ErrorEventType getType() {
        return GameErrorEventType.INCORRECT_PACKET_TYPE;
    }
}
