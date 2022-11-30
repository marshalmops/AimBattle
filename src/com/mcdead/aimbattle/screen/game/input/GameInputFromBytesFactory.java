package com.mcdead.aimbattle.screen.game.input;

import com.mcdead.aimbattle.screen.game.objects.object.GameObject;
import com.mcdead.aimbattle.screen.game.objects.shape.GameShapeType;

import java.nio.ByteBuffer;

public class GameInputFromBytesFactory {
    public static GameInput produceGameInputWithBytes(final byte[] bytes) {
        if (bytes.length < GameInput.C_BYTES_COUNT) return null;

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        return produceGameInputWithBytes(buffer);
    }

    public static GameInput produceGameInputWithBytes(final ByteBuffer bytesBuffer) {
        if (bytesBuffer.remaining() < GameInput.C_BYTES_COUNT) return null;

        GameInputType type = GameInputType.getTypeById(bytesBuffer.get());
        GameInput input = null;

        switch (type) {
            case MOUSE_CLICK -> {input = produceEmptyGameInputMouseClick();}
        }

        return input.fromBytes(bytesBuffer) ? input : null;
    }

    private static GameInput produceEmptyGameInputMouseClick() {
        return new GameInputMouseClick();
    }
}
