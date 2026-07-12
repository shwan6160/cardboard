/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class SecureField {
    private Field field = null;
    private boolean canGet = false;
    private boolean canSet = false;

    public final void init(Field field) {
        this.field = field;
        this.canGet = false;
        this.canSet = false;
    }

    public final void deinit() {
        this.field = null;
        this.canGet = false;
        this.canSet = false;
    }

    public final boolean isInit() {
        return this.field != null;
    }

    public Class<?> getType() {
        return this.field.getType();
    }

    public final Field get() {
        return this.field;
    }

    public final Field read() {
        if (this.field == null) {
            throw new RuntimeException("Field is not initialized");
        }
        if (!this.canGet) {
            if (!this.field.isAccessible() && !Modifier.isPublic(this.field.getModifiers())) {
                try {
                    this.field.setAccessible(true);
                }
                catch (Throwable t) {
                    throw new RuntimeException("Failed to make field " + MPLType.getName(this.field) + " accessible");
                }
            }
            this.canGet = true;
        }
        return this.field;
    }

    public final Field write() {
        if (this.field == null) {
            throw new RuntimeException("Field is not initialized");
        }
        if (!this.canSet) {
            if (!this.field.isAccessible()) {
                try {
                    this.field.setAccessible(true);
                }
                catch (Throwable t) {
                    throw new RuntimeException("Failed to make field " + MPLType.getName(this.field) + " accessible");
                }
            }
            this.canGet = true;
            this.canSet = true;
        }
        return this.field;
    }

    public final void checkInit() {
        if (this.field == null) {
            throw new RuntimeException("Field is not initialized");
        }
    }

    public final void checkGet() {
        this.checkInit();
    }

    public final void checkSet(Object value) {
        this.checkInit();
        Class<?> valueType = this.field.getType();
        if (valueType.isPrimitive() && value == null) {
            throw new IllegalArgumentException("Field primitive type " + valueType.getName() + " can not be assigned null");
        }
        if (value != null && valueType.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("value type " + MPLType.getName(value.getClass()) + " can not be assigned to field type " + MPLType.getName(valueType));
        }
    }

    public final void checkInstance(Object instance) {
        if (this.field == null) {
            return;
        }
        if (Modifier.isStatic(this.field.getModifiers())) {
            if (instance != null) {
                throw new IllegalArgumentException("Field is static, no instance should be used");
            }
        } else {
            if (instance == null) {
                throw new IllegalArgumentException("Instance is null");
            }
            Class<?> type = this.field.getDeclaringClass();
            if (!type.isAssignableFrom(instance.getClass())) {
                throw new IllegalArgumentException("Failed to get field: instance is not an instance of " + MPLType.getName(type));
            }
        }
    }
}

