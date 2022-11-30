package com.mcdead.aimbattle.screen.game.objects.shape;

import java.nio.ByteBuffer;

public class GameShapeFromBytesFactory {
    public static GameShape produceGameShapeWithBytes(final byte[] bytes) {
        if (bytes.length < GameShapeType.C_BYTES_COUNT) return null;

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        return produceGameShapeWithBytes(buffer);
    }

    public static GameShape produceGameShapeWithBytes(final ByteBuffer bytesBuffer) {
        if (bytesBuffer.remaining() < GameShapeType.C_BYTES_COUNT) return null;

        GameShapeType shapeType = GameShapeType.getTypeById(bytesBuffer.get());
        GameShape shape = null;

        switch (shapeType) {
            case CIRCLE -> {shape = produceEmptyGameShapeCircle(bytesBuffer);}
        }

        return shape.fromBytes(bytesBuffer) ? shape : null;
    }

    private static GameShape produceEmptyGameShapeCircle(final ByteBuffer buffer) {
        return new GameShapeCircle();
    }
}
