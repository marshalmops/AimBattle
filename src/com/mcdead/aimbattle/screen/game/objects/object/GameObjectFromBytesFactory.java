package com.mcdead.aimbattle.screen.game.objects.object;

import com.mcdead.aimbattle.screen.game.objects.shape.GameShapeType;

import java.nio.ByteBuffer;

public class GameObjectFromBytesFactory {
    public static GameObject produceGameObjectWithBytes(final byte[] bytes) {
        if (bytes.length < GameShapeType.C_BYTES_COUNT) return null;

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        return produceGameObjectWithBytes(buffer);
    }

    public static GameObject produceGameObjectWithBytes(final ByteBuffer bytesBuffer) {
        if (bytesBuffer.remaining() < GameObject.C_BYTES_COUNT) return null;

        GameObjectType type = GameObjectType.getTypeById(bytesBuffer.get());
        GameObject object = null;

        switch (type) {
            case AIM -> {object = produceEmptyGameObjectAim();}
        }

        return object.fromBytes(bytesBuffer) ? object : null;
    }

    private static GameObject produceEmptyGameObjectAim() {
        return new Aim();
    }
}
