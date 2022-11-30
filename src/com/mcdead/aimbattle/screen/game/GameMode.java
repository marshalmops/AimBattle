package com.mcdead.aimbattle.screen.game;

import com.mcdead.aimbattle.utils.Copyable;

public enum GameMode implements ThreadMode, Copyable {
    INIT(), WAITING_PLAYERS(), PAUSED(), EXEC_GAME(), STOP();

    @Override
    public Copyable copy() {
        return valueOf(name());
    }
}
