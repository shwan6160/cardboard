/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.nbt;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.generated.net.minecraft.nbt.CompoundTagHandle;
import com.bergerkiller.generated.net.minecraft.nbt.ListTagHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Template.InstanceType(value="net.minecraft.nbt.Tag")
public abstract class TagHandle
extends Template.Handle {
    public static final TagClass T = Template.Class.create(TagClass.class, Common.TEMPLATE_RESOLVER);
    private static TypeInfoLookup lookup = null;

    public static TagHandle createHandle(Object handleInstance) {
        return (TagHandle)T.createHandle(handleInstance);
    }

    public abstract byte getTypeId();

    public abstract Object raw_clone();

    public CommonTag toCommonTag() {
        return new CommonTag(this);
    }

    public abstract TagHandle clone();

    public abstract Object getData();

    public final String toPrettyString() {
        StringBuilder str = new StringBuilder(100);
        this.toPrettyString(str, 0);
        return str.toString();
    }

    public void toPrettyString(StringBuilder str, int indent) {
        while (indent-- > 0) {
            str.append("  ");
        }
        Object data = this.getData();
        if (data == null) {
            str.append("UNKNOWN[").append(this.getTypeId()).append("]");
        } else {
            Class<?> unboxedType = BoxedType.getUnboxedType(data.getClass());
            if (unboxedType != null) {
                str.append(unboxedType.getSimpleName());
            } else {
                str.append(data.getClass().getSimpleName());
            }
            str.append(": ");
            if (data instanceof byte[]) {
                byte[] values = (byte[])data;
                str.append("[");
                for (int i = 0; i < values.length; ++i) {
                    if (i > 0) {
                        str.append(", ");
                    }
                    str.append(values[i]);
                }
                str.append("]");
            } else if (data instanceof int[]) {
                int[] values = (int[])data;
                str.append("[");
                for (int i = 0; i < values.length; ++i) {
                    if (i > 0) {
                        str.append(", ");
                    }
                    str.append(values[i]);
                }
                str.append("]");
            } else if (data instanceof long[]) {
                long[] values = (long[])data;
                str.append("[");
                for (int i = 0; i < values.length; ++i) {
                    if (i > 0) {
                        str.append(", ");
                    }
                    str.append(values[i]);
                }
                str.append("]");
            } else {
                str.append(data);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static TypeInfoLookup lookup() {
        TypeInfoLookup lookup = TagHandle.lookup;
        if (lookup != null) {
            return lookup;
        }
        Class<TagHandle> clazz = TagHandle.class;
        synchronized (TagHandle.class) {
            lookup = TagHandle.lookup;
            if (lookup != null) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return lookup;
            }
            TagHandle.lookup = lookup = new TypeInfoLookup();
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return lookup;
        }
    }

    private static TypeInfo findTypeInfo(Object data) {
        if (data == null) {
            throw new IllegalArgumentException("Can not find tag type information for null data");
        }
        TypeInfoLookup lookup = TagHandle.lookup();
        TypeInfo info = lookup.byType.get(data.getClass());
        if (info != null) {
            return info;
        }
        if (data instanceof CommonTag) {
            TypeInfo handle_info = TagHandle.findTypeInfo(((CommonTag)data).getRawHandle());
            return new TypeInfo(handle_info.dataType, handle_info.handleClass, tag -> ((CommonTag)data).getRawHandle(), tag -> handle_info.get_data.apply(((CommonTag)data).getRawHandle()));
        }
        return lookup.toStringFallback;
    }

    public static boolean isDataSupportedNatively(Object data) {
        TypeInfoLookup lookup = TagHandle.lookup();
        return lookup.byType.get(data) != null || data instanceof CommonTag;
    }

    public static Object getDataForHandle(Object handle) {
        return TagHandle.findTypeInfo((Object)handle).get_data.apply(handle);
    }

    public static Object createRawHandleForData(Object data) {
        return TagHandle.findTypeInfo((Object)data).constructor.apply(data);
    }

    public static TagHandle createHandleForData(Object data) {
        TypeInfo info = TagHandle.findTypeInfo(data);
        return info.handleClass.createHandle(info.constructor.apply(data));
    }

    public static Consumer<String> createPartialErrorLogger(Object nbtBase) {
        return s -> {
            String nbtToStr = nbtBase == null ? "[null]" : nbtBase.toString();
            Logging.LOGGER.severe("Failed to read (" + nbtToStr + "): " + s);
        };
    }

    public static final class TagClass
    extends Template.Class<TagHandle> {
        public final Template.StaticMethod<TagHandle> createHandle = new Template.StaticMethod();
        public final Template.Method<Byte> getTypeId = new Template.Method();
        public final Template.Method<Object> raw_clone = new Template.Method();
    }

    private static class TypeInfoLookup {
        public final ClassMap<TypeInfo> byType = new ClassMap();
        public final TypeInfo toStringFallback = new TypeInfo(String.class, StringTagHandle.T, data -> ((Template.StaticMethod)StringTagHandle.T.create.raw).invoke(Conversion.toString.convert(data, "")), Function.identity());

        public TypeInfoLookup() {
            this.registerTypeInfo(String.class, StringTagHandle.T, ((Template.StaticMethod)StringTagHandle.T.create.raw)::invoke, StringTagHandle.T.getData::invoke);
            this.registerTypeInfo(Byte.TYPE, ByteTagHandle.T, ((Template.StaticMethod)ByteTagHandle.T.create.raw)::invoke, ByteTagHandle.T.getByteData::invoke);
            this.registerTypeInfo(Short.TYPE, ShortTagHandle.T, ((Template.StaticMethod)ShortTagHandle.T.create.raw)::invoke, ShortTagHandle.T.getShortData::invoke);
            this.registerTypeInfo(Integer.TYPE, IntTagHandle.T, ((Template.StaticMethod)IntTagHandle.T.create.raw)::invoke, IntTagHandle.T.getIntegerData::invoke);
            this.registerTypeInfo(Long.TYPE, LongTagHandle.T, ((Template.StaticMethod)LongTagHandle.T.create.raw)::invoke, LongTagHandle.T.getLongData::invoke);
            this.registerTypeInfo(Float.TYPE, FloatTagHandle.T, ((Template.StaticMethod)FloatTagHandle.T.create.raw)::invoke, FloatTagHandle.T.getFloatData::invoke);
            this.registerTypeInfo(Double.TYPE, DoubleTagHandle.T, ((Template.StaticMethod)DoubleTagHandle.T.create.raw)::invoke, DoubleTagHandle.T.getDoubleData::invoke);
            this.registerTypeInfo(byte[].class, ByteArrayTagHandle.T, ((Template.StaticMethod)ByteArrayTagHandle.T.create.raw)::invoke, ByteArrayTagHandle.T.getData::invoke);
            this.registerTypeInfo(int[].class, IntArrayTagHandle.T, ((Template.StaticMethod)IntArrayTagHandle.T.create.raw)::invoke, IntArrayTagHandle.T.getData::invoke);
            if (LongArrayTagHandle.T.isAvailable()) {
                this.registerTypeInfo(long[].class, LongArrayTagHandle.T, ((Template.StaticMethod)LongArrayTagHandle.T.create.raw)::invoke, LongArrayTagHandle.T.getData::invoke);
            }
            this.registerTypeInfo(Collection.class, ListTagHandle.T, ((Template.StaticMethod)ListTagHandle.T.create.raw)::invoke, ((Template.Field)ListTagHandle.T.data.raw)::get);
            this.registerTypeInfo(Map.class, CompoundTagHandle.T, ((Template.StaticMethod)CompoundTagHandle.T.create.raw)::invoke, ((Template.Field)CompoundTagHandle.T.data.raw)::get);
        }

        private void registerTypeInfo(Class<?> dataType, Template.Class<? extends TagHandle> handleClass, Function<Object, Object> constructor, Function<Object, Object> get_data) {
            TypeInfo data_typeInfo = new TypeInfo(dataType, handleClass, constructor, Function.identity());
            this.byType.put(dataType, data_typeInfo);
            Class<?> boxedDataType = BoxedType.getBoxedType(dataType);
            if (boxedDataType != null) {
                this.byType.put(boxedDataType, data_typeInfo);
            }
            this.byType.put(handleClass.getType(), new TypeInfo(dataType, handleClass, Function.identity(), get_data));
            this.byType.put(handleClass.getHandleType(), new TypeInfo(dataType, handleClass, handle -> ((Template.Handle)handle).getRaw(), handle -> get_data.apply(((Template.Handle)handle).getRaw())));
            handleClass.createHandle(null, true);
        }
    }

    private static final class TypeInfo {
        public final Class<?> dataType;
        public final Template.Class<? extends TagHandle> handleClass;
        public final Function<Object, Object> constructor;
        public final Function<Object, Object> get_data;

        public TypeInfo(Class<?> dataType, Template.Class<? extends TagHandle> handleClass, Function<Object, Object> constructor, Function<Object, Object> get_data) {
            this.dataType = dataType;
            this.handleClass = handleClass;
            this.constructor = constructor;
            this.get_data = get_data;
        }
    }

    @Template.Optional
    @Template.InstanceType(value="net.minecraft.nbt.LongArrayTag")
    public static abstract class LongArrayTagHandle
    extends TagHandle {
        public static final LongArrayTagClass T = Template.Class.create(LongArrayTagClass.class, Common.TEMPLATE_RESOLVER);

        public static LongArrayTagHandle createHandle(Object handleInstance) {
            return (LongArrayTagHandle)T.createHandle(handleInstance);
        }

        public static LongArrayTagHandle create(long[] data) {
            return LongArrayTagHandle.T.create.invoke(data);
        }

        public abstract long[] getData();

        public static final class LongArrayTagClass
        extends Template.Class<LongArrayTagHandle> {
            public final Template.StaticMethod.Converted<LongArrayTagHandle> create = new Template.StaticMethod.Converted();
            public final Template.Method<long[]> getData = new Template.Method();
        }
    }

    @Template.InstanceType(value="net.minecraft.nbt.IntArrayTag")
    public static abstract class IntArrayTagHandle
    extends TagHandle {
        public static final IntArrayTagClass T = Template.Class.create(IntArrayTagClass.class, Common.TEMPLATE_RESOLVER);

        public static IntArrayTagHandle createHandle(Object handleInstance) {
            return (IntArrayTagHandle)T.createHandle(handleInstance);
        }

        public static IntArrayTagHandle create(int[] data) {
            return IntArrayTagHandle.T.create.invoke(data);
        }

        public abstract int[] getData();

        public static final class IntArrayTagClass
        extends Template.Class<IntArrayTagHandle> {
            public final Template.StaticMethod.Converted<IntArrayTagHandle> create = new Template.StaticMethod.Converted();
            public final Template.Method<int[]> getData = new Template.Method();
        }
    }

    @Template.InstanceType(value="net.minecraft.nbt.ByteArrayTag")
    public static abstract class ByteArrayTagHandle
    extends TagHandle {
        public static final ByteArrayTagClass T = Template.Class.create(ByteArrayTagClass.class, Common.TEMPLATE_RESOLVER);

        public static ByteArrayTagHandle createHandle(Object handleInstance) {
            return (ByteArrayTagHandle)T.createHandle(handleInstance);
        }

        public static ByteArrayTagHandle create(byte[] data) {
            return ByteArrayTagHandle.T.create.invoke(data);
        }

        public abstract byte[] getData();

        public static final class ByteArrayTagClass
        extends Template.Class<ByteArrayTagHandle> {
            public final Template.StaticMethod.Converted<ByteArrayTagHandle> create = new Template.StaticMethod.Converted();
            public final Template.Method<byte[]> getData = new Template.Method();
        }
    }

    @Template.InstanceType(value="net.minecraft.nbt.DoubleTag")
    public static abstract class DoubleTagHandle
    extends TagHandle {
        public static final DoubleTagClass T = Template.Class.create(DoubleTagClass.class, Common.TEMPLATE_RESOLVER);

        public static DoubleTagHandle createHandle(Object handleInstance) {
            return (DoubleTagHandle)T.createHandle(handleInstance);
        }

        public static DoubleTagHandle create(double data) {
            return DoubleTagHandle.T.create.invoke(data);
        }

        public abstract double getDoubleData();

        @Override
        public DoubleTagHandle clone() {
            return CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : DoubleTagHandle.createHandle(this.raw_clone());
        }

        @Override
        public Double getData() {
            return this.getDoubleData();
        }

        public static final class DoubleTagClass
        extends Template.Class<DoubleTagHandle> {
            public final Template.StaticMethod.Converted<DoubleTagHandle> create = new Template.StaticMethod.Converted();
            public final Template.Method<Double> getDoubleData = new Template.Method();
        }
    }

    @Template.InstanceType(value="net.minecraft.nbt.FloatTag")
    public static abstract class FloatTagHandle
    extends TagHandle {
        public static final FloatTagClass T = Template.Class.create(FloatTagClass.class, Common.TEMPLATE_RESOLVER);

        public static FloatTagHandle createHandle(Object handleInstance) {
            return (FloatTagHandle)T.createHandle(handleInstance);
        }

        public static FloatTagHandle create(float data) {
            return FloatTagHandle.T.create.invoke(Float.valueOf(data));
        }

        public abstract float getFloatData();

        @Override
        public FloatTagHandle clone() {
            return CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : FloatTagHandle.createHandle(this.raw_clone());
        }

        @Override
        public Float getData() {
            return Float.valueOf(this.getFloatData());
        }

        public static final class FloatTagClass
        extends Template.Class<FloatTagHandle> {
            public final Template.StaticMethod.Converted<FloatTagHandle> create = new Template.StaticMethod.Converted();
            public final Template.Method<Float> getFloatData = new Template.Method();
        }
    }

    @Template.InstanceType(value="net.minecraft.nbt.LongTag")
    public static abstract class LongTagHandle
    extends TagHandle {
        public static final LongTagClass T = Template.Class.create(LongTagClass.class, Common.TEMPLATE_RESOLVER);

        public static LongTagHandle createHandle(Object handleInstance) {
            return (LongTagHandle)T.createHandle(handleInstance);
        }

        public static LongTagHandle create(long data) {
            return LongTagHandle.T.create.invoke(data);
        }

        public abstract long getLongData();

        @Override
        public LongTagHandle clone() {
            return CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : LongTagHandle.createHandle(this.raw_clone());
        }

        @Override
        public Long getData() {
            return this.getLongData();
        }

        public static final class LongTagClass
        extends Template.Class<LongTagHandle> {
            public final Template.StaticMethod.Converted<LongTagHandle> create = new Template.StaticMethod.Converted();
            public final Template.Method<Long> getLongData = new Template.Method();
        }
    }

    @Template.InstanceType(value="net.minecraft.nbt.IntTag")
    public static abstract class IntTagHandle
    extends TagHandle {
        public static final IntTagClass T = Template.Class.create(IntTagClass.class, Common.TEMPLATE_RESOLVER);

        public static IntTagHandle createHandle(Object handleInstance) {
            return (IntTagHandle)T.createHandle(handleInstance);
        }

        public static IntTagHandle create(int data) {
            return IntTagHandle.T.create.invoke(data);
        }

        public abstract int getIntegerData();

        @Override
        public IntTagHandle clone() {
            return CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : IntTagHandle.createHandle(this.raw_clone());
        }

        @Override
        public Integer getData() {
            return this.getIntegerData();
        }

        public static final class IntTagClass
        extends Template.Class<IntTagHandle> {
            public final Template.StaticMethod.Converted<IntTagHandle> create = new Template.StaticMethod.Converted();
            public final Template.Method<Integer> getIntegerData = new Template.Method();
        }
    }

    @Template.InstanceType(value="net.minecraft.nbt.ShortTag")
    public static abstract class ShortTagHandle
    extends TagHandle {
        public static final ShortTagClass T = Template.Class.create(ShortTagClass.class, Common.TEMPLATE_RESOLVER);

        public static ShortTagHandle createHandle(Object handleInstance) {
            return (ShortTagHandle)T.createHandle(handleInstance);
        }

        public static ShortTagHandle create(short data) {
            return ShortTagHandle.T.create.invoke(data);
        }

        public abstract short getShortData();

        public static Object createRaw(Object data) {
            return ((Template.StaticMethod)ShortTagHandle.T.create.raw).invoke(data);
        }

        @Override
        public ShortTagHandle clone() {
            return CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : ShortTagHandle.createHandle(this.raw_clone());
        }

        @Override
        public Short getData() {
            return this.getShortData();
        }

        public static final class ShortTagClass
        extends Template.Class<ShortTagHandle> {
            public final Template.StaticMethod.Converted<ShortTagHandle> create = new Template.StaticMethod.Converted();
            public final Template.Method<Short> getShortData = new Template.Method();
        }
    }

    @Template.InstanceType(value="net.minecraft.nbt.ByteTag")
    public static abstract class ByteTagHandle
    extends TagHandle {
        public static final ByteTagClass T = Template.Class.create(ByteTagClass.class, Common.TEMPLATE_RESOLVER);

        public static ByteTagHandle createHandle(Object handleInstance) {
            return (ByteTagHandle)T.createHandle(handleInstance);
        }

        public static ByteTagHandle create(byte data) {
            return ByteTagHandle.T.create.invoke(data);
        }

        public abstract byte getByteData();

        @Override
        public ByteTagHandle clone() {
            return CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : ByteTagHandle.createHandle(this.raw_clone());
        }

        @Override
        public Byte getData() {
            return this.getByteData();
        }

        public static final class ByteTagClass
        extends Template.Class<ByteTagHandle> {
            public final Template.StaticMethod.Converted<ByteTagHandle> create = new Template.StaticMethod.Converted();
            public final Template.Method<Byte> getByteData = new Template.Method();
        }
    }

    @Template.InstanceType(value="net.minecraft.nbt.StringTag")
    public static abstract class StringTagHandle
    extends TagHandle {
        public static final StringTagClass T = Template.Class.create(StringTagClass.class, Common.TEMPLATE_RESOLVER);

        public static StringTagHandle createHandle(Object handleInstance) {
            return (StringTagHandle)T.createHandle(handleInstance);
        }

        public static StringTagHandle create(String data) {
            return StringTagHandle.T.create.invoke(data);
        }

        @Override
        public abstract String getData();

        @Override
        public StringTagHandle clone() {
            return CommonCapabilities.IMMUTABLE_NBT_PRIMITIVES ? this : StringTagHandle.createHandle(this.raw_clone());
        }

        public static final class StringTagClass
        extends Template.Class<StringTagHandle> {
            public final Template.StaticMethod.Converted<StringTagHandle> create = new Template.StaticMethod.Converted();
            public final Template.Method<String> getData = new Template.Method();
        }
    }
}

