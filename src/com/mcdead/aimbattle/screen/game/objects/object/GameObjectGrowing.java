package com.mcdead.aimbattle.screen.game.objects.object;

import com.mcdead.aimbattle.GameContext;
import com.mcdead.aimbattle.screen.game.objects.shape.GameShape;
import com.mcdead.aimbattle.screen.game.objects.shape.GameShapeFromBytesFactory;
import com.mcdead.aimbattle.screen.game.objects.shape.GameShapeType;
import com.mcdead.aimbattle.utils.ColorUtils;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Random;

public abstract class GameObjectGrowing extends GameObject {
    public static int C_BYTES_COUNT = Integer.BYTES * 2;

    private int m_minGrowSpeed;
    private int m_maxGrowSpeed;

    public GameObjectGrowing(final GameObjectType type) {
        super(type);

        m_minGrowSpeed = 0;
        m_maxGrowSpeed = 0;
    }

    public GameObjectGrowing(final GameObjectType type, final Color color, final GameShape gameShape,
                             final int minGrowSpeed, final int maxGrowSpeed, final int lifespan)
    {
        super(type, color, gameShape, lifespan);

        m_minGrowSpeed = minGrowSpeed;
        m_maxGrowSpeed = maxGrowSpeed;
    }

    public GameObjectGrowing(final GameObjectGrowing other)
    {
        super(other);

        m_minGrowSpeed = other.m_minGrowSpeed;
        m_maxGrowSpeed = other.m_maxGrowSpeed;
    }

    public int getRandGrowSpeed() {
        return new Random(System.currentTimeMillis()).nextInt(m_minGrowSpeed, m_maxGrowSpeed);
    }

    public byte[] toBytes() {
        byte[] rawSuperBytes = super.toBytes();

        byte[] rawFieldBytes = ByteBuffer.allocate(C_BYTES_COUNT)
                .putInt(m_minGrowSpeed).putInt(m_maxGrowSpeed).array();

        byte[] rawBytes = new byte[rawSuperBytes.length + rawFieldBytes.length];

        System.arraycopy(rawSuperBytes, 0, rawBytes, 0, rawSuperBytes.length);
        System.arraycopy(rawFieldBytes, 0, rawBytes, rawSuperBytes.length, rawFieldBytes.length);

        return rawBytes;
    }

    public boolean fromBytes(final ByteBuffer byteBuffer) {
        if (!super.fromBytes(byteBuffer)) return false;
        if (byteBuffer.remaining() < C_BYTES_COUNT) return false;

        m_minGrowSpeed = byteBuffer.getInt();
        m_maxGrowSpeed = byteBuffer.getInt();

        return true;
    }
}
