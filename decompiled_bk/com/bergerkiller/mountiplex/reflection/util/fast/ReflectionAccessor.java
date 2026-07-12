/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.Copier;
import com.bergerkiller.mountiplex.reflection.util.fast.GeneratedAccessor;
import com.bergerkiller.mountiplex.reflection.util.fast.Reader;
import com.bergerkiller.mountiplex.reflection.util.fast.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionAccessor<T>
implements Reader<T>,
Writer<T>,
Copier {
    private final Field f;

    protected ReflectionAccessor(Field field) {
        this.f = field;
    }

    private RuntimeException checkInstance(Object instance) {
        if (Modifier.isStatic(this.f.getModifiers())) {
            if (instance != null) {
                return new IllegalArgumentException("Instance should be null for static fields, but was " + MPLType.getName(instance.getClass()) + " instead");
            }
        } else {
            if (instance == null) {
                return new IllegalArgumentException("Instance can not be null for member fields declared in " + MPLType.getName(this.f.getDeclaringClass()));
            }
            if (!this.f.getDeclaringClass().isAssignableFrom(instance.getClass())) {
                return new IllegalArgumentException("Instance of type " + MPLType.getName(instance.getClass()) + " does not contain the field declared in " + MPLType.getName(this.f.getDeclaringClass()));
            }
        }
        return null;
    }

    private RuntimeException f(Object instance, Throwable t) {
        RuntimeException iex = this.checkInstance(instance);
        if (iex != null) {
            return iex;
        }
        if (t instanceof RuntimeException) {
            return (RuntimeException)t;
        }
        return new RuntimeException("Failed to get field", t);
    }

    private RuntimeException f(Object instance, Object value, Throwable t) {
        RuntimeException iex = this.checkInstance(instance);
        if (iex != null) {
            return iex;
        }
        Class<?> fieldType = this.f.getType();
        if (fieldType.isPrimitive()) {
            if (value == null) {
                return new IllegalArgumentException("Field primitive type " + fieldType.getName() + " can not be assigned null");
            }
            Class<?> valueType = BoxedType.getUnboxedType(value.getClass());
            if (valueType == null || !fieldType.isAssignableFrom(valueType)) {
                return new IllegalArgumentException("value type " + MPLType.getName(value.getClass()) + " can not be assigned to primitive field type " + fieldType.getName());
            }
        } else if (value != null && !fieldType.isAssignableFrom(value.getClass())) {
            return new IllegalArgumentException("value type " + MPLType.getName(value.getClass()) + " can not be assigned to field type " + MPLType.getName(fieldType));
        }
        if (t instanceof RuntimeException) {
            return (RuntimeException)t;
        }
        return new RuntimeException("Failed to set field", t);
    }

    @Override
    public T get(Object o) {
        try {
            return (T)this.f.get(o);
        }
        catch (Throwable t) {
            throw this.f(o, t);
        }
    }

    @Override
    public double getDouble(Object o) {
        try {
            return this.f.getDouble(o);
        }
        catch (Throwable t) {
            throw this.f(o, t);
        }
    }

    @Override
    public float getFloat(Object o) {
        try {
            return this.f.getFloat(o);
        }
        catch (Throwable t) {
            throw this.f(o, t);
        }
    }

    @Override
    public byte getByte(Object o) {
        try {
            return this.f.getByte(o);
        }
        catch (Throwable t) {
            throw this.f(o, t);
        }
    }

    @Override
    public short getShort(Object o) {
        try {
            return this.f.getShort(o);
        }
        catch (Throwable t) {
            throw this.f(o, t);
        }
    }

    @Override
    public int getInteger(Object o) {
        try {
            return this.f.getInt(o);
        }
        catch (Throwable t) {
            throw this.f(o, t);
        }
    }

    @Override
    public long getLong(Object o) {
        try {
            return this.f.getLong(o);
        }
        catch (Throwable t) {
            throw this.f(o, t);
        }
    }

    @Override
    public char getCharacter(Object o) {
        try {
            return this.f.getChar(o);
        }
        catch (Throwable t) {
            throw this.f(o, t);
        }
    }

    @Override
    public boolean getBoolean(Object o) {
        try {
            return this.f.getBoolean(o);
        }
        catch (Throwable t) {
            throw this.f(o, t);
        }
    }

    @Override
    public Field getReadField() {
        return this.f;
    }

    @Override
    public void checkCanRead() {
    }

    @Override
    public void set(Object o, T v) {
        try {
            this.f.set(o, v);
        }
        catch (Throwable t) {
            throw this.f(o, v, t);
        }
    }

    @Override
    public void setDouble(Object o, double v) {
        try {
            this.f.setDouble(o, v);
        }
        catch (Throwable t) {
            throw this.f(o, v, t);
        }
    }

    @Override
    public void setFloat(Object o, float v) {
        try {
            this.f.setFloat(o, v);
        }
        catch (Throwable t) {
            throw this.f(o, Float.valueOf(v), t);
        }
    }

    @Override
    public void setByte(Object o, byte v) {
        try {
            this.f.setByte(o, v);
        }
        catch (Throwable t) {
            throw this.f(o, v, t);
        }
    }

    @Override
    public void setShort(Object o, short v) {
        try {
            this.f.setShort(o, v);
        }
        catch (Throwable t) {
            throw this.f(o, v, t);
        }
    }

    @Override
    public void setInteger(Object o, int v) {
        try {
            this.f.setInt(o, v);
        }
        catch (Throwable t) {
            throw this.f(o, v, t);
        }
    }

    @Override
    public void setLong(Object o, long v) {
        try {
            this.f.setLong(o, v);
        }
        catch (Throwable t) {
            throw this.f(o, v, t);
        }
    }

    @Override
    public void setCharacter(Object o, char v) {
        try {
            this.f.setChar(o, v);
        }
        catch (Throwable t) {
            throw this.f(o, Character.valueOf(v), t);
        }
    }

    @Override
    public void setBoolean(Object o, boolean v) {
        try {
            this.f.setBoolean(o, v);
        }
        catch (Throwable t) {
            throw this.f(o, v, t);
        }
    }

    @Override
    public Field getWriteField() {
        return this.f;
    }

    @Override
    public void checkCanWrite() {
    }

    @Override
    public void copy(Object instanceFrom, Object instanceTo) {
        this.set(instanceTo, this.get(instanceFrom));
    }

    @Override
    public Field getCopyField() {
        return this.f;
    }

    @Override
    public void checkCanCopy() {
    }

    public static <T> ReflectionAccessor<T> create(Field field) {
        int modifiers = field.getModifiers();
        if (Resolver.isPublic(field) || Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
            return GeneratedAccessor.create(field);
        }
        return new ReflectionAccessor<T>(field);
    }
}

