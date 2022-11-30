package com.mcdead.aimbattle.screen.game.side.server.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

public class ServerSocket extends Socket {
    private static int C_ACCEPT_TIMEOUT = 100;

    private java.net.ServerSocket m_serverSocket;

    public ServerSocket() throws IOException {
        m_serverSocket = new java.net.ServerSocket();

        m_serverSocket.setSoTimeout(C_ACCEPT_TIMEOUT);
    }

    public java.net.Socket accept() {
        java.net.Socket acceptedSocket = null;

        try {
            acceptedSocket = m_serverSocket.accept();

        } catch (IOException e) {
            if (!(e instanceof SocketTimeoutException))
                e.printStackTrace();

            return null;
        }

        return acceptedSocket;
    }

    @Override
    public boolean init(String hostname, int port) {
        try {
            m_serverSocket.setReuseAddress(true);
            m_serverSocket.bind(new InetSocketAddress(hostname, port));

        } catch (Throwable e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    @Override
    public boolean close() {
        try {
            m_serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }
}
