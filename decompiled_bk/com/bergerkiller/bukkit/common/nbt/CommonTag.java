/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.nbt;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.generated.net.minecraft.nbt.NbtIoHandle;
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.conversion.util.ConvertingMap;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CommonTag
extends BasicWrapper<TagHandle>
implements Cloneable {
    boolean readOnly = false;

    public CommonTag(TagHandle handle) {
        this.setHandle(handle);
    }

    public CommonTag(Object data) {
        if (data instanceof TagHandle) {
            this.setHandle((TagHandle)data);
        } else {
            this.setHandle(TagHandle.createHandleForData(data));
        }
    }

    void assertWritable() {
        if (this.readOnly) {
            throw new UnsupportedOperationException("This NBT Tag is read-only");
        }
    }

    protected Object getRawData() {
        return TagHandle.getDataForHandle(this.getRawHandle());
    }

    public Object getData() {
        return CommonTag.wrapRawData(this.getRawData(), this.readOnly);
    }

    public <T> T getData(T def) {
        return Conversion.convert(this.getData(), def);
    }

    public <T> T getData(Class<T> type) {
        return Conversion.convert(this.getData(), type, null);
    }

    public <T> T getData(Class<T> type, T def) {
        return Conversion.convert(this.getData(), type, def);
    }

    @Override
    public String toString() {
        return ((TagHandle)this.handle).toPrettyString();
    }

    public CommonTag clone() {
        return new CommonTag(((TagHandle)this.handle).clone());
    }

    public void writeToStream(OutputStream out) throws IOException {
        if (out instanceof DataOutput) {
            NbtIoHandle.uncompressed_writeTag((TagHandle)this.handle, (DataOutput)((Object)out));
        } else {
            NbtIoHandle.uncompressed_writeTag((TagHandle)this.handle, new DataOutputStream(out));
        }
    }

    public static CommonTag readFromStream(InputStream in) throws IOException {
        if (in instanceof DataInput) {
            return CommonTag.create(NbtIoHandle.uncompressed_readTag((DataInput)((Object)in)));
        }
        return CommonTag.create(NbtIoHandle.uncompressed_readTag(new DataInputStream(in)));
    }

    public static CommonTag createForData(Object data) {
        return TagHandle.createHandleForData(data).toCommonTag();
    }

    public static CommonTag create(Object handle) {
        if (handle == null) {
            return null;
        }
        return TagHandle.createHandleForData(handle).toCommonTag();
    }

    protected static Object wrapGetDataForHandle(Object nmsNBTHandle, boolean readOnly) {
        return CommonTag.wrapRawData(TagHandle.getDataForHandle(nmsNBTHandle), readOnly);
    }

    protected static Object wrapRawData(Object value, boolean readOnly) {
        if (value instanceof Map) {
            return new ConvertingMap<String, CommonTag>((Map)value, DuplexConversion.string_string, CommonTag.nbtBaseToCommonTag(readOnly));
        }
        if (value instanceof List) {
            return new ConvertingList<CommonTag>((List)value, CommonTag.nbtBaseToCommonTag(readOnly));
        }
        return value;
    }

    static DuplexConverter<Object, CommonTag> nbtBaseToCommonTag(boolean readOnly) {
        return readOnly ? DuplexConversion.nbtBase_commonTag_readOnly : DuplexConversion.nbtBase_commonTag;
    }

    public static <T extends CommonTag> T makeReadOnly(T tag) {
        if (tag != null) {
            tag.readOnly = true;
        }
        return tag;
    }

    public static SNBTResult<? extends CommonTag> fromSNBT(String snbtContent) {
        try {
            TagHandle result = NbtIoHandle.parseTagFromSNBT(snbtContent);
            return SNBTResult.success(TagHandle.createHandleForData(result.getRaw()).toCommonTag());
        }
        catch (Throwable t) {
            t.printStackTrace();
            return SNBTResult.error(NbtIoHandle.handleSNBTParseError(snbtContent, t));
        }
    }

    public static class SNBTResult<T extends CommonTag> {
        private final T result;
        private final String errorMessage;

        public static <T extends CommonTag> SNBTResult<T> error(String errorMessage) {
            return new SNBTResult<Object>(null, errorMessage);
        }

        public static <T extends CommonTag> SNBTResult<T> success(T tag) {
            return new SNBTResult<T>(tag, null);
        }

        private SNBTResult(T result, String errorMessage) {
            this.result = result;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return this.result != null;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public T getResult() {
            return this.result;
        }

        public <E extends RuntimeException> T getResultOrThrow(Function<String, E> errorCreator) {
            if (this.result != null) {
                return this.result;
            }
            throw (RuntimeException)errorCreator.apply(this.errorMessage);
        }

        public String toString() {
            return this.isSuccess() ? "Success<" + this.getResult() + ">" : "Failure<" + this.getErrorMessage() + ">";
        }
    }
}

