package com.mcdead.aimbattle.screen.game.side.server.socket;

import java.io.IOException;

public abstract class Socket {
    public abstract boolean init(final String hostname, final int port);
    public abstract boolean close();
}
