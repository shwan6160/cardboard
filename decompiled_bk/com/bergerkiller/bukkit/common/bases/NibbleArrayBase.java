/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.bases;

import com.bergerkiller.generated.net.minecraft.world.level.chunk.DataLayerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.reflection.net.minecraft.server.NMSNibbleArray;

public class NibbleArrayBase {
    private final DataLayerHandle arr;

    public NibbleArrayBase(byte[] data) {
        this.arr = DataLayerHandle.createNew(data);
    }

    public NibbleArrayBase() {
        this.arr = DataLayerHandle.createNew();
    }

    public byte[] getData() {
        return NMSNibbleArray.getValueArray(this.arr.getRaw());
    }

    public byte[] toArray() {
        return NMSNibbleArray.getArrayCopy(this.arr.getRaw());
    }

    public void set(int x, int y, int z, int value) {
        this.arr.set(x, y, z, value);
    }

    public int get(int x, int y, int z) {
        return this.arr.get(x, y, z);
    }

    public Object toHandle() {
        return ((Template.Constructor)DataLayerHandle.T.constr_data.raw).newInstance(this.getData());
    }
}

