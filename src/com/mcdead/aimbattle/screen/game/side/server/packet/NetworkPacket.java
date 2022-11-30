package com.mcdead.aimbattle.screen.game.side.server.packet;

import java.nio.ByteBuffer;

public class NetworkPacket {
    private NetworkPacketHeader m_header;
    private byte[] m_rawPacketBytes;

    public NetworkPacket(final GameNetworkPacket packet) {
        m_rawPacketBytes = packet.toBytes();
        m_header = new NetworkPacketHeader(m_rawPacketBytes.length);
    }

    public byte[] toBytes() {
        return ByteBuffer.allocate(NetworkPacketHeader.C_BYTES_COUNT + m_rawPacketBytes.length)
                .put(m_header.toBytes()).put(m_rawPacketBytes).array();
    }

    public static class NetworkPacketHeader {
        public static int C_BYTES_COUNT = Integer.BYTES;

        private int m_length;

        public NetworkPacketHeader() {
            m_length = 0;
        }

        public NetworkPacketHeader(final int length) {
            m_length = length;
        }

        public int getLength() {
            return m_length;
        }

        public byte[] toBytes() {
            return ByteBuffer.allocate(C_BYTES_COUNT)
                    .putInt(m_length).array();
        }

        public boolean fromBytes(final byte[] bytes) {
            if (bytes.length < C_BYTES_COUNT) return false;

            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            m_length = buffer.getInt();

            return true;
        }
    }
}
