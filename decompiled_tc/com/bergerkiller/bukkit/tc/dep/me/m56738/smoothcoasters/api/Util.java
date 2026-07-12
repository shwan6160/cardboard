/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

class Util {
    Util() {
    }

    public static int readVarInt(ByteBuffer buffer) {
        byte b;
        int length = 0;
        int result = 0;
        do {
            b = buffer.get();
            result |= (b & 0x7F) << length * 7;
            if (++length <= 5) continue;
            throw new IllegalArgumentException();
        } while ((b & 0x80) != 0);
        return result;
    }

    public static String readString(ByteBuffer buffer, int limit) {
        int length = Util.readVarInt(buffer);
        if (length > buffer.remaining()) {
            throw new BufferUnderflowException();
        }
        if (length > limit) {
            throw new IllegalArgumentException("String too long: " + length + " > " + limit);
        }
        byte[] data = new byte[length];
        buffer.get(data);
        return new String(data);
    }
}

