package com.mcdead.aimbattle.screen.game.objects.object;

import com.mcdead.aimbattle.ByteConvertable;
import com.mcdead.aimbattle.GameContext;
import com.mcdead.aimbattle.screen.game.objects.shape.GameShape;
import com.mcdead.aimbattle.screen.game.objects.shape.GameShapeFromBytesFactory;
import com.mcdead.aimbattle.screen.game.objects.shape.GameShapeType;
import com.mcdead.aimbattle.utils.ColorUtils;
import com.mcdead.aimbattle.utils.Copyable;

import java.awt.*;
import java.nio.ByteBuffer;

public abstract class GameObject implements Copyable, ByteConvertable {
    public static int C_BYTES_COUNT = GameObjectType.C_BYTES_COUNT + Integer.BYTES + Integer.BYTES + Integer.BYTES + Long.BYTES;
    public static int C_DEFAULT_LIFESPAN_IN_TICKS = 100;

    private static int s_idCounter = 0;

    private GameObjectType m_type;
    private int m_id;
    private Color m_color;
    private long m_creationTime;
    private int m_lifespanInTicks;
    private GameShape m_gameShape;

    public GameObject(final GameObjectType type) {
        m_type = type;
        m_id = -1;
        m_color = null;
        m_lifespanInTicks = C_DEFAULT_LIFESPAN_IN_TICKS;
        m_creationTime = System.currentTimeMillis();
        m_gameShape = null;
    }

    public GameObject(final GameObjectType type, final Color color, final GameShape gameShape, final int lifespan) {
        m_type = type;
        m_id = s_idCounter;
        m_color = color;
        m_lifespanInTicks = lifespan;
        m_creationTime = System.currentTimeMillis();
        m_gameShape = gameShape;

        ++s_idCounter;
    }

    public GameObject(final GameObject other) {
        m_type = other.m_type;
        m_id = other.m_id;
        m_color = new Color(other.m_color.getRGB());
        m_lifespanInTicks = other.m_lifespanInTicks;
        m_creationTime = other.m_creationTime;
        m_gameShape = other.m_gameShape.clone();
    }

    public GameObjectType getType() {
        return m_type;
    }

    public int getId() {
        return m_id;
    }

    public Color getColor() {
        return m_color;
    }

    public int getLifespanInTicks() {
        return m_lifespanInTicks;
    }

    public long getCreationTime() {
        return m_creationTime;
    }

    public GameShape getGameShape() {
        return m_gameShape;
    }

    public byte[] toBytes() {
        byte[] rawFieldBytes = ByteBuffer.allocate(C_BYTES_COUNT)
                .put(m_type.getId()).putInt(m_id).putInt(m_color.getRGB())
                .putInt(m_lifespanInTicks).putLong(m_creationTime).array();
        byte[] shapeRawBytes = m_gameShape.toBytes();

        byte[] rawBytes = new byte[rawFieldBytes.length + shapeRawBytes.length];

        System.arraycopy(rawFieldBytes, 0, rawBytes, 0, rawFieldBytes.length);
        System.arraycopy(shapeRawBytes, 0, rawBytes, rawFieldBytes.length, shapeRawBytes.length);

        return rawBytes;
    }

    public boolean fromBytes(final ByteBuffer byteBuffer) {
        if (byteBuffer.remaining() < C_BYTES_COUNT - GameObjectType.C_BYTES_COUNT) return false;

        m_id = byteBuffer.getInt();
        m_color = ColorUtils.fromBytes(byteBuffer);
        m_lifespanInTicks = byteBuffer.getInt();
        m_creationTime = byteBuffer.getLong();
        m_gameShape = GameShapeFromBytesFactory.produceGameShapeWithBytes(byteBuffer);

        return (m_gameShape != null);
    }
}
