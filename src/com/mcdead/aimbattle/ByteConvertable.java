package com.mcdead.aimbattle;

import java.nio.ByteBuffer;

public interface ByteConvertable {
    byte[] toBytes();
    boolean fromBytes(final ByteBuffer byteBuffer);
}
