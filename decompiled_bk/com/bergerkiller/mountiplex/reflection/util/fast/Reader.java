/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import java.lang.reflect.Field;

public interface Reader<T> {
    public T get(Object var1);

    public double getDouble(Object var1);

    public float getFloat(Object var1);

    public byte getByte(Object var1);

    public short getShort(Object var1);

    public int getInteger(Object var1);

    public long getLong(Object var1);

    public char getCharacter(Object var1);

    public boolean getBoolean(Object var1);

    public Field getReadField();

    public void checkCanRead();
}

