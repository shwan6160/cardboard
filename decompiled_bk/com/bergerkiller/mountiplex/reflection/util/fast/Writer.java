/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import java.lang.reflect.Field;

public interface Writer<T> {
    public void set(Object var1, T var2);

    public void setDouble(Object var1, double var2);

    public void setFloat(Object var1, float var2);

    public void setByte(Object var1, byte var2);

    public void setShort(Object var1, short var2);

    public void setInteger(Object var1, int var2);

    public void setLong(Object var1, long var2);

    public void setCharacter(Object var1, char var2);

    public void setBoolean(Object var1, boolean var2);

    public Field getWriteField();

    public void checkCanWrite();
}

