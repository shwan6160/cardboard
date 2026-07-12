/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.IgnoresRemapping;

public class FastConvertedField<T>
implements IgnoresRemapping {
    private final FastField<Object> field;
    private final DuplexConverter<Object, T> converter;

    public FastConvertedField(FastField<?> field, DuplexConverter<?, T> converter) {
        this.field = field;
        this.converter = converter;
    }

    public T get(Object instance) {
        return this.converter.convertInput(this.field.get(instance));
    }

    public void set(Object instance, T value) {
        this.field.set(instance, this.converter.convertOutput(value));
    }

    public byte getByte(Object instance) {
        return (Byte)this.get(instance);
    }

    public short getShort(Object instance) {
        return (Short)this.get(instance);
    }

    public int getInteger(Object instance) {
        return (Integer)this.get(instance);
    }

    public long getLong(Object instance) {
        return (Long)this.get(instance);
    }

    public char getCharacter(Object instance) {
        return ((Character)this.get(instance)).charValue();
    }

    public float getFloat(Object instance) {
        return ((Float)this.get(instance)).floatValue();
    }

    public double getDouble(Object instance) {
        return (Double)this.get(instance);
    }

    public void setByte(Object instance, byte value) {
        this.set(instance, value);
    }

    public void setShort(Object instance, short value) {
        this.set(instance, value);
    }

    public void setInteger(Object instance, int value) {
        this.set(instance, value);
    }

    public void setLong(Object instance, long value) {
        this.set(instance, value);
    }

    public void setCharacter(Object instance, char value) {
        this.set(instance, Character.valueOf(value));
    }

    public void setFloat(Object instance, float value) {
        this.set(instance, Float.valueOf(value));
    }

    public void setDouble(Object instance, double value) {
        this.set(instance, value);
    }
}

