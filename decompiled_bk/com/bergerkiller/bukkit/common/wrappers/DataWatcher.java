/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.ExtendedEntity;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.conversion.type.EntityPoseConversion;
import com.bergerkiller.bukkit.common.conversion.type.JOMLConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonDisabledEntity;
import com.bergerkiller.bukkit.common.internal.proxy.DataWatcherObject;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BasicWrapper;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BoatWoodType;
import com.bergerkiller.bukkit.common.wrappers.Brightness;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcherSerializers;
import com.bergerkiller.bukkit.common.wrappers.EntityPose;
import com.bergerkiller.bukkit.common.wrappers.ItemDisplayMode;
import com.bergerkiller.generated.net.minecraft.core.RotationsHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.EntityDataAccessorHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.EntityDataSerializersHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.SynchedEntityDataHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.conversion.util.ConvertingList;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import com.bergerkiller.mountiplex.reflection.util.BoxedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalInt;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class DataWatcher
extends BasicWrapper<SynchedEntityDataHandle>
implements Cloneable {
    public DataWatcher(Entity entityOwner) {
        this(SynchedEntityDataHandle.createNew(entityOwner));
    }

    public DataWatcher() {
        this(SynchedEntityDataHandle.createNew(CommonDisabledEntity.INSTANCE));
    }

    public static DataWatcher createForHandle(Object nmsDataWatcherHandle) {
        return new DataWatcher(SynchedEntityDataHandle.createHandle(nmsDataWatcherHandle));
    }

    private DataWatcher(SynchedEntityDataHandle handle) {
        this.setHandle(handle);
    }

    public <V> void set(Key<V> key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (!(key instanceof Key.Disabled)) {
            ((SynchedEntityDataHandle)this.handle).setRaw(key, key.getType().getConverter().convertReverse(value), false);
        }
    }

    public <V> void forceSet(Key<V> key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (!(key instanceof Key.Disabled)) {
            ((SynchedEntityDataHandle)this.handle).setRaw(key, key.getType().getConverter().convertReverse(value), true);
        }
    }

    public void setByte(Key<Byte> key, int value) {
        this.set(key, (byte)(value & 0xFF));
    }

    public boolean getFlag(Key<Byte> key, int flag) {
        return (this.tryGetByte(key, 0) & flag) != 0;
    }

    public void setFlag(Key<Byte> key, int flag, boolean set) {
        int old_flags;
        int new_flags = old_flags = this.tryGetByte(key, 0);
        new_flags = set ? (new_flags |= flag) : (new_flags &= ~flag);
        if (old_flags != new_flags) {
            this.set(key, (byte)(new_flags & 0xFF));
        }
    }

    public <V> V get(Key<V> key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (key instanceof Key.Disabled) {
            return (V)((Key.Disabled)key).getDefaultValue();
        }
        Object rawItem = this.getItemRawHandle(key);
        if (rawItem == null) {
            throw new IllegalArgumentException("This key is not watched in this DataWatcher");
        }
        Object rawValue = SynchedEntityDataHandle.DataItemHandle.T.getValue.invoke(rawItem);
        return (V)key.getType().getConverter().convert(rawValue);
    }

    public int getByte(Key<Byte> key) {
        Byte result = this.get(key);
        return result == null ? -1 : result.intValue();
    }

    public <V> V tryGet(Key<V> key, V defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (key instanceof Key.Disabled) {
            return (V)((Key.Disabled)key).getDefaultValue();
        }
        Object rawItem = this.getItemRawHandle(key);
        if (rawItem == null) {
            return defaultValue;
        }
        Object rawValue = SynchedEntityDataHandle.DataItemHandle.T.getValue.invoke(rawItem);
        return (V)key.getType().getConverter().convert(rawValue);
    }

    public int tryGetByte(Key<Byte> key, int defaultValue) {
        Byte result = (byte)this.tryGet(key, (byte)(defaultValue & 0xFF));
        return result == null ? -1 : (int)result.byteValue();
    }

    public <V> Item<V> getItem(Key<V> key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        Object rawItem = this.getItemRawHandle(key);
        if (rawItem == null) {
            return null;
        }
        return new Item<V>(key, SynchedEntityDataHandle.DataItemHandle.createHandle(rawItem));
    }

    public <T> void setClientDefault(Key<T> key, T defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (key instanceof Key.Disabled) {
            return;
        }
        Object defaultValueConv = key.getType().getConverter().convertReverse(defaultValue);
        ((SynchedEntityDataHandle)this.handle).setRawDefault(key, defaultValueConv);
    }

    public void setClientByteDefault(Key<Byte> key, int defaultValue) {
        this.setClientDefault(key, (byte)(defaultValue & 0xFF));
    }

    @Deprecated
    public <T> void watch(Key<T> key, T defaultValue) {
        this.set(key, defaultValue);
    }

    @Deprecated
    public void watch(Item<?> item) {
        this.set(item.getKey(), item.getValue());
    }

    public boolean isWatched(Key<?> key) {
        return key != null && ((SynchedEntityDataHandle)this.handle).read(key) != null;
    }

    public List<Item<?>> getWatchedItems() {
        List itemHandles = (List)((Template.Method)SynchedEntityDataHandle.T.getCopyOfAllItems.raw).invoke(((SynchedEntityDataHandle)this.handle).getRaw());
        if (itemHandles == null) {
            itemHandles = Collections.emptyList();
        }
        return new ConvertingList(itemHandles, DuplexConversion.dataWatcherItem);
    }

    @Deprecated
    public List<Item<?>> getWatchedItems(boolean unwatch) {
        if (unwatch) {
            this.packChanges();
        }
        return this.getWatchedItems();
    }

    public List<PackedItem<?>> packAll() {
        List itemHandles = (List)((Template.Method)SynchedEntityDataHandle.T.packAll.raw).invoke(((SynchedEntityDataHandle)this.handle).getRaw());
        if (itemHandles == null) {
            itemHandles = Collections.emptyList();
        }
        return new ConvertingList(itemHandles, DuplexConversion.dataWatcherPackedItem);
    }

    public List<PackedItem<?>> packChanges() {
        List itemHandles = (List)((Template.Method)SynchedEntityDataHandle.T.packChanges.raw).invoke(((SynchedEntityDataHandle)this.handle).getRaw());
        if (itemHandles == null) {
            itemHandles = Collections.emptyList();
        }
        return new ConvertingList(itemHandles, DuplexConversion.dataWatcherPackedItem);
    }

    public List<PackedItem<?>> packNonDefaults() {
        List itemHandles = (List)((Template.Method)SynchedEntityDataHandle.T.packNonDefaults.raw).invoke(((SynchedEntityDataHandle)this.handle).getRaw());
        if (itemHandles == null) {
            itemHandles = Collections.emptyList();
        }
        return new ConvertingList(itemHandles, DuplexConversion.dataWatcherPackedItem);
    }

    public boolean isChanged() {
        return ((SynchedEntityDataHandle)this.handle).isChanged();
    }

    public boolean isEmpty() {
        return ((SynchedEntityDataHandle)this.handle).isEmpty();
    }

    @Override
    public String toString() {
        String str = "DataWatcher Items[";
        boolean first = true;
        for (Item<?> item : this.getWatchedItems()) {
            if (first) {
                first = false;
            } else {
                str = str + ", ";
            }
            str = str + item.toString();
        }
        str = str + "]";
        return str;
    }

    public DataWatcher clone() {
        return new DataWatcher(((SynchedEntityDataHandle)this.handle).cloneWithOwner(CommonDisabledEntity.INSTANCE));
    }

    private Object getItemRawHandle(Key<?> key) {
        if (CommonCapabilities.DATAWATCHER_OBJECTS) {
            return ((Template.Method)SynchedEntityDataHandle.T.read.raw).invoke(((SynchedEntityDataHandle)this.handle).getRaw(), key.getRawHandle());
        }
        return ((Template.Method)SynchedEntityDataHandle.T.read.raw).invoke(((SynchedEntityDataHandle)this.handle).getRaw(), key.getId());
    }

    public static class Key<V>
    extends BasicWrapper<EntityDataAccessorHandle> {
        private final Type<V> _serializer;

        protected Key(Type<V> serializer) {
            this._serializer = serializer;
        }

        public Key(Object handle) {
            this(handle, null);
        }

        public Key(Object handle, Type<V> serializer) {
            this.setHandle(EntityDataAccessorHandle.createHandle(handle));
            Object token = ((EntityDataAccessorHandle)this.handle).getSerializer();
            DataWatcherSerializers.InternalType internalType = DataWatcherSerializers.getInternalTypeFromToken(token);
            if (internalType != null) {
                if (serializer == null) {
                    serializer = Type.getForType((Class)LogicUtil.unsafeCast(internalType.type));
                }
                this._serializer = ((Type)serializer).setInternalOptional(internalType.optional);
            } else {
                this._serializer = serializer != null ? serializer : new Type(token, DuplexConverter.createNull(TypeDeclaration.OBJECT));
            }
        }

        public Type<V> getType() {
            return this._serializer;
        }

        public Class<?> getInternalType() {
            return this._serializer.getInternalType();
        }

        public int getSerializerId() {
            Object rawSerializer = ((EntityDataAccessorHandle)this.handle).getSerializer();
            return EntityDataSerializersHandle.T.getSerializerId.invoke(rawSerializer);
        }

        public int getId() {
            return EntityDataAccessorHandle.T.getId.invoke(((EntityDataAccessorHandle)this.handle).getRaw());
        }

        @Override
        public int hashCode() {
            return this.getId();
        }

        @Override
        public String toString() {
            return "{id=" + this.getId() + ", type=" + this.getType().getInternalType() + "}";
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Key)) {
                return false;
            }
            Key other = (Key)o;
            if (this.getId() != other.getId()) {
                return false;
            }
            return this.getInternalType() == other.getInternalType();
        }

        public static class Type<T> {
            private static final HashMap<Class<?>, Type<?>> byTypeMapping = new HashMap();
            private final Object _token;
            private final DuplexConverter<Object, T> _converter;
            private Type<T> _optional_opposite;
            private static final Type<Object> MISSING_TYPE = new Type<Object>(){
                private final Key<Object> disabled_key = new Disabled<Object>(this);

                @Override
                public Key<Object> createKey(Template.StaticField.Converted<? extends Key<?>> tokenField, int alternativeId) {
                    return this.disabled_key;
                }

                @Override
                public <C> Type<C> translate(DuplexConverter<Object, C> converter) {
                    return (Type)LogicUtil.unsafeCast(this);
                }
            };
            public static final Type<Boolean> BOOLEAN = Type.getForType(Boolean.class);
            public static final Type<Byte> BYTE = Type.getForType(Byte.class);
            public static final Type<Integer> INTEGER = Type.getForType(Integer.class);
            public static final Type<Float> FLOAT = Type.getForType(Float.class);
            public static final Type<String> STRING = Type.getForType(String.class);
            public static final Type<Vector> ROTATION_VECTOR = new Type<Vector>(RotationsHandle.T.getType(), Vector.class);
            public static final Type<Vector> JOML_VECTOR3F;
            public static final Type<Quaternion> JOML_QUATERNIONF;
            public static final Type<EntityPose> ENTITY_POSE;
            public static final Type<IntVector3> BLOCK_POSITION;
            public static final Type<ChatText> CHAT_TEXT;
            public static final Type<ItemStack> ITEMSTACK;
            public static final Type<BlockFace> DIRECTION;
            public static final Type<OptionalInt> ENTITY_ID;
            public static final Type<BoatWoodType> BOAT_WOOD_TYPE;
            public static final Type<Integer> SLIME_SIZE_TYPE;
            public static final Type<BlockData> BLOCK_DATA;
            public static final Type<ItemDisplayMode> ITEM_DISPLAY_MODE;
            public static final Type<Brightness> DISPLAY_BRIGHTNESS;

            private Type() {
                this._token = null;
                this._converter = new DuplexConverter<Object, T>(Object.class, Object.class){

                    @Override
                    public T convertInput(Object value) {
                        return null;
                    }

                    @Override
                    public Object convertOutput(Object value) {
                        return null;
                    }
                };
            }

            private Type(Object token, DuplexConverter<Object, T> converter) {
                if (!CommonCapabilities.DATAWATCHER_OBJECTS && !(token instanceof Integer)) {
                    throw new IllegalArgumentException("Legacy type serializer tokens must be Integers!");
                }
                this._token = token;
                this._converter = converter;
            }

            private Type(Class<?> internalType, Class<T> externalType) {
                DataWatcherSerializers.ConvertedToken<T> convToken = DataWatcherSerializers.getConvertedSerializerToken(internalType, externalType);
                this._token = convToken.token;
                this._converter = convToken.converter;
            }

            public <C> Type<C> translate(Class<C> exposedType) {
                return this.translate(TypeDeclaration.fromClass(exposedType));
            }

            public Type<?> translate(TypeDeclaration exposedType) {
                return this.translate(Conversion.findDuplex(this._converter.output, exposedType));
            }

            public <C> Type<C> translate(DuplexConverter<T, C> converter) {
                final DuplexConverter<Object, T> ca = this._converter;
                final DuplexConverter<T, C> cb = converter;
                return new Type<T>(this._token, new DuplexConverter<Object, C>(this, ca.input, cb.output){
                    final /* synthetic */ Type this$0;
                    {
                        this.this$0 = this$0;
                        super(arg0, arg1);
                    }

                    @Override
                    public C convertInput(Object value) {
                        Object ca_output = ca.convertInput(value);
                        return ca_output == null ? null : (Object)cb.convertInput(ca_output);
                    }

                    @Override
                    public Object convertOutput(C value) {
                        Object cb_input = cb.convertOutput(value);
                        return cb_input == null ? null : ca.convertOutput(cb_input);
                    }
                });
            }

            private Type<T> setInternalOptional(boolean optional) {
                boolean selfIsOptional = this._converter instanceof DataWatcherSerializers.OptionalDuplexConverter;
                if (selfIsOptional == optional) {
                    return this;
                }
                if (this._optional_opposite == null) {
                    this._optional_opposite = selfIsOptional ? new Type(this._token, ((DataWatcherSerializers.OptionalDuplexConverter)this._converter).getBase()) : new Type<T>(this._token, new DataWatcherSerializers.OptionalDuplexConverter<T>(this._converter));
                }
                return this._optional_opposite;
            }

            public Key<T> createKey(Template.StaticField.Converted<? extends Key<?>> tokenField, int alternativeId) {
                if (CommonCapabilities.DATAWATCHER_OBJECTS) {
                    if (!tokenField.isAvailable()) {
                        if (alternativeId != -1) {
                            Logging.LOGGER_REGISTRY.warning("DataWatcher key not found: " + tokenField.getElementName());
                        }
                        return new Disabled(this);
                    }
                    return new Key(((Template.StaticField)tokenField.raw).get(), this);
                }
                if (alternativeId != -1) {
                    DataWatcherObject handle = new DataWatcherObject(alternativeId, this._token);
                    return new Key(handle, this);
                }
                return new Disabled(this);
            }

            public Object getToken() {
                return this._token;
            }

            public DuplexConverter<Object, T> getConverter() {
                return this._converter;
            }

            public Class<?> getInternalType() {
                return this._converter.input.type;
            }

            public Class<?> getExternalType() {
                return this._converter.output.type;
            }

            public String toString() {
                return "Type{internal=" + this._converter.input.toString(true) + ", external=" + this._converter.output + "}";
            }

            public static <V> Type<V> getForType(Class<V> externalType) {
                Type<Object> result = byTypeMapping.get(externalType);
                if (result == null) {
                    Class<?> internalType = DataWatcherSerializers.getInternalType(externalType);
                    if (internalType == null) {
                        throw new IllegalArgumentException("Object of type " + externalType.getName() + " can not be stored in a DataWatcher");
                    }
                    result = new Type<V>(internalType, externalType);
                    byTypeMapping.put(externalType, result);
                }
                return result;
            }

            public static <T> Type<T> missing() {
                return (Type)LogicUtil.unsafeCast(MISSING_TYPE);
            }

            static {
                Type<Object> type = CommonBootstrap.evaluateMCVersion(">=", "1.21.11") ? new Type<Vector>(JOMLConversion.JOML_VECTOR3F_CONSTANT_TYPE, Vector.class) : (JOML_VECTOR3F = CommonBootstrap.evaluateMCVersion(">=", "1.19.4") ? new Type<Vector>(JOMLConversion.JOML_VECTOR3F_TYPE, Vector.class) : Type.missing());
                JOML_QUATERNIONF = CommonBootstrap.evaluateMCVersion(">=", "1.21.11") ? new Type<Quaternion>(JOMLConversion.JOML_QUATERNIONF_CONSTANT_TYPE, Quaternion.class) : (CommonBootstrap.evaluateMCVersion(">=", "1.19.4") ? new Type<Quaternion>(JOMLConversion.JOML_QUATERNIONF_TYPE, Quaternion.class) : Type.missing());
                ENTITY_POSE = CommonBootstrap.evaluateMCVersion(">=", "1.14") ? new Type<EntityPose>(EntityPoseConversion.NMS_ENTITY_POSE_TYPE, EntityPose.class) : Type.missing();
                BLOCK_POSITION = Type.getForType(IntVector3.class);
                CHAT_TEXT = Type.getForType(ChatText.class);
                ITEMSTACK = Type.getForType(ItemStack.class);
                DIRECTION = Type.getForType(BlockFace.class);
                ENTITY_ID = new Type<OptionalInt>(Type.INTEGER._token, DataWatcherSerializers.ENTITY_ID_TYPE_CONVERTER);
                BOAT_WOOD_TYPE = new Type<BoatWoodType>(Type.INTEGER._token, DataWatcherSerializers.BOAT_WOOD_TYPE_CONVERTER);
                SLIME_SIZE_TYPE = CommonCapabilities.DATAWATCHER_OBJECTS ? INTEGER : new Type<Integer>(Type.BYTE._token, DataWatcherSerializers.SLIME_SIZE_CONVERTER);
                BLOCK_DATA = CommonCapabilities.HAS_BLOCKDATA_METADATA ? Type.getForType(BlockData.class) : Type.missing();
                ITEM_DISPLAY_MODE = BYTE.translate(ItemDisplayMode.class);
                DISPLAY_BRIGHTNESS = INTEGER.translate(Brightness.class);
            }
        }

        public static final class Disabled<T>
        extends Key<T> {
            private final T _defaultValue;

            public Disabled(Type<T> keyType) {
                super(keyType);
                Object internalValue = null;
                Class<?> type = LogicUtil.getUnboxedType(this.getType().getInternalType());
                if (type != null) {
                    internalValue = BoxedType.getDefaultValue(type);
                }
                this._defaultValue = this.getType().getConverter().convert(internalValue);
            }

            public T getDefaultValue() {
                return this._defaultValue;
            }
        }
    }

    public static class Item<V>
    extends BasicWrapper<SynchedEntityDataHandle.DataItemHandle> {
        private final Key<V> key;

        protected Item(Key<V> key, SynchedEntityDataHandle.DataItemHandle handle) {
            this.key = key;
            this.setHandle(handle);
        }

        public Item(SynchedEntityDataHandle.DataItemHandle handle) {
            this.key = null;
            this.setHandle(handle);
        }

        public <W> Item<W> translate(Key<W> key) {
            if (!key.equals(this.getKey())) {
                return null;
            }
            return new Item<W>(key, (SynchedEntityDataHandle.DataItemHandle)this.handle);
        }

        public Key<V> getKey() {
            if (this.key != null) {
                return this.key;
            }
            if (CommonCapabilities.DATAWATCHER_OBJECTS) {
                return SynchedEntityDataHandle.DataItemHandle.T.key.get(((SynchedEntityDataHandle.DataItemHandle)this.handle).getRaw());
            }
            int typeId = SynchedEntityDataHandle.DataItemHandle.T.typeId.getInteger(((SynchedEntityDataHandle.DataItemHandle)this.handle).getRaw());
            int keyId = SynchedEntityDataHandle.DataItemHandle.T.keyId.getInteger(((SynchedEntityDataHandle.DataItemHandle)this.handle).getRaw());
            Integer token = typeId;
            DataWatcherObject handle = new DataWatcherObject(keyId, token);
            return new Key(handle);
        }

        public boolean isChanged() {
            return ((SynchedEntityDataHandle.DataItemHandle)this.handle).isChanged();
        }

        public V getValue() {
            return (V)this.getKey().getType().getConverter().convert(((SynchedEntityDataHandle.DataItemHandle)this.handle).getValue());
        }

        public void setChanged(boolean changed) {
            ((SynchedEntityDataHandle.DataItemHandle)this.handle).setChanged(changed);
        }

        public void setValue(V value, boolean changed) {
            if (this.key != null) {
                ((SynchedEntityDataHandle.DataItemHandle)this.handle).setValue(this.key.getType().getConverter().convertReverse(value));
            } else {
                ((SynchedEntityDataHandle.DataItemHandle)this.handle).setValue(value);
            }
            ((SynchedEntityDataHandle.DataItemHandle)this.handle).setChanged(true);
        }

        @Override
        public String toString() {
            Key<V> key = this.getKey();
            return "{id=" + key.getId() + ",type=" + key.getType().getInternalType().getSimpleName() + ",changed=" + this.isChanged() + ",value=" + this.getValue() + "}";
        }

        public static Object getRawValue(Item<?> item) {
            return ((SynchedEntityDataHandle.DataItemHandle)item.handle).getValue();
        }

        public PackedItem<V> pack() {
            return new PackedItem(((SynchedEntityDataHandle.DataItemHandle)this.handle).pack(), this.key);
        }
    }

    private static class ConvertingEntityItem<A, B>
    extends EntityItem<B> {
        private final EntityItem<A> item;
        private final DuplexConverter<A, B> pair;

        public ConvertingEntityItem(EntityItem<A> item, DuplexConverter<A, B> pair) {
            super(null, null);
            this.item = item;
            this.pair = pair;
        }

        @Override
        public B get() {
            return (B)this.pair.convert(this.item.get());
        }

        @Override
        public void set(B value) {
            this.item.set(this.pair.convertReverse(value));
        }
    }

    public static class EntityItem<V> {
        private final Key<V> key;
        private final SynchedEntityDataHandle datawatcher;

        public EntityItem(ExtendedEntity<?> owner, Key<V> key) {
            this.key = key;
            this.datawatcher = SynchedEntityDataHandle.createHandle(((Template.Field)EntityHandle.T.datawatcherField.raw).get(owner.getHandle()));
        }

        public V get() {
            return (V)this.datawatcher.get(this.key);
        }

        public void set(V value) {
            this.datawatcher.setRaw(this.key, this.key.getType().getConverter().convertReverse(value), false);
        }

        public <C> EntityItem<C> translate(DuplexConverter<V, C> converterPair) {
            return new ConvertingEntityItem<V, C>(this, converterPair);
        }
    }

    public static class PackedItem<V>
    extends BasicWrapper<SynchedEntityDataHandle.DataValueHandle> {
        private final Key<V> key;

        private PackedItem(SynchedEntityDataHandle.DataValueHandle handle, Key<V> key) {
            this.setHandle(handle);
            this.key = key;
        }

        public static <T> PackedItem<T> fromHandle(Object nmsPackedItemHandle) {
            return new PackedItem(SynchedEntityDataHandle.DataValueHandle.createHandle(nmsPackedItemHandle), null);
        }

        public V value() {
            if (this.key != null) {
                return (V)this.key.getType().getConverter().convert(((SynchedEntityDataHandle.DataValueHandle)this.handle).value());
            }
            return (V)((SynchedEntityDataHandle.DataValueHandle)this.handle).value();
        }

        public PackedItem<V> cloneWithValue(V value) {
            if (this.key != null) {
                return new PackedItem<V>(((SynchedEntityDataHandle.DataValueHandle)this.handle).cloneWithValue(this.key.getType().getConverter().convertReverse(value)), this.key);
            }
            return new PackedItem<V>(((SynchedEntityDataHandle.DataValueHandle)this.handle).cloneWithValue(value), null);
        }

        public <W> PackedItem<W> translate(Key<W> key) {
            return this.isForKey(key) ? new PackedItem<W>((SynchedEntityDataHandle.DataValueHandle)this.handle, key) : null;
        }

        public boolean isForKey(Key<?> key) {
            return ((SynchedEntityDataHandle.DataValueHandle)this.handle).isForKey(key);
        }
    }

    public static final class PrototypeBuilder {
        private final DataWatcher dataWatcher;
        private boolean created = false;

        private PrototypeBuilder(DataWatcher dataWatcher) {
            this.dataWatcher = dataWatcher;
        }

        public <T> PrototypeBuilder setClientDefault(Key<T> key, T defaultValue) {
            this.dataWatcher.setClientDefault(key, defaultValue);
            return this;
        }

        public PrototypeBuilder setClientByteDefault(Key<Byte> key, int defaultValue) {
            this.dataWatcher.setClientByteDefault(key, defaultValue);
            return this;
        }

        public <T> PrototypeBuilder set(Key<T> key, T value) {
            this.dataWatcher.set(key, value);
            return this;
        }

        public PrototypeBuilder setByte(Key<Byte> key, int value) {
            this.dataWatcher.setByte(key, value);
            return this;
        }

        public PrototypeBuilder setFlag(Key<Byte> key, int flag, boolean set) {
            this.dataWatcher.setFlag(key, flag, set);
            return this;
        }

        public Prototype create() {
            if (this.created) {
                throw new IllegalStateException("DataWatcher Prototype Builder was already used to create a new ProtoType!");
            }
            this.created = true;
            this.dataWatcher.packChanges();
            return this.dataWatcher::clone;
        }
    }

    @FunctionalInterface
    public static interface Prototype {
        public DataWatcher create();

        default public PrototypeBuilder modify() {
            return new PrototypeBuilder(this.create());
        }

        public static PrototypeBuilder build() {
            return new PrototypeBuilder(new DataWatcher());
        }
    }
}

