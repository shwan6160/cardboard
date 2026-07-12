/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BoatWoodType;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.core.DirectionHandle;
import com.bergerkiller.generated.net.minecraft.core.RotationsHandle;
import com.bergerkiller.generated.net.minecraft.network.chat.ComponentHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.BlockStateHandle;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.OptionalInt;
import java.util.logging.Level;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

class DataWatcherSerializers {
    public static final DuplexConverter<Object, OptionalInt> ENTITY_ID_TYPE_CONVERTER = new EntityIdTypeConverter();
    public static final DuplexConverter<Object, BoatWoodType> BOAT_WOOD_TYPE_CONVERTER = new BoatWoodTypeIdConverter();
    public static final DuplexConverter<Object, Integer> SLIME_SIZE_CONVERTER = new SlimeSizeByteConverter();
    private static final HashMap<Object, InternalType> tokenRegistryRev = new HashMap();
    private static final HashMap<Class<?>, Object> tokenRegistry = new HashMap();
    private static final HashMap<Class<?>, Object> tokenRegistry_optional = new HashMap();
    private static final HashMap<Class<?>, Class<?>> typeMapping = new HashMap();

    DataWatcherSerializers() {
    }

    private static void register(Class<?> type, Object token) {
        DataWatcherSerializers.register(type, token, false);
    }

    private static void register(Class<?> type, Object token, boolean optional) {
        DataWatcherSerializers.register(new InternalType(token, type, optional));
    }

    private static void register(InternalType type) {
        if (type.optional) {
            tokenRegistry_optional.put(type.type, type.token);
        } else {
            tokenRegistry.put(type.type, type.token);
        }
        tokenRegistryRev.put(type.token, type);
    }

    public static Class<?> getInternalType(Class<?> exposedType) {
        return typeMapping.get(exposedType);
    }

    private static Object getSerializerToken(Class<?> type, boolean optional) {
        return (optional ? tokenRegistry_optional : tokenRegistry).get(type);
    }

    public static InternalType getInternalTypeFromToken(Object token) {
        return tokenRegistryRev.get(token);
    }

    public static <T> ConvertedToken<T> getConvertedSerializerToken(Class<?> internalType, Class<T> externalType) {
        boolean optional = false;
        Object token = DataWatcherSerializers.getSerializerToken(internalType, optional);
        if (token == null) {
            optional = true;
            token = DataWatcherSerializers.getSerializerToken(internalType, optional);
        }
        if (token == null) {
            throw new RuntimeException("No token found for internal type " + internalType.getName());
        }
        if (!CommonCapabilities.DATAWATCHER_OBJECTS && !(token instanceof Integer)) {
            throw new RuntimeException("Legacy type serializer tokens must be Integers!");
        }
        DuplexConverter<?, T> converter = Conversion.findDuplex(internalType, externalType);
        if (converter == null) {
            throw new RuntimeException("Failed to find converter from internal type " + internalType.getName() + " to " + externalType.getName());
        }
        if (optional) {
            converter = new OptionalDuplexConverter<T>(converter);
        }
        return new ConvertedToken<T>(token, converter);
    }

    static {
        CommonBootstrap.initServer();
        if (CommonBootstrap.evaluateMCVersion(">=", "1.9")) {
            Class<?> registryClass = CommonUtil.getClass("net.minecraft.network.syncher.EntityDataSerializers");
            if (registryClass == null) {
                throw new IllegalStateException("Failed to find EntityDataSerializers class. DataWatcher serializers cannot be registered!");
            }
            Class<?> serializerClass = CommonUtil.getClass("net.minecraft.network.syncher.EntityDataSerializer");
            if (serializerClass == null) {
                throw new IllegalStateException("Failed to find EntityDataSerializer class. DataWatcher serializers cannot be registered!");
            }
            for (Field f : registryClass.getDeclaredFields()) {
                if (!f.getType().equals(serializerClass) || !Modifier.isStatic(f.getModifiers())) continue;
                try {
                    boolean isOptional;
                    if (!Modifier.isPublic(f.getModifiers())) {
                        f.setAccessible(true);
                    }
                    TypeDeclaration typeDec = TypeDeclaration.fromType(f.getGenericType());
                    if (typeDec.genericTypes.length != 1) continue;
                    TypeDeclaration dataType = typeDec.genericTypes[0];
                    boolean bl = isOptional = CommonNMS.isDWROptionalType(dataType.type) && dataType.genericTypes.length == 1;
                    if (isOptional) {
                        dataType = dataType.genericTypes[0];
                    }
                    DataWatcherSerializers.register(dataType.type, f.get(null), isOptional);
                }
                catch (Throwable t) {
                    Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error registering Datawatcher serializer " + f, t);
                }
            }
            typeMapping.put(ChatText.class, ComponentHandle.T.getType());
            typeMapping.put(BlockFace.class, DirectionHandle.T.getType());
        } else {
            DataWatcherSerializers.register(Byte.class, 0);
            DataWatcherSerializers.register(Short.class, 1);
            DataWatcherSerializers.register(Integer.class, 2);
            DataWatcherSerializers.register(Float.class, 3);
            DataWatcherSerializers.register(String.class, 4);
            DataWatcherSerializers.register(ItemStackHandle.T.getType(), 5);
            DataWatcherSerializers.register(BlockPosHandle.T.getType(), 6);
            DataWatcherSerializers.register(RotationsHandle.T.getType(), 7);
            typeMapping.put(Boolean.class, Byte.class);
            typeMapping.put(ComponentHandle.T.getType(), String.class);
            typeMapping.put(ChatText.class, String.class);
            typeMapping.put(BlockFace.class, Integer.class);
        }
        for (Class<?> type : tokenRegistry.keySet()) {
            typeMapping.put(type, type);
        }
        for (Class<?> type : tokenRegistry_optional.keySet()) {
            typeMapping.put(type, type);
        }
        typeMapping.put(Vector.class, RotationsHandle.T.getType());
        typeMapping.put(IntVector3.class, BlockPosHandle.T.getType());
        typeMapping.put(ItemStack.class, ItemStackHandle.T.getType());
        typeMapping.put(BlockData.class, BlockStateHandle.T.getType());
    }

