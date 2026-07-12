/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.nbt;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.config.TempFileOutputStream;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.nbt.CompoundTagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.ListTagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.NbtIoHandle;
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommonTagCompound
extends CommonTag
implements Map<String, CommonTag> {
    public static final CommonTagCompound EMPTY = CommonTagCompound.makeReadOnly(new CommonTagCompound());

    public CommonTagCompound() {
        this(new HashMap());
    }

    public CommonTagCompound(Map<String, ?> data) {
        this((Object)data);
    }

    protected CommonTagCompound(Object data) {
        super(data);
    }

    public CommonTagCompound(CompoundTagHandle handle) {
        super(handle);
    }

    @Override
    public CompoundTagHandle getBackingHandle() {
        return (CompoundTagHandle)this.handle;
    }

    @Override
    public CommonTagCompound clone() {
        return new CommonTagCompound((Object)((TagHandle)this.handle).clone());
    }

    @Override
    public Map<String, CommonTag> getData() {
        return (Map)super.getData();
    }

    @Override
    protected Map<String, Object> getRawData() {
        return (Map)super.getRawData();
    }

    private Collection<?> getHandleValues() {
        return this.getRawData().values();
    }

    public Object removeValue(String key) {
        if (key == null) {
            return null;
        }
        this.assertWritable();
        Object removedHandle = this.getRawData().remove(key);
        return removedHandle == null ? null : CommonTagCompound.wrapGetDataForHandle(removedHandle, this.readOnly);
    }

    @Override
    public CommonTag remove(Object key) {
        if (key == null) {
            return null;
        }
        this.assertWritable();
        return CommonTag.create(this.getRawData().remove(key.toString()));
    }

    private final <T> T putGetRemove(PutGetRemoveOp op, String key, Class<T> type, T value, boolean returnValue) {
        Object v;
        Object pos;
        Object rawNBTResult = null;
        if (type == UUID.class) {
            UUID uuid = (UUID)value;
            Long uuidMost = this.putGetRemove(op, key + "UUIDMost", Long.class, uuid == null ? null : Long.valueOf(uuid.getMostSignificantBits()), returnValue);
            Long uuidLeast = this.putGetRemove(op, key + "UUIDLeast", Long.class, uuid == null ? null : Long.valueOf(uuid.getLeastSignificantBits()), returnValue);
            if (uuidMost != null && uuidLeast != null) {
                return (T)new UUID(uuidMost, uuidLeast);
            }
        } else if (type == BlockLocation.class) {
            pos = (BlockLocation)value;
            String world = this.putGetRemove(op, key + "World", String.class, pos == null ? null : ((BlockLocation)pos).world, returnValue);
            Integer x = this.putGetRemove(op, key + "X", Integer.class, pos == null ? null : Integer.valueOf(((BlockLocation)pos).x), returnValue);
            Integer y = this.putGetRemove(op, key + "Y", Integer.class, pos == null ? null : Integer.valueOf(((BlockLocation)pos).y), returnValue);
            Integer z = this.putGetRemove(op, key + "Z", Integer.class, pos == null ? null : Integer.valueOf(((BlockLocation)pos).z), returnValue);
            if (world != null && !world.isEmpty() && x != null && y != null && z != null) {
                return (T)new BlockLocation(world, (int)x, (int)y, (int)z);
            }
        } else if (type == IntVector3.class) {
            pos = (IntVector3)value;
            Integer x = this.putGetRemove(op, key + "X", Integer.class, pos == null ? null : Integer.valueOf(((IntVector3)pos).x), returnValue);
            Integer y = this.putGetRemove(op, key + "Y", Integer.class, pos == null ? null : Integer.valueOf(((IntVector3)pos).y), returnValue);
            Integer z = this.putGetRemove(op, key + "Z", Integer.class, pos == null ? null : Integer.valueOf(((IntVector3)pos).z), returnValue);
            if (x != null && y != null && z != null) {
                return (T)new IntVector3(x, y, z);
            }
        } else if (type == IdentifierHandle.class) {
            v = this.putGetRemove(op, key, String.class, value == null ? null : ((IdentifierHandle)value).toString(), returnValue);
            if (v != null) {
                return (T)IdentifierHandle.createNew((String)v);
            }
        } else if (type == ChatText.class) {
            CommonTag result = this.putGetRemove(op, key, CommonTag.class, value == null ? null : ((ChatText)value).getNBT(), returnValue);
            if (result != null) {
                return (T)ChatText.fromNBT(result);
            }
        } else if (type == Boolean.TYPE || type == Boolean.class) {
            v = this.putGetRemove(op, key, Byte.class, value == null ? null : Byte.valueOf((Boolean)value != false ? (byte)1 : 0), returnValue);
            if (v != null) {
                return (T)((Byte)v != 0 ? Boolean.TRUE : Boolean.FALSE);
            }
        } else if (op == PutGetRemoveOp.GET) {
            rawNBTResult = ((Template.Method)CompoundTagHandle.T.get.raw).invoke(this.getRawHandle(), key);
        } else if (op == PutGetRemoveOp.REMOVE || op == PutGetRemoveOp.PUT && value == null) {
            this.assertWritable();
            rawNBTResult = this.getRawData().remove(key);
        } else if (op == PutGetRemoveOp.PUT) {
            this.assertWritable();
            Object putValueNBT = TagHandle.createRawHandleForData(value);
            rawNBTResult = ((Template.Method)CompoundTagHandle.T.put.raw).invoke(this.getRawHandle(), key, putValueNBT);
        }
        if (!returnValue) {
            return null;
        }
        if (rawNBTResult == null) {
            return null;
        }
        if (type == null) {
            return (T)CommonTagCompound.wrapGetDataForHandle(rawNBTResult, this.readOnly);
        }
        if (TagHandle.class.isAssignableFrom(type)) {
            return Conversion.convert(TagHandle.createHandleForData(rawNBTResult), type, null);
        }
        if (CommonTag.class.isAssignableFrom(type)) {
            CommonTag commonTag = TagHandle.createHandleForData(rawNBTResult).toCommonTag();
            commonTag.readOnly = this.readOnly;
            return Conversion.convert(commonTag, type, null);
        }
        if (TagHandle.T.isAssignableFrom(type)) {
            return Conversion.convert(rawNBTResult, type, null);
        }
        if (CompoundTagHandle.T.isAssignableFrom((Object)rawNBTResult)) {
            return Conversion.convert(CommonTagCompound.wrapRawData(CompoundTagHandle.T.data.get(rawNBTResult), this.readOnly), type, null);
        }
        if (ListTagHandle.T.isAssignableFrom((Object)rawNBTResult)) {
            return Conversion.convert(CommonTagCompound.wrapRawData(ListTagHandle.T.data.get(rawNBTResult), this.readOnly), type, null);
        }
        return Conversion.convert(TagHandle.getDataForHandle(rawNBTResult), type, null);
    }

    public <T> T removeValue(String key, Class<T> type) {
        return this.putGetRemove(PutGetRemoveOp.REMOVE, key, type, null, true);
    }

    public <T> T putValue(String key, Class<T> type, T value) {
        return this.putGetRemove(PutGetRemoveOp.PUT, key, type, value, true);
    }

    public void putValue(String key, Object value) {
        this.putGetRemove(PutGetRemoveOp.PUT, key, value == null ? null : value.getClass(), value, false);
    }

    public <T> void putListValues(String key, T ... values) {
        CommonTagList list = new CommonTagList();
        list.setAllValues(values);
        this.put(key, list);
    }

    public <T> T getValue(String key, Class<T> type) {
        return this.putGetRemove(PutGetRemoveOp.GET, key, type, null, true);
    }

    public Object getValue(String key) {
        return this.putGetRemove(PutGetRemoveOp.GET, key, null, null, true);
    }

    public <T> T getValue(String key, Class<T> type, T def) {
        T result = this.putGetRemove(PutGetRemoveOp.GET, key, type, def, true);
        return result == null ? def : result;
    }

    public <T> T getValue(String key, T def) {
        return (T)this.getValue(key, def.getClass(), def);
    }

    public <T> T getAllValues(Class<T> type) {
        T values = Conversion.convert(this.values(), type, null);
        if (values == null) {
            throw new IllegalArgumentException("Unsupported type: " + type.getName());
        }
        return values;
    }

    public UUID getUUID(String key) {
        return (UUID)((Object)this.getValue(key, (Object)UUID.class));
    }

    public void putUUID(String key, UUID data) {
        this.putValue(key, UUID.class, data);
    }

    public IdentifierHandle getMinecraftKey(String key) {
        return (IdentifierHandle)((Object)this.getValue(key, (Object)IdentifierHandle.class));
    }

    public void putMinecraftKey(String key, IdentifierHandle minecraftKey) {
        this.putValue(key, IdentifierHandle.class, minecraftKey);
    }

    public BlockLocation getBlockLocation(String key) {
        return (BlockLocation)((Object)this.getValue(key, (Object)BlockLocation.class));
    }

    public void putBlockLocation(String key, BlockLocation location) {
        this.putValue(key, BlockLocation.class, location);
    }

    public CommonTagCompound getCompoundOrEmpty(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Can not store elements under null keys");
        }
        Object handle = this.getRawData().get(key);
        if (CompoundTagHandle.T.isAssignableFrom(handle)) {
            CommonTagCompound tag = CommonTagCompound.create(CompoundTagHandle.createHandle(handle));
            tag.readOnly = this.readOnly;
            return tag;
        }
        return EMPTY;
    }

    public CommonTagCompound createCompound(Object key) {
        CommonTagCompound tag = this.getCompoundOrEmpty(key);
        if (tag == EMPTY) {
            this.assertWritable();
            tag = new CommonTagCompound();
            this.getRawData().put(key.toString(), tag.getRawHandle());
        }
        return tag;
    }

    public CommonTagList getListOrEmpty(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("Can not store elements under null keys");
        }
        Object handle = this.getRawData().get(key);
        if (ListTagHandle.T.isAssignableFrom(handle)) {
            CommonTagList list = CommonTagList.create(ListTagHandle.createHandle(handle));
            list.readOnly = this.readOnly;
            return list;
        }
        return CommonTagList.EMPTY;
    }

    public CommonTagList createList(Object key) {
        CommonTagList tag = this.getListOrEmpty(key);
        if (tag == CommonTagList.EMPTY) {
            this.assertWritable();
            tag = new CommonTagList();
            this.getRawData().put(key.toString(), tag.getRawHandle());
        }
        return tag;
    }

    public <T extends CommonTag> T get(Object key, Class<T> type) {
        return (T)((CommonTag)Conversion.convert(this.get(key), type, null));
    }

    @Override
    public CommonTag get(Object key) {
        if (key == null) {
            return null;
        }
        TagHandle handle = this.getBackingHandle().get(key.toString());
        return handle == null ? null : handle.toCommonTag();
    }

    @Override
    public CommonTag put(String key, CommonTag value) {
        return this.putGetRemove(PutGetRemoveOp.PUT, key, CommonTag.class, value, true);
    }

    @Override
    public void clear() {
        this.assertWritable();
        this.getRawData().clear();
    }

    @Override
    public int size() {
        return this.getBackingHandle().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getBackingHandle().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return key != null && this.getBackingHandle().containsKey(key.toString());
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof TagHandle) {
            value = ((TagHandle)value).getRaw();
        } else if (value instanceof CommonTag) {
            value = ((CommonTag)value).getRawHandle();
        }
        if (TagHandle.T.isAssignableFrom(value)) {
            return this.getHandleValues().contains(value);
        }
        for (Object base : this.getHandleValues()) {
            if (!TagHandle.getDataForHandle(base).equals(value)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends String, ? extends CommonTag> m) {
        for (Map.Entry<? extends String, ? extends CommonTag> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<String> keySet() {
        return this.getRawData().keySet();
    }

    @Override
    public Collection<CommonTag> values() {
        return this.getData().values();
    }

    @Override
    public Set<Map.Entry<String, CommonTag>> entrySet() {
        return this.getData().entrySet();
    }

    @Override
    public void writeToStream(OutputStream out) throws IOException {
        this.writeToStream(out, false);
    }

    public void writeToStream(OutputStream out, boolean compressed) throws IOException {
        if (compressed) {
            NbtIoHandle.compressed_writeTagCompound(this.getBackingHandle(), out);
        } else if (out instanceof DataOutput) {
            NbtIoHandle.uncompressed_writeTagCompound(this.getBackingHandle(), (DataOutput)((Object)out));
        } else {
            NbtIoHandle.uncompressed_writeTagCompound(this.getBackingHandle(), new DataOutputStream(out));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void writeToFile(File file, boolean compressed) throws IOException {
        TempFileOutputStream stream = new TempFileOutputStream(file);
        boolean successful = false;
        try {
            this.writeToStream(stream, compressed);
            successful = true;
        }
        finally {
            stream.close(successful);
        }
    }

    public static CommonTagCompound readFromStream(InputStream in) throws IOException {
        return CommonTagCompound.readFromStream(in, false);
    }

    public static CommonTagCompound readFromStream(InputStream in, boolean compressed) throws IOException {
        CompoundTagHandle handle = compressed ? NbtIoHandle.compressed_readTagCompound(in) : (in instanceof DataInput ? NbtIoHandle.uncompressed_readTagCompound((DataInput)((Object)in)) : NbtIoHandle.uncompressed_readTagCompound(new DataInputStream(in)));
        return handle == null ? null : new CommonTagCompound(handle);
    }

    public static CommonTagCompound fromBase64String(String internal) {
        CommonTagCompound commonTagCompound;
        if (internal == null) {
            return null;
        }
        byte[] compressedBytes = Base64.getDecoder().decode(internal);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedBytes);
        try {
            commonTagCompound = CommonTagCompound.readFromStream(byteStream, true);
        }
        catch (Throwable throwable) {
            try {
                try {
                    byteStream.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IllegalArgumentException ex) {
                return null;
            }
            catch (Exception ex) {
                return null;
            }
        }
        byteStream.close();
        return commonTagCompound;
    }

    public String toBase64String() {
        String string;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            this.writeToStream(byteStream, true);
            byteStream.flush();
            string = Base64.getEncoder().encodeToString(byteStream.toByteArray());
        }
        catch (Throwable throwable) {
            try {
                try {
                    byteStream.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException ex) {
                throw new IllegalStateException("Failed to write NBT data to byte stream", ex);
            }
        }
        byteStream.close();
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static CommonTagCompound readFromFile(File file, boolean compressed) throws IOException {
        try (FileInputStream stream = new FileInputStream(file);){
            CommonTagCompound commonTagCompound = CommonTagCompound.readFromStream(stream, compressed);
            return commonTagCompound;
        }
    }

    public static CommonTagCompound create(Object handle) {
        return LogicUtil.tryCast(CommonTag.create(handle), CommonTagCompound.class);
    }

    public static CommonTagCompound createReadOnly(Object handle) {
        return CommonTagCompound.makeReadOnly(CommonTagCompound.create(handle));
    }

    @Deprecated
    public static CommonTagCompound fromMojangson(String mojangson) {
        return CommonTagCompound.fromSNBT(mojangson).getResult();
    }

    public static CommonTag.SNBTResult<CommonTagCompound> fromSNBT(String snbtContent) {
        try {
            CompoundTagHandle result = NbtIoHandle.parseTagCompoundFromSNBT(snbtContent);
            return CommonTag.SNBTResult.success(result.toCommonTag());
        }
        catch (Throwable t) {
            return CommonTag.SNBTResult.error(NbtIoHandle.handleSNBTParseError(snbtContent, t));
        }
    }

    private static enum PutGetRemoveOp {
        PUT,
        GET,
        REMOVE;

    }
}

