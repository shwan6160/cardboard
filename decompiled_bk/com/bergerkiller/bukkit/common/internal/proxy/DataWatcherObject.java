/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.proxy;

public class DataWatcherObject<T> {
    private final int id;
    private final Integer serializer;

    public DataWatcherObject(int id, Object token) {
        if (!(token instanceof Integer)) {
            throw new IllegalArgumentException("Legacy DataWatcherObject must use Integer typeId tokens!");
        }
        this.id = id;
        this.serializer = (Integer)token;
    }

    public int getId() {
        return this.id;
    }

    public Object getSerializer() {
        return this.serializer;
    }

    public int getSerializerId() {
        return this.serializer;
    }
}

