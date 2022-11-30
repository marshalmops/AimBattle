package com.mcdead.aimbattle.utils;

import java.awt.*;
import java.nio.ByteBuffer;

public class ColorUtils {
    public static int C_COLOR_BYTES_COUNT = Integer.BYTES;

    public static byte[] toBytes(final Color color) {
        return ByteBuffer.allocate(C_COLOR_BYTES_COUNT)
                .putInt(color.getRGB()).array();
    }

    public static Color fromBytes(final ByteBuffer bytesBuffer) {
        if (bytesBuffer.remaining() < C_COLOR_BYTES_COUNT) return null;

        return new Color(bytesBuffer.getInt());
    }
}
