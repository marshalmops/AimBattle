package com.mcdead.aimbattle.event.game;

public class GameEventCreate extends GameEvent {
    public GameEventCreate() {

    }

    @Override
    public GameEventType getType() {
        return GameEventType.CREATE;
    }
}
