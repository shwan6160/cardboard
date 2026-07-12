/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util;

import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.IgnoresRemapping;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.mountiplex.reflection.util.fast.Copier;
import com.bergerkiller.mountiplex.reflection.util.fast.Reader;
import com.bergerkiller.mountiplex.reflection.util.fast.ReflectionAccessor;
import com.bergerkiller.mountiplex.reflection.util.fast.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class FastField<T>
implements Reader<T>,
Writer<T>,
Copier,
LazyInitializedObject,
IgnoresRemapping {
    private Reader<T> reader;
    private Writer<T> writer;
    private Copier copier;
    private Field field;
    private String missingInfo = "!!UNKNOWN!!";

    public FastField() {
        this.init(null);
    }

    public FastField(Field field) {
        this.init(field);
    }

    public final void init(Field field) {
        FastFieldInitProxy initProxy;
        this.field = field;
        this.reader = initProxy = new FastFieldInitProxy();
        this.writer = initProxy;
        this.copier = initProxy;
    }

    public final void initUnavailable(String missingInfo) {
        this.init(null);
        this.missingInfo = missingInfo;
    }

    public final void checkInit() {
        if (this.field == null) {
            throw new UnsupportedOperationException("Field " + this.missingInfo + " is not available");
        }
    }

    public final boolean isAvailable() {
        return this.field != null;
    }

    public final Field getField() {
        return this.field;
    }

    public final Class<T> getType() {
        if (this.field == null) {
            return null;
        }
        return this.field.getType();
    }

    public final String getName() {
        if (this.field == null) {
            return "null";
        }
        return MPLType.getName(this.field);
    }

    public final String getDescription() {
        if (this.field == null) {
            return this.missingInfo;
        }
        return this.field.toString();
    }

    public final boolean isStatic() {
        return this.field != null ? Modifier.isStatic(this.field.getModifiers()) : false;
    }

    private void makeAccessible() {
        try {
            this.field.setAccessible(true);
        }
        catch (Throwable t) {
            throw new RuntimeException("Failed to make field " + MPLType.getName(this.field) + " accessible");
        }
    }

    @Override
    public void forceInitialization() {
        this.reader.checkCanRead();
    }

    @Override
    public final void checkCanCopy() {
        this.checkCanWrite();
        this.checkCanRead();
    }

    @Override
    public final void checkCanWrite() {
        this.writer.checkCanWrite();
    }

    @Override
    public final void checkCanRead() {
        this.reader.checkCanRead();
    }

    @Override
    public T get(Object o) {
        return this.reader.get(o);
    }

    @Override
    public double getDouble(Object o) {
        return this.reader.getDouble(o);
    }

    @Override
    public float getFloat(Object o) {
        return this.reader.getFloat(o);
    }

    @Override
    public byte getByte(Object o) {
        return this.reader.getByte(o);
    }

    @Override
    public short getShort(Object o) {
        return this.reader.getShort(o);
    }

    @Override
    public int getInteger(Object o) {
        return this.reader.getInteger(o);
    }

    @Override
    public long getLong(Object o) {
        return this.reader.getLong(o);
    }

    @Override
    public char getCharacter(Object o) {
        return this.reader.getCharacter(o);
    }

    @Override
    public boolean getBoolean(Object o) {
        return this.reader.getBoolean(o);
    }

    @Override
    public Field getReadField() {
        return this.reader.getReadField();
    }

    @Override
    public void set(Object o, T v) {
        this.writer.set(o, v);
    }

    @Override
    public void setDouble(Object o, double v) {
        this.writer.setDouble(o, v);
    }

    @Override
    public void setFloat(Object o, float v) {
        this.writer.setFloat(o, v);
    }

    @Override
    public void setByte(Object o, byte v) {
        this.writer.setByte(o, v);
    }

    @Override
    public void setShort(Object o, short v) {
        this.writer.setShort(o, v);
    }

    @Override
    public void setInteger(Object o, int v) {
        this.writer.setInteger(o, v);
    }

    @Override
    public void setLong(Object o, long v) {
        this.writer.setLong(o, v);
    }

    @Override
    public void setCharacter(Object o, char v) {
        this.writer.setCharacter(o, v);
    }

    @Override
    public void setBoolean(Object o, boolean v) {
        this.writer.setBoolean(o, v);
    }

    @Override
    public Field getWriteField() {
        return this.writer.getWriteField();
    }

    @Override
    public void copy(Object a, Object b) {
        this.copier.copy(a, b);
    }

    @Override
    public Field getCopyField() {
        return this.copier.getCopyField();
    }

    private final class FastFieldInitProxy
    implements Reader<T>,
    Writer<T>,
    Copier {
        private FastFieldInitProxy() {
        }

        private ReflectionAccessor<T> access() {
            if (FastField.this.writer instanceof ReflectionAccessor) {
                return (ReflectionAccessor)FastField.this.writer;
            }
            if (FastField.this.reader instanceof ReflectionAccessor) {
                return (ReflectionAccessor)FastField.this.reader;
            }
            if (FastField.this.copier instanceof ReflectionAccessor) {
                return (ReflectionAccessor)FastField.this.copier;
            }
            return ReflectionAccessor.create(FastField.this.field);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Reader<T> read() {
            if (FastField.this.reader == this) {
                FastField fastField = FastField.this;
                synchronized (fastField) {
                    if (FastField.this.reader == this) {
                        FastField.this.checkInit();
                        if (!Resolver.isPublic(FastField.this.field)) {
                            FastField.this.makeAccessible();
                        }
                        FastField.this.reader = this.access();
                    }
                }
            }
            return FastField.this.reader;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Writer<T> write() {
            if (FastField.this.writer == this) {
                FastField fastField = FastField.this;
                synchronized (fastField) {
                    if (FastField.this.writer == this) {
                        FastField.this.checkInit();
                        int mod = FastField.this.field.getModifiers();
                        if (!Resolver.isPublic(FastField.this.field) || Modifier.isFinal(mod)) {
                            FastField.this.makeAccessible();
                        }
                        FastField.this.writer = this.access();
                    }
                }
            }
            return FastField.this.writer;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Copier copy() {
            if (FastField.this.copier == this) {
                FastField fastField = FastField.this;
                synchronized (fastField) {
                    if (FastField.this.copier == this) {
                        FastField.this.checkInit();
                        int mod = FastField.this.field.getModifiers();
                        if (Modifier.isStatic(mod)) {
                            throw new RuntimeException("Static fields can not be copied");
                        }
                        if (!Resolver.isPublic(FastField.this.field) || Modifier.isFinal(mod)) {
                            FastField.this.makeAccessible();
                        }
                        FastField.this.copier = this.access();
                    }
                }
            }
            return FastField.this.copier;
        }

        @Override
        public final void checkCanCopy() {
            this.checkCanWrite();
            this.checkCanRead();
        }

        @Override
        public final void checkCanWrite() {
            this.write().checkCanWrite();
        }

        @Override
        public final void checkCanRead() {
            this.read().checkCanRead();
        }

        @Override
        public T get(Object o) {
            return this.read().get(o);
        }

        @Override
        public double getDouble(Object o) {
            return this.read().getDouble(o);
        }

        @Override
        public float getFloat(Object o) {
            return this.read().getFloat(o);
        }

        @Override
        public byte getByte(Object o) {
            return this.read().getByte(o);
        }

        @Override
        public short getShort(Object o) {
            return this.read().getShort(o);
        }

        @Override
        public int getInteger(Object o) {
            return this.read().getInteger(o);
        }

        @Override
        public long getLong(Object o) {
            return this.read().getLong(o);
        }

        @Override
        public char getCharacter(Object o) {
            return this.read().getCharacter(o);
        }

        @Override
        public boolean getBoolean(Object o) {
            return this.read().getBoolean(o);
        }

        @Override
        public Field getReadField() {
            return this.read().getReadField();
        }

        @Override
        public void set(Object o, T v) {
            this.write().set(o, v);
        }

        @Override
        public void setDouble(Object o, double v) {
            this.write().setDouble(o, v);
        }

        @Override
        public void setFloat(Object o, float v) {
            this.write().setFloat(o, v);
        }

        @Override
        public void setByte(Object o, byte v) {
            this.write().setByte(o, v);
        }

        @Override
        public void setShort(Object o, short v) {
            this.write().setShort(o, v);
        }

        @Override
        public void setInteger(Object o, int v) {
            this.write().setInteger(o, v);
        }

        @Override
        public void setLong(Object o, long v) {
            this.write().setLong(o, v);
        }

        @Override
        public void setCharacter(Object o, char v) {
            this.write().setCharacter(o, v);
        }

        @Override
        public void setBoolean(Object o, boolean v) {
            this.write().setBoolean(o, v);
        }

        @Override
        public Field getWriteField() {
            return this.write().getWriteField();
        }

        @Override
        public void copy(Object a, Object b) {
            this.copy().copy(a, b);
        }

        @Override
        public Field getCopyField() {
            return this.copy().getCopyField();
        }
    }
}

