package com.mcdead.aimbattle.event.game;

public class GameEventRestart extends GameEvent {
    public GameEventRestart() {

    }

    @Override
    public GameEventType getType() {
        return GameEventType.RESTART;
    }
}
