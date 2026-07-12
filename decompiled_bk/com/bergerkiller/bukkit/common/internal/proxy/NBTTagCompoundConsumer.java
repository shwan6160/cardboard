/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import java.util.function.Consumer;

public class NBTTagCompoundConsumer
implements Consumer<Object> {
    private final Consumer<CommonTagCompound> consumer;

    public NBTTagCompoundConsumer(Consumer<CommonTagCompound> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void accept(Object o) {
        this.consumer.accept(CommonTagCompound.create(o));
    }
}

