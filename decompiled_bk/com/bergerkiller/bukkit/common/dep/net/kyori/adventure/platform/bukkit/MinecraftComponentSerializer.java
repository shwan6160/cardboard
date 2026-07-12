/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit;

import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.platform.bukkit.MinecraftReflection;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.ComponentSerializer;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public final class MinecraftComponentSerializer
implements ComponentSerializer<Component, Component, Object> {
    private static final MinecraftComponentSerializer INSTANCE;
    @Nullable
    private static final Class<?> CLASS_JSON_DESERIALIZER;
    @Nullable
    private static final Class<?> CLASS_JSON_ELEMENT;
    @Nullable
    private static final Class<?> CLASS_JSON_OPS;
    @Nullable
    private static final Class<?> CLASS_JSON_PARSER;
    @Nullable
    private static final Class<?> CLASS_CHAT_COMPONENT;
    @Nullable
    private static final Class<?> CLASS_COMPONENT_SERIALIZATION;
    @Nullable
    private static final Class<?> CLASS_CRAFT_REGISTRY;
    @Nullable
    private static final Class<?> CLASS_HOLDERLOOKUP_PROVIDER;
    @Nullable
    private static final Class<?> CLASS_REGISTRY_ACCESS;
    @Nullable
    private static final MethodHandle PARSE_JSON;
    @Nullable
    private static final MethodHandle GET_REGISTRY;
    private static final AtomicReference<RuntimeException> INITIALIZATION_ERROR;
    private static final Object JSON_OPS_INSTANCE;
    private static final Object JSON_PARSER_INSTANCE;
    private static final Object MC_TEXT_GSON;
    private static final Object REGISTRY_ACCESS;
    private static final MethodHandle TEXT_SERIALIZER_DESERIALIZE;
    private static final MethodHandle TEXT_SERIALIZER_SERIALIZE;
    private static final MethodHandle TEXT_SERIALIZER_DESERIALIZE_TREE;
    private static final MethodHandle TEXT_SERIALIZER_SERIALIZE_TREE;
    private static final MethodHandle COMPONENTSERIALIZATION_CODEC_ENCODE;
    private static final MethodHandle COMPONENTSERIALIZATION_CODEC_DECODE;
    private static final MethodHandle CREATE_SERIALIZATION_CONTEXT;
    private static final boolean SUPPORTED;

    public static boolean isSupported() {
        return SUPPORTED;
    }

    @NotNull
    public static MinecraftComponentSerializer get() {
        return INSTANCE;
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull Object input) {
        if (!SUPPORTED) {
            throw INITIALIZATION_ERROR.get();
        }
        try {
            Object element;
            if (TEXT_SERIALIZER_SERIALIZE_TREE != null) {
                element = TEXT_SERIALIZER_SERIALIZE_TREE.invoke(input);
            } else if (MC_TEXT_GSON != null) {
                element = ((Gson)MC_TEXT_GSON).toJsonTree(input);
            } else {
                if (COMPONENTSERIALIZATION_CODEC_ENCODE != null && CREATE_SERIALIZATION_CONTEXT != null) {
                    Object serializationContext = CREATE_SERIALIZATION_CONTEXT.bindTo(REGISTRY_ACCESS).invoke(JSON_OPS_INSTANCE);
                    Object result = COMPONENTSERIALIZATION_CODEC_ENCODE.invoke(input, serializationContext, null);
                    Method getOrThrow = result.getClass().getMethod("getOrThrow", Function.class);
                    Object jsonElement = getOrThrow.invoke(result, RuntimeException::new);
                    return BukkitComponentSerializer.gson().serializer().fromJson(jsonElement.toString(), Component.class);
                }
                return BukkitComponentSerializer.gson().serializer().fromJson(TEXT_SERIALIZER_SERIALIZE.invoke(input), Component.class);
            }
            return BukkitComponentSerializer.gson().serializer().fromJson(element.toString(), Component.class);
        }
        catch (Throwable error) {
            throw new UnsupportedOperationException(error);
        }
    }

    @Override
    @NotNull
    public Object serialize(@NotNull Component component) {
        if (!SUPPORTED) {
            throw INITIALIZATION_ERROR.get();
        }
        if (TEXT_SERIALIZER_DESERIALIZE_TREE != null || MC_TEXT_GSON != null) {
            JsonElement json = BukkitComponentSerializer.gson().serializer().toJsonTree(component);
            try {
                if (TEXT_SERIALIZER_DESERIALIZE_TREE != null) {
                    Object unRelocatedJsonElement = PARSE_JSON.invoke(JSON_PARSER_INSTANCE, json.toString());
                    return TEXT_SERIALIZER_DESERIALIZE_TREE.invoke(unRelocatedJsonElement);
                }
                return ((Gson)MC_TEXT_GSON).fromJson(json, CLASS_CHAT_COMPONENT);
            }
            catch (Throwable error) {
                throw new UnsupportedOperationException(error);
            }
        }
        JsonElement json = BukkitComponentSerializer.gson().serializer().toJsonTree(component);
        try {
            if (COMPONENTSERIALIZATION_CODEC_DECODE != null && CREATE_SERIALIZATION_CONTEXT != null) {
                Object serializationContext = CREATE_SERIALIZATION_CONTEXT.bindTo(REGISTRY_ACCESS).invoke(JSON_OPS_INSTANCE);
                Object unRelocatedJsonElement = PARSE_JSON.invoke(JSON_PARSER_INSTANCE, json.toString());
                Object result = COMPONENTSERIALIZATION_CODEC_DECODE.invoke(serializationContext, unRelocatedJsonElement);
                Method getOrThrow = result.getClass().getMethod("getOrThrow", Function.class);
                Object pair = getOrThrow.invoke(result, RuntimeException::new);
                Method getFirst = pair.getClass().getMethod("getFirst", new Class[0]);
                return getFirst.invoke(pair, new Object[0]);
            }
            return TEXT_SERIALIZER_DESERIALIZE.invoke((String)BukkitComponentSerializer.gson().serialize(component));
        }
        catch (Throwable error) {
            throw new UnsupportedOperationException(error);
        }
    }

    static {
        MethodHandle createContext;
        MethodHandle codecDecode;
        MethodHandle codecEncode;
        MethodHandle textSerializerSerializeTree;
        MethodHandle textSerializerDeserializeTree;
        MethodHandle textSerializerSerialize;
        MethodHandle textSerializerDeserialize;
        Object registryAccessInstance;
        Object jsonParserInstance;
        Object jsonOpsInstance;
        Object gson;
        block18: {
            INSTANCE = new MinecraftComponentSerializer();
            CLASS_JSON_DESERIALIZER = MinecraftReflection.findClass("com.goo".concat("gle.gson.JsonDeserializer"));
            CLASS_JSON_ELEMENT = MinecraftReflection.findClass("com.goo".concat("gle.gson.JsonElement"));
            CLASS_JSON_OPS = MinecraftReflection.findClass("com.mo".concat("jang.serialization.JsonOps"));
            CLASS_JSON_PARSER = MinecraftReflection.findClass("com.goo".concat("gle.gson.JsonParser"));
            CLASS_CHAT_COMPONENT = MinecraftReflection.findClass(MinecraftReflection.findNmsClassName("IChatBaseComponent"), MinecraftReflection.findMcClassName("network.chat.IChatBaseComponent"), MinecraftReflection.findMcClassName("network.chat.Component"));
            CLASS_COMPONENT_SERIALIZATION = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("network.chat.ComponentSerialization"));
            CLASS_CRAFT_REGISTRY = MinecraftReflection.findCraftClass("CraftRegistry");
            CLASS_HOLDERLOOKUP_PROVIDER = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("core.HolderLookup$Provider"), MinecraftReflection.findMcClassName("core.HolderLookup$a"));
            CLASS_REGISTRY_ACCESS = MinecraftReflection.findClass(MinecraftReflection.findMcClassName("core.IRegistryCustom"), MinecraftReflection.findMcClassName("core.RegistryAccess"));
            PARSE_JSON = MinecraftReflection.findMethod(CLASS_JSON_PARSER, "parse", CLASS_JSON_ELEMENT, String.class);
            GET_REGISTRY = MinecraftReflection.findStaticMethod(CLASS_CRAFT_REGISTRY, "getMinecraftRegistry", CLASS_REGISTRY_ACCESS, new Class[0]);
            INITIALIZATION_ERROR = new AtomicReference<UnsupportedOperationException>(new UnsupportedOperationException());
            gson = null;
            jsonOpsInstance = null;
            jsonParserInstance = null;
            registryAccessInstance = null;
            textSerializerDeserialize = null;
            textSerializerSerialize = null;
            textSerializerDeserializeTree = null;
            textSerializerSerializeTree = null;
            codecEncode = null;
            codecDecode = null;
            createContext = null;
            try {
                Field gsonField;
                Object registryAccess;
                if (CLASS_JSON_OPS != null) {
                    Field instanceField = CLASS_JSON_OPS.getField("INSTANCE");
                    instanceField.setAccessible(true);
                    jsonOpsInstance = instanceField.get(null);
                }
                if (CLASS_JSON_PARSER != null) {
                    jsonParserInstance = CLASS_JSON_PARSER.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                }
                if (CLASS_CHAT_COMPONENT == null) break block18;
                registryAccessInstance = registryAccess = GET_REGISTRY != null ? GET_REGISTRY.invoke() : null;
                Class<?> chatSerializerClass = Arrays.stream(CLASS_CHAT_COMPONENT.getClasses()).filter(c -> {
                    if (CLASS_JSON_DESERIALIZER != null) {
                        return CLASS_JSON_DESERIALIZER.isAssignableFrom((Class<?>)c);
                    }
                    for (Class<?> itf : c.getInterfaces()) {
                        if (!itf.getSimpleName().equals("JsonDeserializer")) continue;
                        return true;
                    }
                    return false;
                }).findAny().orElse(MinecraftReflection.findNmsClass("ChatSerializer"));
                if (chatSerializerClass != null && (gsonField = (Field)Arrays.stream(chatSerializerClass.getDeclaredFields()).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> m.getType().equals(Gson.class)).findFirst().orElse(null)) != null) {
                    gsonField.setAccessible(true);
                    gson = gsonField.get(null);
                }
                ArrayList candidates = new ArrayList();
                if (chatSerializerClass != null) {
                    candidates.add(chatSerializerClass);
                }
                candidates.addAll(Arrays.asList(CLASS_CHAT_COMPONENT.getClasses()));
                for (Class clazz : candidates) {
                    Method[] declaredMethods = clazz.getDeclaredMethods();
                    Method method = Arrays.stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> CLASS_CHAT_COMPONENT.isAssignableFrom(m.getReturnType())).filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(String.class)).min(Comparator.comparing(Method::getName)).orElse(null);
                    Method serialize = Arrays.stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> m.getReturnType().equals(String.class)).filter(m -> m.getParameterCount() == 1 && CLASS_CHAT_COMPONENT.isAssignableFrom(m.getParameterTypes()[0])).findFirst().orElse(null);
                    Method deserializeTree = Arrays.stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> CLASS_CHAT_COMPONENT.isAssignableFrom(m.getReturnType())).filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(CLASS_JSON_ELEMENT)).findFirst().orElse(null);
                    Method serializeTree = Arrays.stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> m.getReturnType().equals(CLASS_JSON_ELEMENT)).filter(m -> m.getParameterCount() == 1 && CLASS_CHAT_COMPONENT.isAssignableFrom(m.getParameterTypes()[0])).findFirst().orElse(null);
                    Method deserializeTreeWithRegistryAccess = Arrays.stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> CLASS_CHAT_COMPONENT.isAssignableFrom(m.getReturnType())).filter(m -> m.getParameterCount() == 2).filter(m -> m.getParameterTypes()[0].equals(CLASS_JSON_ELEMENT)).filter(m -> m.getParameterTypes()[1].isInstance(registryAccess)).findFirst().orElse(null);
                    Method serializeTreeWithRegistryAccess = Arrays.stream(declaredMethods).filter(m -> Modifier.isStatic(m.getModifiers())).filter(m -> m.getReturnType().equals(CLASS_JSON_ELEMENT)).filter(m -> m.getParameterCount() == 2).filter(m -> CLASS_CHAT_COMPONENT.isAssignableFrom(m.getParameterTypes()[0])).filter(m -> m.getParameterTypes()[1].isInstance(registryAccess)).findFirst().orElse(null);
                    if (method != null) {
                        textSerializerDeserialize = MinecraftReflection.lookup().unreflect(method);
                    }
                    if (serialize != null) {
                        textSerializerSerialize = MinecraftReflection.lookup().unreflect(serialize);
                    }
                    if (deserializeTree != null) {
                        textSerializerDeserializeTree = MinecraftReflection.lookup().unreflect(deserializeTree);
                    } else if (deserializeTreeWithRegistryAccess != null) {
                        deserializeTreeWithRegistryAccess.setAccessible(true);
                        textSerializerDeserializeTree = MethodHandles.insertArguments(MinecraftReflection.lookup().unreflect(deserializeTreeWithRegistryAccess), 1, registryAccess);
                    }
                    if (serializeTree != null) {
                        textSerializerSerializeTree = MinecraftReflection.lookup().unreflect(serializeTree);
                        continue;
                    }
                    if (serializeTreeWithRegistryAccess == null) continue;
                    serializeTreeWithRegistryAccess.setAccessible(true);
                    textSerializerSerializeTree = MethodHandles.insertArguments(MinecraftReflection.lookup().unreflect(serializeTreeWithRegistryAccess), 1, registryAccess);
                }
                if (registryAccess != null && CLASS_HOLDERLOOKUP_PROVIDER != null) {
                    for (AccessibleObject accessibleObject : CLASS_HOLDERLOOKUP_PROVIDER.getDeclaredMethods()) {
                        ((Method)accessibleObject).setAccessible(true);
                        if (((Method)accessibleObject).getParameterCount() != 1 || !((Method)accessibleObject).getParameterTypes()[0].getSimpleName().equals("DynamicOps") || !((Method)accessibleObject).getReturnType().getSimpleName().contains("RegistryOps")) continue;
                        createContext = MinecraftReflection.lookup().unreflect((Method)accessibleObject);
                        break;
                    }
                }
                if (CLASS_COMPONENT_SERIALIZATION == null) break block18;
                for (AccessibleObject accessibleObject : CLASS_COMPONENT_SERIALIZATION.getDeclaredFields()) {
                    if (!Modifier.isStatic(((Field)accessibleObject).getModifiers()) || !((Field)accessibleObject).getType().getSimpleName().equals("Codec")) continue;
                    ((Field)accessibleObject).setAccessible(true);
                    Object codecInstance = ((Field)accessibleObject).get(null);
                    Class<?> codecClass = codecInstance.getClass();
                    for (Method m3 : codecClass.getDeclaredMethods()) {
                        if (m3.getName().equals("decode")) {
                            codecDecode = MinecraftReflection.lookup().unreflect(m3).bindTo(codecInstance);
                            continue;
                        }
                        if (!m3.getName().equals("encode")) continue;
                        codecEncode = MinecraftReflection.lookup().unreflect(m3).bindTo(codecInstance);
                    }
                    break;
                }
            }
            catch (Throwable error) {
                INITIALIZATION_ERROR.set(new UnsupportedOperationException("Error occurred during initialization", error));
            }
        }
        MC_TEXT_GSON = gson;
        JSON_OPS_INSTANCE = jsonOpsInstance;
        JSON_PARSER_INSTANCE = jsonParserInstance;
        TEXT_SERIALIZER_DESERIALIZE = textSerializerDeserialize;
        TEXT_SERIALIZER_SERIALIZE = textSerializerSerialize;
        TEXT_SERIALIZER_DESERIALIZE_TREE = textSerializerDeserializeTree;
        TEXT_SERIALIZER_SERIALIZE_TREE = textSerializerSerializeTree;
        COMPONENTSERIALIZATION_CODEC_ENCODE = codecEncode;
        COMPONENTSERIALIZATION_CODEC_DECODE = codecDecode;
        CREATE_SERIALIZATION_CONTEXT = createContext;
        REGISTRY_ACCESS = registryAccessInstance;
        SUPPORTED = MC_TEXT_GSON != null || TEXT_SERIALIZER_DESERIALIZE != null && TEXT_SERIALIZER_SERIALIZE != null || TEXT_SERIALIZER_DESERIALIZE_TREE != null && TEXT_SERIALIZER_SERIALIZE_TREE != null || COMPONENTSERIALIZATION_CODEC_ENCODE != null && COMPONENTSERIALIZATION_CODEC_DECODE != null && CREATE_SERIALIZATION_CONTEXT != null && JSON_OPS_INSTANCE != null;
    }
}

