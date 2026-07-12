/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeField;

public class CommonPacket {
    private Object packet;
    private PacketType type;

    public CommonPacket(PacketType packetType) {
        this.type = packetType;
        this.packet = this.type.newInstance();
    }

    public CommonPacket(Object packetHandle) {
        this(packetHandle, PacketType.getType(packetHandle));
    }

    public CommonPacket(Object packetHandle, PacketType packetType) {
        this.type = packetType;
        this.packet = packetHandle;
    }

    public PacketType getType() {
        return this.type;
    }

    public Object getHandle() {
        return this.packet;
    }

    public <T> void write(FieldAccessor<T> fieldAccessor, T value) {
        fieldAccessor.set(this.getHandle(), value);
    }

    public void write(String field, Object value) {
        try {
            SafeField.set(this.packet, field, value);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid field name: " + field, e);
        }
    }

    public void write(int index, Object value) throws IllegalArgumentException {
        this.write(this.type.getFieldAt(index), value);
    }

    public <T> T read(FieldAccessor<T> fieldAccessor) {
        return fieldAccessor.get(this.getHandle());
    }

    public Object read(String field) {
        try {
            return SafeField.get(this.packet, field, null);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid field name: " + field, e);
        }
    }

    public <T> T read(String field, Class<T> fieldType) {
        try {
            return SafeField.get(this.packet, field, fieldType);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid field name: " + field, e);
        }
    }

    public Object read(int index) throws IllegalArgumentException {
        return this.read(this.type.getFieldAt(index));
    }

    public void setDatawatcher(DataWatcher datawatcher) throws IllegalArgumentException {
        this.write(this.type.getMetaDataField(), datawatcher);
    }

    public Object getDatawatcher() throws IllegalArgumentException {
        return this.read(this.type.getMetaDataField());
    }

    public int readInt(int index) throws IllegalArgumentException {
        return (Integer)this.read(index);
    }

    public boolean readBoolean(int index) throws IllegalArgumentException {
        return (Boolean)this.read(index);
    }

    public byte readByte(int index) throws IllegalArgumentException {
        return (Byte)this.read(index);
    }

    public String readString(int index) throws IllegalArgumentException {
        return (String)this.read(index);
    }

    public double readDouble(int index) throws IllegalArgumentException {
        return (Double)this.read(index);
    }

    public CommonPacket clone() {
        return new CommonPacket(this.getType().cloneInstance(this.getHandle()));
    }

    public String toString() {
        Object handle = this.getHandle();
        if (handle == null) {
            return "null";
        }
        PacketType type = this.getType();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(type).append(" {\n");
            for (SafeField<?> field : ClassTemplate.create(type).getFields()) {
                Object fieldValue;
                if (field.isStatic() || !((fieldValue = field.get(type)) instanceof FieldAccessor)) continue;
                String name = field.getName();
                Object value = ((FieldAccessor)fieldValue).get(handle);
                builder.append("  ").append(name).append(" = ").append((String)Conversion.toString.convert(value)).append('\n');
            }
            builder.append("}");
            return builder.toString();
        }
        catch (Throwable builder) {
            StringBuilder builder2 = new StringBuilder();
            if (handle.getClass().getName().startsWith(Common.NMS_ROOT)) {
                builder2.append(handle.getClass().getSimpleName());
            } else {
                builder2.append(handle.getClass().getName());
            }
            builder2.append(" {\n");
            for (SafeField<?> field : ClassTemplate.create(handle).getFields()) {
                if (field.isStatic()) continue;
                builder2.append("  ").append(field.getName()).append(" = ").append((String)Conversion.toString.convert(field.get(handle))).append('\n');
            }
            builder2.append("}");
            return builder2.toString();
        }
    }
}

