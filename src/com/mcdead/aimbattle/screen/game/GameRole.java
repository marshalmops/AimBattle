package com.mcdead.aimbattle.screen.game;

import com.mcdead.aimbattle.utils.Copyable;

public enum GameRole implements Copyable {
    INCORRECT(), SLAVE(), MASTER();

    @Override
    public Copyable copy() {
        return valueOf(name());
    }
}
