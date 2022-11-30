package com.mcdead.aimbattle.screen.game.objects.shape;

import java.awt.geom.Ellipse2D;
import java.nio.ByteBuffer;

public class GameShapeCircle extends GameShape {
    public static int C_BYTES_COUNT = Float.BYTES * 3;

    public GameShapeCircle() {
        super(GameShapeType.CIRCLE);
    }

    public GameShapeCircle(final int x, final int y, final int diagonalSize) {
        super(GameShapeType.CIRCLE, new Ellipse2D.Float((float)x, (float)y, (float)diagonalSize, (float)diagonalSize));
    }

    public GameShapeCircle(final Ellipse2D.Float ellipseShape) {
        super(GameShapeType.CIRCLE, ellipseShape);
    }

    @Override
    public Ellipse2D.Float getShape() {
        return (Ellipse2D.Float) super.getShape();
    }

    @Override
    public GameShapeCircle clone() {
        return new GameShapeCircle((Ellipse2D.Float)getShape().clone());
    }

    @Override
    public byte[] toBytes() {
        byte[] rawSuperBytes = super.toBytes();

        Ellipse2D.Float shape = getShape();
        byte[] rawEllipseBytes = ByteBuffer.allocate(C_BYTES_COUNT)
                .putFloat(shape.x).putFloat(shape.y)
                .putFloat(shape.width).array();

        byte[] rawBytes = new byte[rawSuperBytes.length + rawEllipseBytes.length];

        System.arraycopy(rawSuperBytes, 0, rawBytes, 0, rawSuperBytes.length);
        System.arraycopy(rawEllipseBytes, 0, rawBytes, rawSuperBytes.length, rawEllipseBytes.length);

        return rawBytes;
    }

    @Override
    public boolean fromBytes(final ByteBuffer byteBuffer) {
        if (!super.fromBytes(byteBuffer)) return false;
        if (byteBuffer.remaining() < C_BYTES_COUNT) return false;

        float x = byteBuffer.getFloat();
        float y = byteBuffer.getFloat();
        float d = byteBuffer.getFloat();

        m_shape = new Ellipse2D.Float(x, y, d, d);

        return true;
    }
}
