package com.mcdead.aimbattle.event.game;

public class GameEventStop extends GameEvent {
    public GameEventStop() {

    }

    @Override
    public GameEventType getType() {
        return GameEventType.STOP;
    }
}
