package com.mcdead.aimbattle.screen.game.objects.shape;

import com.mcdead.aimbattle.ByteConvertable;

import java.awt.*;
import java.nio.ByteBuffer;

public abstract class GameShape implements Cloneable, ByteConvertable {
    public static int C_BYTES_COUNT = GameShapeType.C_BYTES_COUNT;

    private GameShapeType m_type;
    protected Shape m_shape;

    public GameShape(final GameShapeType type) {
        m_type = type;
        m_shape = null;
    }

    public GameShape(final GameShapeType type, final Shape shape) {
        m_type = type;
        m_shape = shape;
    }

    public GameShapeType getType() {
        return m_type;
    }

    public Shape getShape() {
        return m_shape;
    }

    public abstract GameShape clone();

    public byte[] toBytes() {
        return ByteBuffer.allocate(C_BYTES_COUNT)
                .put(m_type.getId()).array();
    }

    public boolean fromBytes(final ByteBuffer byteBuffer) {
        if (byteBuffer.remaining() < C_BYTES_COUNT - GameShapeType.C_BYTES_COUNT)
            return false;

        return true;
    }
}
