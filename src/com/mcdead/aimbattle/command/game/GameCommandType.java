package com.mcdead.aimbattle.command.game;

public enum GameCommandType {
    CREATE(), CONNECT(), START(), STOP(),
    RESTART(), PAUSE(), RESUME(), CLOSE(),
    EXIT();

    private GameCommandType() {

    }
}
