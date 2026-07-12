/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.signactions.mutex;

import com.bergerkiller.bukkit.tc.Util;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public enum MutexZoneSlotType {
    NORMAL,
    SMART;

    private static final MutexZoneSlotType[] SLOT_TYPES;

    public static MutexZoneSlotType readFrom(InputStream stream) throws IOException {
        int typeOrd = Util.readVariableLengthInt(stream);
        return typeOrd >= 0 && typeOrd < SLOT_TYPES.length ? SLOT_TYPES[typeOrd] : NORMAL;
    }

    public void writeTo(OutputStream stream) throws IOException {
        Util.writeVariableLengthInt(stream, this.ordinal());
    }

    static {
        SLOT_TYPES = MutexZoneSlotType.values();
    }
}

