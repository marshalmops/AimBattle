package com.mcdead.aimbattle.screen.game.side.server.packet;

import com.mcdead.aimbattle.screen.game.input.GameInput;
import com.mcdead.aimbattle.screen.game.input.GameInputFromBytesFactory;

import java.nio.ByteBuffer;
import java.util.UUID;

public class GameNetworkPacketPlayerInput extends GameNetworkPacket {
    public static int C_BYTES_COUNT = 0;

    private GameInput m_gameInput;

    public GameNetworkPacketPlayerInput()
    {
        super(GameNetworkPacketType.PLAYER_INPUT);

        m_gameInput = null;
    }

    public GameNetworkPacketPlayerInput(final GameInput gameInput)
    {
        super(GameNetworkPacketType.PLAYER_INPUT);

        m_gameInput = gameInput;
    }

    public GameInput getGameInput() {
        return m_gameInput;
    }

    @Override
    public byte[] toBytes() {
        byte[] superRawBytes = super.toBytes();

        byte[] inputRawBytes = m_gameInput.toBytes();

        byte[] rawBytes = new byte[superRawBytes.length + inputRawBytes.length];

        System.arraycopy(superRawBytes, 0, rawBytes, 0, superRawBytes.length);
        System.arraycopy(inputRawBytes, 0, rawBytes, superRawBytes.length, inputRawBytes.length);

        return rawBytes;
    }

    @Override
    public boolean fromBytes(ByteBuffer byteBuffer) {
        if (!super.fromBytes(byteBuffer)) return false;

        m_gameInput = GameInputFromBytesFactory.produceGameInputWithBytes(byteBuffer);

        return (m_gameInput != null);
    }
}
