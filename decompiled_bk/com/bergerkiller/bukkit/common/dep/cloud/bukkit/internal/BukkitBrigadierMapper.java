/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Bukkit
 *  org.bukkit.NamespacedKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.argument.BrigadierMappingBuilder;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CommandBuildContextSupplier;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.MinecraftArgumentTypes;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.RegistryReflection;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.BlockPredicateParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.EnchantmentParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.ItemStackParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.ItemStackPredicateParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.NamespacedKeyParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.Location2DParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.location.LocationParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.SingleEntitySelectorParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.selector.SinglePlayerSelectorParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.standard.UUIDParser;
import com.bergerkiller.bukkit.common.dep.typetoken.TypeToken;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.INTERNAL)
public final class BukkitBrigadierMapper<C> {
    private final Logger logger;
    private final CloudBrigadierManager<C, ?> brigadierManager;

    public BukkitBrigadierMapper(@NonNull Logger logger, @NonNull CloudBrigadierManager<C, ?> brigadierManager) {
        this.logger = logger;
        this.brigadierManager = brigadierManager;
    }

    public void registerBuiltInMappings() {
        this.registerUUID();
        this.mapSimpleNMS(new TypeToken<NamespacedKeyParser<C>>(){}, "resource_location", true);
        this.registerEnchantment();
        this.mapSimpleNMS(new TypeToken<ItemStackParser<C>>(){}, "item_stack");
        this.mapSimpleNMS(new TypeToken<ItemStackPredicateParser<C>>(){}, "item_predicate");
        this.mapSimpleNMS(new TypeToken<BlockPredicateParser<C>>(){}, "block_predicate");
        this.mapSelector(new TypeToken<SingleEntitySelectorParser<C>>(){}, true, false);
        this.mapSelector(new TypeToken<SinglePlayerSelectorParser<C>>(){}, true, true);
        this.mapSelector(new TypeToken<MultipleEntitySelectorParser<C>>(){}, false, false);
        this.mapSelector(new TypeToken<MultiplePlayerSelectorParser<C>>(){}, false, true);
        this.mapNMS(new TypeToken<LocationParser<C>>(){}, "vec3", this::argumentVec3);
        this.mapNMS(new TypeToken<Location2DParser<C>>(){}, "vec2", this::argumentVec2);
    }

    private void registerEnchantment() {
        if (Bukkit.getServer() == null) {
            this.mapResourceKey(new TypeToken<EnchantmentParser<C>>(){}, "enchantment");
            return;
        }
        try {
            Class<? extends ArgumentType<?>> ench = MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft((String)"item_enchantment"));
            this.mapSimpleNMS(new TypeToken<EnchantmentParser<C>>(){}, "item_enchantment");
        }
        catch (IllegalArgumentException ignore) {
            this.mapResourceKey(new TypeToken<EnchantmentParser<C>>(){}, "enchantment");
        }
    }

    private void registerUUID() {
        if (Bukkit.getServer() == null) {
            this.mapSimpleNMS(new TypeToken<UUIDParser<C>>(){}, "uuid");
            return;
        }
        try {
            Class<? extends ArgumentType<?>> uuid = MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft((String)"uuid"));
            this.mapSimpleNMS(new TypeToken<UUIDParser<C>>(){}, "uuid");
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
    }

    private <T extends ArgumentParser<C, ?>> void mapResourceKey(@NonNull TypeToken<T> parserType, @NonNull String registryName) {
        this.mapNMS(parserType, "resource_key", type -> (ArgumentType)type.getDeclaredConstructors()[0].newInstance(RegistryReflection.registryKey(registryName)));
    }

    private <T extends ArgumentParser<C, ?>> void mapSelector(@NonNull TypeToken<T> parserType, boolean single, boolean playersOnly) {
        this.mapNMS(parserType, "entity", argumentTypeCls -> {
            Constructor<?> constructor = argumentTypeCls.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            return (ArgumentType)constructor.newInstance(single, playersOnly);
        });
    }

    private @NonNull ArgumentType<?> argumentVec3(Class<? extends ArgumentType<?>> type) throws ReflectiveOperationException {
        return type.getDeclaredConstructor(Boolean.TYPE).newInstance(true);
    }

    private @NonNull ArgumentType<?> argumentVec2(Class<? extends ArgumentType<?>> type) throws ReflectiveOperationException {
        return type.getDeclaredConstructor(Boolean.TYPE).newInstance(true);
    }

    public <T extends ArgumentParser<C, ?>> void mapSimpleNMS(@NonNull TypeToken<T> type, @NonNull String argumentId) {
        this.mapSimpleNMS(type, argumentId, false);
    }

    public <T extends ArgumentParser<C, ?>> void mapSimpleNMS(@NonNull TypeToken<T> type, @NonNull String argumentId, boolean useCloudSuggestions) {
        this.mapNMS(type, argumentId, cls -> {
            Object[] objectArray;
            Constructor<?> ctr = cls.getDeclaredConstructors()[0];
            if (ctr.getParameterCount() == 1) {
                Object[] objectArray2 = new Object[1];
                objectArray = objectArray2;
                objectArray2[0] = CommandBuildContextSupplier.commandBuildContext();
            } else {
                objectArray = new Object[]{};
            }
            Object[] args = objectArray;
            return (ArgumentType)ctr.newInstance(args);
        }, useCloudSuggestions);
    }

    public <T extends ArgumentParser<C, ?>> void mapNMS(@NonNull TypeToken<T> type, @NonNull String argumentId, @NonNull ArgumentTypeFactory factory) {
        this.mapNMS(type, argumentId, factory, false);
    }

    public <T extends ArgumentParser<C, ?>> void mapNMS(@NonNull TypeToken<T> type, @NonNull String argumentId, @NonNull ArgumentTypeFactory factory, boolean cloudSuggestions) {
        Supplier argumentTypeClass = Suppliers.memoize(() -> {
            try {
                return MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft((String)argumentId));
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to locate class for " + argumentId, e);
            }
        });
        this.brigadierManager.registerMapping(type, arg_0 -> this.lambda$mapNMS$5(factory, (java.util.function.Supplier)argumentTypeClass, argumentId, cloudSuggestions, arg_0));
    }

    private /* synthetic */ void lambda$mapNMS$5(ArgumentTypeFactory factory, java.util.function.Supplier argumentTypeClass, String argumentId, boolean cloudSuggestions, BrigadierMappingBuilder builder) {
        builder.to(arg_0 -> this.lambda$mapNMS$4(factory, (java.util.function.Supplier)argumentTypeClass, argumentId, arg_0));
        if (cloudSuggestions) {
            builder.cloudSuggestions();
        }
    }

    private /* synthetic */ ArgumentType lambda$mapNMS$4(ArgumentTypeFactory factory, java.util.function.Supplier argumentTypeClass, String argumentId, ArgumentParser argument) {
        try {
            return factory.makeInstance((Class)argumentTypeClass.get());
        }
        catch (Exception e) {
            this.logger.log(Level.WARNING, "Failed to create instance of " + argumentId + ", falling back to StringArgumentType.word()", e);
            return StringArgumentType.word();
        }
    }

    @API(status=API.Status.INTERNAL)
    @FunctionalInterface
    public static interface ArgumentTypeFactory {
        public ArgumentType<?> makeInstance(Class<? extends ArgumentType<?>> var1) throws ReflectiveOperationException;
    }
}

