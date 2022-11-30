package com.mcdead.aimbattle.event.game;

public class GameEventResume extends GameEvent {
    public GameEventResume() {

    }

    @Override
    public GameEventType getType() {
        return GameEventType.RESUME;
    }
}
