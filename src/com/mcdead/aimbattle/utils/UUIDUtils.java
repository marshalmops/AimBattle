package com.mcdead.aimbattle.utils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.logging.Logger;

public class UUIDUtils {
    public static int C_UUID_BYTES_COUNT = Long.BYTES * 2;

    public static UUID fromBytes(final byte[] bytes) {
        if (bytes.length < C_UUID_BYTES_COUNT) return null;

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        long firstLong = buffer.getLong();
        long secondLong = buffer.getLong();

        return new UUID(firstLong, secondLong);
    }

    public static UUID fromBytes(final ByteBuffer bytesBuffer) {
        if (bytesBuffer.remaining() < C_UUID_BYTES_COUNT) return null;

        long firstLong = bytesBuffer.getLong();
        long secondLong = bytesBuffer.getLong();

        return new UUID(firstLong, secondLong);
    }

    public static byte[] toBytes(final UUID uuid) {
        long firstLong = uuid.getMostSignificantBits();
        long secondLong = uuid.getLeastSignificantBits();

        return ByteBuffer.allocate(Long.BYTES * 2)
                .putLong(firstLong).putLong(secondLong)
                .array();
    }

    public static void main(String[] argv) {
        UUID someUUID = UUID.randomUUID();

        Logger.getGlobal().info(someUUID.toString());

        byte[] someUUIDasBytes = toBytes(someUUID);
        UUID someUUIDfromBytes = fromBytes(someUUIDasBytes);

        Logger.getGlobal().info(someUUIDfromBytes.toString());
    }
}
