/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal;

import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.typetoken.GenericTypeReflector;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
public final class RegistryReflection {
    public static final @Nullable Field REGISTRY_REGISTRY;
    public static final @Nullable Method REGISTRY_GET;
    public static final @Nullable Method REGISTRY_KEY;
    private static final Class<?> IDENTIFIER_CLASS;
    private static final Class<?> RESOURCE_KEY_CLASS;
    private static final Executable NEW_RESOURCE_LOCATION;
    private static final Executable CREATE_REGISTRY_RESOURCE_KEY;

    private RegistryReflection() {
    }

    public static Object registryKey(String registryName) {
        Objects.requireNonNull(CREATE_REGISTRY_RESOURCE_KEY, "CREATE_REGISTRY_RESOURCE_KEY");
        try {
            Object resourceLocation = RegistryReflection.createResourceLocation(registryName);
            return CraftBukkitReflection.invokeConstructorOrStaticMethod(CREATE_REGISTRY_RESOURCE_KEY, resourceLocation);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object get(Object registry, String resourceLocation) {
        Objects.requireNonNull(REGISTRY_GET, "REGISTRY_GET");
        try {
            return REGISTRY_GET.invoke(registry, RegistryReflection.createResourceLocation(resourceLocation));
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object builtInRegistryByName(String name) {
        Objects.requireNonNull(REGISTRY_REGISTRY, "REGISTRY_REGISTRY");
        try {
            return RegistryReflection.get(REGISTRY_REGISTRY.get(null), name);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object createResourceLocation(String str) {
        try {
            return CraftBukkitReflection.invokeConstructorOrStaticMethod(NEW_RESOURCE_LOCATION, str);
        }
        catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field registryRegistryField(Class<?> registryClass) {
        return Arrays.stream(registryClass.getDeclaredFields()).filter(it -> it.getType().equals(registryClass)).findFirst().orElseGet(() -> RegistryReflection.registryRegistryFieldFromBuiltInRegistries(registryClass));
    }

    private static Field registryRegistryFieldFromBuiltInRegistries(Class<?> registryClass) {
        Class<?> builtInRegistriesClass = CraftBukkitReflection.needMCClass("core.registries.BuiltInRegistries");
        return Arrays.stream(builtInRegistriesClass.getDeclaredFields()).filter(it -> {
            if (!it.getType().equals(registryClass) || !Modifier.isStatic(it.getModifiers())) {
                return false;
            }
            Type genericType = it.getGenericType();
            if (!(genericType instanceof ParameterizedType)) {
                return false;
            }
            Type valueType = ((ParameterizedType)genericType).getActualTypeArguments()[0];
            while (valueType instanceof WildcardType) {
                valueType = ((WildcardType)valueType).getUpperBounds()[0];
            }
            return GenericTypeReflector.erase(valueType).equals(registryClass);
        }).findFirst().orElseThrow(() -> new IllegalStateException("Could not find Registry Registry field"));
    }

    static {
        IDENTIFIER_CLASS = CraftBukkitReflection.needNMSClassOrElse("MinecraftKey", "net.minecraft.resources.MinecraftKey", "net.minecraft.resources.ResourceLocation", "net.minecraft.resources.Identifier");
        RESOURCE_KEY_CLASS = CraftBukkitReflection.needNMSClassOrElse("ResourceKey", "net.minecraft.resources.ResourceKey");
        if (CraftBukkitReflection.MAJOR_REVISION < 17) {
            REGISTRY_REGISTRY = null;
            REGISTRY_GET = null;
            REGISTRY_KEY = null;
            NEW_RESOURCE_LOCATION = null;
            CREATE_REGISTRY_RESOURCE_KEY = null;
        } else {
            Class registryClass = CraftBukkitReflection.firstNonNullOrThrow(() -> "Registry", CraftBukkitReflection.findMCClass("core.IRegistry"), CraftBukkitReflection.findMCClass("core.Registry"));
            REGISTRY_REGISTRY = RegistryReflection.registryRegistryField(registryClass);
            REGISTRY_REGISTRY.setAccessible(true);
            REGISTRY_GET = Arrays.stream(registryClass.getDeclaredMethods()).filter(it -> it.getParameterCount() == 1 && it.getParameterTypes()[0].equals(IDENTIFIER_CLASS) && it.getReturnType().equals(Object.class)).findFirst().orElseThrow(() -> new IllegalStateException("Could not find Registry#get(Identifier)"));
            Class<?> resourceKeyClass = CraftBukkitReflection.needMCClass("resources.ResourceKey");
            REGISTRY_KEY = Arrays.stream(registryClass.getDeclaredMethods()).filter(m -> m.getParameterCount() == 0 && m.getReturnType().equals(resourceKeyClass)).findFirst().orElse(null);
            NEW_RESOURCE_LOCATION = CraftBukkitReflection.firstNonNullOrThrow(() -> "Could not find Identifier#parse(String) or Identifier#<init>(String)", CraftBukkitReflection.findConstructor(IDENTIFIER_CLASS, String.class), CraftBukkitReflection.findMethod(IDENTIFIER_CLASS, "parse", String.class), CraftBukkitReflection.findMethod(IDENTIFIER_CLASS, "a", String.class));
            CREATE_REGISTRY_RESOURCE_KEY = CraftBukkitReflection.firstNonNullOrThrow(() -> "Could not find ResourceKey#createRegistryKey(Identifier)", CraftBukkitReflection.findMethod(RESOURCE_KEY_CLASS, "createRegistryKey", IDENTIFIER_CLASS), CraftBukkitReflection.findMethod(RESOURCE_KEY_CLASS, "a", IDENTIFIER_CLASS));
        }
    }
}

