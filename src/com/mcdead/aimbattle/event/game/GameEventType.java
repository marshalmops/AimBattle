package com.mcdead.aimbattle.event.game;

import com.mcdead.aimbattle.event.Event;

public enum GameEventType implements Event {
    CREATE(), CONNECT(), RESUME(), RESTART(), STOP();

    private GameEventType() {

    }
}
