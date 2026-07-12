/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 */
package com.bergerkiller.bukkit.tc.signactions.mutex.railslot;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlotType;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class MutexRailSlot {
    private final IntVector3 rail;
    private MutexZoneSlotType type;
    private int ticksLastProbed;

    public MutexRailSlot(IntVector3 rail) {
        this.rail = rail;
        this.ticksLastProbed = -1;
        this.type = MutexZoneSlotType.SMART;
    }

    void probe(int nowTicks) {
        this.ticksLastProbed = nowTicks;
    }

    void probe(MutexZoneSlotType type, int nowTicks) {
        if (type == MutexZoneSlotType.NORMAL) {
            this.type = type;
        }
        this.ticksLastProbed = nowTicks;
    }

    public IntVector3 rail() {
        return this.rail;
    }

    public MutexZoneSlotType type() {
        return this.type;
    }

    public boolean isFullLocking() {
        return this.type == MutexZoneSlotType.NORMAL;
    }

    public boolean isNew() {
        return this.ticksLastProbed < 0;
    }

    public int ticksLastProbed() {
        return this.ticksLastProbed;
    }

    public void debugPrint(StringBuilder str) {
        str.append("[").append(this.rail.x).append("/").append(this.rail.y).append("/").append(this.rail.z).append("]");
        str.append(" ").append(this.type.name());
    }

    public void writeTo(DataOutputStream stream) throws IOException {
        this.rail.write(stream);
        this.type.writeTo(stream);
    }

    public static MutexRailSlot read(DataInputStream stream) throws IOException {
        MutexRailSlot slot = new MutexRailSlot(IntVector3.read((DataInputStream)stream));
        slot.type = MutexZoneSlotType.readFrom(stream);
        return slot;
    }
}

