package com.mcdead.aimbattle.event.game;

import com.mcdead.aimbattle.event.Event;

public abstract class GameEvent implements Event {
    public GameEvent() {

    }

    public abstract GameEventType getType();
}
