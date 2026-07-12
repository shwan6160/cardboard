/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.NamespacedKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.RegistryReflection;
import com.google.common.base.Suppliers;
import com.mojang.brigadier.arguments.ArgumentType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apiguardian.api.API;
import org.bukkit.NamespacedKey;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class MinecraftArgumentTypes {
    private static final ArgumentTypeGetter ARGUMENT_TYPE_GETTER = CraftBukkitReflection.classExists("org.bukkit.entity.Warden") ? new ArgumentTypeGetterImpl() : new LegacyArgumentTypeGetter();

    private MinecraftArgumentTypes() {
    }

    public static Class<? extends ArgumentType<?>> getClassByKey(@NonNull NamespacedKey key) throws IllegalArgumentException {
        return ARGUMENT_TYPE_GETTER.getClassByKey(key);
    }

    private static interface ArgumentTypeGetter {
        public Class<? extends ArgumentType<?>> getClassByKey(@NonNull NamespacedKey var1) throws IllegalArgumentException;
    }

    private static final class ArgumentTypeGetterImpl
    implements ArgumentTypeGetter {
        private final Supplier<Object> argumentRegistry = Suppliers.memoize(() -> RegistryReflection.builtInRegistryByName("command_argument_type"));
        private final Map<?, ?> byClassMap;

        private ArgumentTypeGetterImpl() {
            try {
                Field declaredField = CraftBukkitReflection.needMCClass("commands.synchronization.ArgumentTypeInfos").getDeclaredFields()[0];
                declaredField.setAccessible(true);
                this.byClassMap = (Map)declaredField.get(null);
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<? extends ArgumentType<?>> getClassByKey(@NonNull NamespacedKey key) throws IllegalArgumentException {
            Object argTypeInfo = RegistryReflection.get(this.argumentRegistry.get(), key.getNamespace() + ":" + key.getKey());
            for (Map.Entry<?, ?> entry : this.byClassMap.entrySet()) {
                if (entry.getValue() != argTypeInfo) continue;
                return (Class)entry.getKey();
            }
            throw new IllegalArgumentException(key.toString());
        }
    }

    private static final class LegacyArgumentTypeGetter
    implements ArgumentTypeGetter {
        private static final Constructor<?> MINECRAFT_KEY_CONSTRUCTOR;
        private static final Method ARGUMENT_REGISTRY_GET_BY_KEY_METHOD;
        private static final Field BY_CLASS_MAP_FIELD;

        private LegacyArgumentTypeGetter() {
        }

        @Override
        public Class<? extends ArgumentType<?>> getClassByKey(@NonNull NamespacedKey key) throws IllegalArgumentException {
            try {
                Object minecraftKey = MINECRAFT_KEY_CONSTRUCTOR.newInstance(key.getNamespace(), key.getKey());
                Object entry = ARGUMENT_REGISTRY_GET_BY_KEY_METHOD.invoke(null, minecraftKey);
                if (entry == null) {
                    throw new IllegalArgumentException(key.toString());
                }
                Map map = (Map)BY_CLASS_MAP_FIELD.get(null);
                for (Map.Entry mapEntry : map.entrySet()) {
                    if (mapEntry.getValue() != entry) continue;
                    return (Class)mapEntry.getKey();
                }
                throw new IllegalArgumentException(key.toString());
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        static {
            try {
                Class<?> argumentRegistry;
                Class<?> minecraftKey;
                if (CraftBukkitReflection.findMCClass("resources.ResourceLocation") != null) {
                    minecraftKey = CraftBukkitReflection.needMCClass("resources.ResourceLocation");
                    argumentRegistry = CraftBukkitReflection.needMCClass("commands.synchronization.ArgumentTypes");
                } else {
                    minecraftKey = CraftBukkitReflection.needNMSClassOrElse("MinecraftKey", "net.minecraft.resources.MinecraftKey");
                    argumentRegistry = CraftBukkitReflection.needNMSClassOrElse("ArgumentRegistry", "net.minecraft.commands.synchronization.ArgumentRegistry");
                }
                MINECRAFT_KEY_CONSTRUCTOR = minecraftKey.getConstructor(String.class, String.class);
                MINECRAFT_KEY_CONSTRUCTOR.setAccessible(true);
                ARGUMENT_REGISTRY_GET_BY_KEY_METHOD = Arrays.stream(argumentRegistry.getDeclaredMethods()).filter(method -> method.getParameterCount() == 1).filter(method -> minecraftKey.equals(method.getParameterTypes()[0])).findFirst().orElseThrow(NoSuchMethodException::new);
                ARGUMENT_REGISTRY_GET_BY_KEY_METHOD.setAccessible(true);
                BY_CLASS_MAP_FIELD = Arrays.stream(argumentRegistry.getDeclaredFields()).filter(field -> Modifier.isStatic(field.getModifiers())).filter(field -> field.getType().equals(Map.class)).filter(field -> {
                    ParameterizedType parameterizedType = (ParameterizedType)field.getGenericType();
                    Type param = parameterizedType.getActualTypeArguments()[0];
                    if (!(param instanceof ParameterizedType)) {
                        return false;
                    }
                    return ((ParameterizedType)param).getRawType().equals(Class.class);
                }).findFirst().orElseThrow(NoSuchFieldException::new);
                BY_CLASS_MAP_FIELD.setAccessible(true);
            }
            catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }
}

