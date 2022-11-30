package com.mcdead.aimbattle.event.game;

public class GameEventConnect extends GameEvent {
    private String m_host;

    public GameEventConnect(final String host) {
        m_host = host;
    }

    public String getHost() {
        return m_host;
    }

    @Override
    public GameEventType getType() {
        return GameEventType.CONNECT;
    }
}
