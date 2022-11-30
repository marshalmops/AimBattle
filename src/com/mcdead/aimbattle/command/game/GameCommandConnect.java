package com.mcdead.aimbattle.command.game;

public class GameCommandConnect extends GameCommand {
    private String m_host;

    public GameCommandConnect(final Origin origin, final String host) {
        super(origin);

        m_host = host;
    }

    public String getHost() {
        return m_host;
    }

    @Override
    public GameCommandType getType() {
        return GameCommandType.CONNECT;
    }
}
