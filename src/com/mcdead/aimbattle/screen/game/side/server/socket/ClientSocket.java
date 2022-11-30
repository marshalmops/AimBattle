package com.mcdead.aimbattle.screen.game.side.server.socket;

import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacket;
import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacketFromBytesFactory;
import com.mcdead.aimbattle.screen.game.side.server.packet.NetworkPacket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientSocket extends Socket {
    private static int C_CONNECT_TIMEOUT_MSEC = 1500;
    private static int C_RECEIVE_TIMEOUT_MSEC = 1000;

    private java.net.Socket m_socket;
    private boolean m_isCriticalErrorOccurred;

    public ClientSocket() {
        m_socket = new java.net.Socket();
        m_isCriticalErrorOccurred = false;
    }
    public ClientSocket(final java.net.Socket socket) {
        m_socket = socket;
        m_isCriticalErrorOccurred = false;
    }

    public boolean isCriticalErrorOccurred() {
        return m_isCriticalErrorOccurred;
    }

//    public boolean isClosed() {
//        if (m_socket.isClosed()) return false;
//
//
//        return ;
//    }

    public boolean sendPacket(final GameNetworkPacket packet) {
        if (m_isCriticalErrorOccurred) return false;
        if (packet == null) return false;

        try {
            OutputStream outputStream = m_socket.getOutputStream();

            outputStream.write(new NetworkPacket(packet).toBytes());
            outputStream.flush();

        } catch (IOException e) {
            m_isCriticalErrorOccurred = true;

            e.printStackTrace();

            return false;
        }

        return true;
    }

    public GameNetworkPacket tryReceivePacket()  {
        if (m_isCriticalErrorOccurred) return null;

        try {
            InputStream inputStream = m_socket.getInputStream();

            if (inputStream.available() < NetworkPacket.NetworkPacketHeader.C_BYTES_COUNT)
                return null;

            byte[] rawHeaderBytes = inputStream.readNBytes(NetworkPacket.NetworkPacketHeader.C_BYTES_COUNT);

            NetworkPacket.NetworkPacketHeader header = new NetworkPacket.NetworkPacketHeader();

            if (!header.fromBytes(rawHeaderBytes))
                throw new IOException();

            long startTime = System.currentTimeMillis();

            while (true) {
                if (System.currentTimeMillis() - startTime >= C_RECEIVE_TIMEOUT_MSEC)
                    throw new IOException();

                if (inputStream.available() < header.getLength())
                    continue;

                byte[] rawPacketBytes = inputStream.readNBytes(header.getLength());

                return GameNetworkPacketFromBytesFactory.producePacketWithBytes(rawPacketBytes);
            }
        } catch (Throwable e) {
            try {
                m_socket.close();
            } catch (Throwable socketCloseException) {}

            m_isCriticalErrorOccurred = true;

            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean init(String hostname, int port) {
        try {
            m_socket.connect(new InetSocketAddress(hostname, port), C_CONNECT_TIMEOUT_MSEC);
        } catch (Throwable e) {
            if (e instanceof SocketTimeoutException)
                return false;

            m_isCriticalErrorOccurred = true;

            e.printStackTrace();

            return false;
        }

        return true;
    }

    @Override
    public boolean close() {
        try {
            m_socket.close();
        } catch (IOException e) {
            m_isCriticalErrorOccurred = true;

            e.printStackTrace();

            return false;
        }

        return true;
    }
}