    public static final class InternalType {
        public final Object token;
        public final Class<?> type;
        public final boolean optional;

        private InternalType(Object token, Class<?> type, boolean optional) {
            this.token = token;
            this.type = type;
            this.optional = optional;
        }

        public String toString() {
            String s = this.type.getName();
            if (this.optional) {
                s = "Optional<" + s + ">";
            }
            return s + ":" + this.token;
        }
    }

    public static final class OptionalDuplexConverter<T>
    extends DuplexConverter<Object, T> {
        private final DuplexConverter<Object, T> _baseConverter;

        public OptionalDuplexConverter(DuplexConverter<Object, T> baseConverter) {
            super(OptionalDuplexConverter.makeOptional(baseConverter.input), baseConverter.output);
            this._baseConverter = baseConverter;
        }

        public DuplexConverter<Object, T> getBase() {
            return this._baseConverter;
        }

        @Override
        public T convertInput(Object value) {
            if ((value = CommonNMS.unwrapDWROptional(value)) == null && !this._baseConverter.acceptsNullInput()) {
                return null;
            }
            return this._baseConverter.convertInput(value);
        }

        @Override
        public Object convertOutput(T value) {
            Object result = value != null || this._baseConverter.acceptsNullOutput() ? this._baseConverter.convertOutput(value) : null;
            result = CommonNMS.wrapDWROptional(result);
            return result;
        }

        @Override
        public boolean acceptsNullInput() {
            return true;
        }

        @Override
        public boolean acceptsNullOutput() {
            return true;
        }

        private static TypeDeclaration makeOptional(TypeDeclaration type) {
            return TypeDeclaration.createGeneric(CommonNMS.DWR_OPTIONAL_TYPE, type);
        }
    }

    public static final class ConvertedToken<T> {
        public final Object token;
        public final DuplexConverter<Object, T> converter;

        public ConvertedToken(Object token, DuplexConverter<Object, T> converter) {
            this.token = token;
            this.converter = converter;
        }
    }

    private static final class EntityIdTypeConverter
    extends DuplexConverter<Object, OptionalInt> {
        public EntityIdTypeConverter() {
            super(Integer.class, OptionalInt.class);
        }

        @Override
        public OptionalInt convertInput(Object value) {
            int intValue;
            if (value instanceof Integer && (intValue = ((Integer)value).intValue()) > 0) {
                return OptionalInt.of(intValue - 1);
            }
            return OptionalInt.empty();
        }

        @Override
        public Object convertOutput(OptionalInt value) {
            if (value != null && value.isPresent()) {
                return value.getAsInt() + 1;
            }
            return 0;
        }

        @Override
        public boolean acceptsNullInput() {
            return true;
        }

        @Override
        public boolean acceptsNullOutput() {
            return true;
        }
    }

    private static final class BoatWoodTypeIdConverter
    extends DuplexConverter<Object, BoatWoodType> {
        public BoatWoodTypeIdConverter() {
            super(Integer.class, BoatWoodType.class);
        }

        @Override
        public BoatWoodType convertInput(Object value) {
            if (value instanceof Integer) {
                return BoatWoodType.byId((Integer)value);
            }
            return BoatWoodType.OAK;
        }

        @Override
        public Object convertOutput(BoatWoodType value) {
            return value == null ? 0 : value.getId();
        }

        @Override
        public boolean acceptsNullInput() {
            return true;
        }

        @Override
        public boolean acceptsNullOutput() {
            return true;
        }
    }

    private static final class SlimeSizeByteConverter
    extends DuplexConverter<Object, Integer> {
        public SlimeSizeByteConverter() {
            super(Byte.TYPE, Integer.TYPE);
        }

        @Override
        public Integer convertInput(Object value) {
            return ((Number)value).byteValue() & 0xFF;
        }

        @Override
        public Object convertOutput(Integer integer) {
            return integer.byteValue();
        }
    }
}

