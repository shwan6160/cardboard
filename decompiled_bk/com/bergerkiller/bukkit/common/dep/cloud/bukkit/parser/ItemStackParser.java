/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Material
 *  org.bukkit.NamespacedKey
 *  org.bukkit.inventory.ItemStack
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser;

import com.bergerkiller.bukkit.common.dep.cloud.brigadier.parser.WrappedBrigadierParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.data.ProtoItemStack;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CommandBuildContextSupplier;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.MinecraftArgumentTypes;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser.MaterialParser;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.BlockingSuggestionProvider;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.google.common.base.Suppliers;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apiguardian.api.API;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ItemStackParser<C>
implements ArgumentParser.FutureArgumentParser<C, ProtoItemStack> {
    private final ArgumentParser<C, ProtoItemStack> parser = ItemStackParser.findItemInputClass() != null ? new ModernParser() : new LegacyParser();

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, ProtoItemStack> itemStackParser() {
        return ParserDescriptor.of(new ItemStackParser<C>(), ProtoItemStack.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, ProtoItemStack> itemStackComponent() {
        return CommandComponent.builder().parser(ItemStackParser.itemStackParser());
    }

    private static @Nullable Class<?> findItemInputClass() {
        Class[] classes;
        for (Class clazz : classes = new Class[]{CraftBukkitReflection.findNMSClass("ArgumentPredicateItemStack"), CraftBukkitReflection.findMCClass("commands.arguments.item.ArgumentPredicateItemStack"), CraftBukkitReflection.findMCClass("commands.arguments.item.ItemInput")}) {
            if (clazz == null) continue;
            return clazz;
        }
        return null;
    }

    @Override
    public final @NonNull CompletableFuture<@NonNull ArgumentParseResult<ProtoItemStack>> parseFuture(@NonNull CommandContext<C> commandContext, @NonNull CommandInput commandInput) {
        return this.parser.parseFuture(commandContext, commandInput);
    }

    @Override
    public final @NonNull SuggestionProvider<C> suggestionProvider() {
        return this.parser.suggestionProvider();
    }

    static /* synthetic */ Class access$100() {
        return ItemStackParser.findItemInputClass();
    }

    private static final class ModernParser<C>
    implements ArgumentParser.FutureArgumentParser<C, ProtoItemStack> {
        private static final Class<?> NMS_ITEM_STACK_CLASS = CraftBukkitReflection.needNMSClassOrElse("ItemStack", "net.minecraft.world.item.ItemStack");
        private static final Class<?> CRAFT_ITEM_STACK_CLASS = CraftBukkitReflection.needOBCClass("inventory.CraftItemStack");
        private static final Supplier<Class<?>> ARGUMENT_ITEM_STACK_CLASS = Suppliers.memoize(() -> MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft((String)"item_stack")));
        private static final Class<?> ITEM_INPUT_CLASS = Objects.requireNonNull(ItemStackParser.access$100(), "ItemInput class");
        private static final Class<?> NMS_ITEM_CLASS = CraftBukkitReflection.needNMSClassOrElse("Item", "net.minecraft.world.item.Item");
        private static final Supplier<Method> GET_MATERIAL_METHOD = Suppliers.memoize(() -> CraftBukkitReflection.needMethod(CraftBukkitReflection.needOBCClass("util.CraftMagicNumbers"), "getMaterial", NMS_ITEM_CLASS));
        private static final Method CREATE_ITEM_STACK_METHOD = CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find createItemStack method on ItemInput", CraftBukkitReflection.findMethod(ITEM_INPUT_CLASS, "a", Integer.TYPE, Boolean.TYPE), CraftBukkitReflection.findMethod(ITEM_INPUT_CLASS, "createItemStack", Integer.TYPE, Boolean.TYPE), CraftBukkitReflection.findMethod(ITEM_INPUT_CLASS, "createItemStack", Integer.TYPE));
        private static final Method AS_BUKKIT_STACK_METHOD = CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find asBukkitCopy or asCraftMirror method on CraftItemStack", CraftBukkitReflection.findMethod(CRAFT_ITEM_STACK_CLASS, "asBukkitCopy", NMS_ITEM_STACK_CLASS), CraftBukkitReflection.findMethod(CRAFT_ITEM_STACK_CLASS, "asCraftMirror", NMS_ITEM_STACK_CLASS));
        private static final Field ITEM_FIELD = CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find item field on ItemInput", CraftBukkitReflection.findField(ITEM_INPUT_CLASS, "b"), CraftBukkitReflection.findField(ITEM_INPUT_CLASS, "item"));
        private static final Field EXTRA_DATA_FIELD = CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find tag field on ItemInput", CraftBukkitReflection.findField(ITEM_INPUT_CLASS, "c"), CraftBukkitReflection.findField(ITEM_INPUT_CLASS, "tag"), CraftBukkitReflection.findField(ITEM_INPUT_CLASS, "components"));
        private static final Class<?> HOLDER_CLASS = CraftBukkitReflection.findMCClass("core.Holder");
        private static final @Nullable Method VALUE_METHOD = HOLDER_CLASS == null ? null : CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find Holder#value", CraftBukkitReflection.findMethod(HOLDER_CLASS, "value", new Class[0]), CraftBukkitReflection.findMethod(HOLDER_CLASS, "a", new Class[0]));
        private static final Class<?> NBT_TAG_CLASS = CraftBukkitReflection.firstNonNullOrThrow(() -> "Cloud not find net.minecraft.nbt.Tag", CraftBukkitReflection.findClass("net.minecraft.nbt.Tag"), CraftBukkitReflection.findClass("net.minecraft.nbt.NBTBase"), CraftBukkitReflection.findNMSClass("NBTBase"));
        private final ArgumentParser<C, ProtoItemStack> parser = this.createParser();

        ModernParser() {
        }

        private ArgumentParser<C, ProtoItemStack> createParser() {
            Supplier inst = () -> {
                Constructor<?> ctr = ARGUMENT_ITEM_STACK_CLASS.get().getDeclaredConstructors()[0];
                try {
                    if (ctr.getParameterCount() == 0) {
                        return (ArgumentType)ctr.newInstance(new Object[0]);
                    }
                    return (ArgumentType)ctr.newInstance(CommandBuildContextSupplier.commandBuildContext());
                }
                catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Failed to initialize modern ItemStack parser.", e);
                }
            };
            return new WrappedBrigadierParser(inst).flatMapSuccess((ctx, itemInput) -> ArgumentParseResult.successFuture(new ModernProtoItemStack(itemInput)));
        }

        @Override
        public @NonNull CompletableFuture<@NonNull ArgumentParseResult<@NonNull ProtoItemStack>> parseFuture(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
            return this.parser.parseFuture(commandContext, commandInput);
        }

        @Override
        public @NonNull SuggestionProvider<C> suggestionProvider() {
            return this.parser.suggestionProvider();
        }

        private static final class ModernProtoItemStack
        implements ProtoItemStack {
            private final Object itemInput;
            private final Material material;
            private final boolean hasExtraData;

            ModernProtoItemStack(@NonNull Object itemInput) {
                this.itemInput = itemInput;
                try {
                    Object item = ITEM_FIELD.get(itemInput);
                    if (HOLDER_CLASS != null && HOLDER_CLASS.isInstance(item)) {
                        item = VALUE_METHOD.invoke(item, new Object[0]);
                    }
                    this.material = (Material)((Method)GET_MATERIAL_METHOD.get()).invoke(null, item);
                    Object extraData = EXTRA_DATA_FIELD.get(itemInput);
                    if (NBT_TAG_CLASS.isInstance(extraData) || extraData == null) {
                        this.hasExtraData = extraData != null;
                    } else {
                        List isEmptyMethod = Arrays.stream(extraData.getClass().getMethods()).filter(it -> it.getParameterCount() == 0 && it.getReturnType().equals(Boolean.TYPE)).collect(Collectors.toList());
                        if (isEmptyMethod.size() != 1) {
                            throw new IllegalStateException("Failed to locate DataComponentMap/Patch#isEmpty; size=" + isEmptyMethod.size());
                        }
                        this.hasExtraData = (Boolean)((Method)isEmptyMethod.get(0)).invoke(extraData, new Object[0]) == false;
                    }
                }
                catch (ReflectiveOperationException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public @NonNull Material material() {
                return this.material;
            }

            @Override
            public boolean hasExtraData() {
                return this.hasExtraData;
            }

            @Override
            public @NonNull ItemStack createItemStack(int stackSize) {
                try {
                    Object nmsItemStack = CREATE_ITEM_STACK_METHOD.getParameterCount() == 1 ? CREATE_ITEM_STACK_METHOD.invoke(this.itemInput, stackSize) : CREATE_ITEM_STACK_METHOD.invoke(this.itemInput, stackSize, true);
                    return (ItemStack)AS_BUKKIT_STACK_METHOD.invoke(null, nmsItemStack);
                }
                catch (InvocationTargetException ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof CommandSyntaxException) {
                        throw new IllegalArgumentException(cause.getMessage(), cause);
                    }
                    throw new RuntimeException(ex);
                }
                catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static final class LegacyParser<C>
    implements ArgumentParser.FutureArgumentParser<C, ProtoItemStack>,
    BlockingSuggestionProvider.Strings<C> {
        private final ArgumentParser<C, ProtoItemStack> parser = new MaterialParser().mapSuccess((ctx, material) -> CompletableFuture.completedFuture(new LegacyProtoItemStack((Material)material)));

        private LegacyParser() {
        }

        @Override
        public @NonNull CompletableFuture<@NonNull ArgumentParseResult<@NonNull ProtoItemStack>> parseFuture(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
            return this.parser.parseFuture(commandContext, commandInput);
        }

        @Override
        public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
            return Arrays.stream(Material.values()).filter(Material::isItem).map(value -> value.name().toLowerCase(Locale.ROOT)).collect(Collectors.toList());
        }

        private static final class LegacyProtoItemStack
        implements ProtoItemStack {
            private final Material material;

            private LegacyProtoItemStack(@NonNull Material material) {
                this.material = material;
            }

            @Override
            public @NonNull Material material() {
                return this.material;
            }

            @Override
            public boolean hasExtraData() {
                return false;
            }

            @Override
            public @NonNull ItemStack createItemStack(int stackSize) throws IllegalArgumentException {
                if (stackSize > this.material.getMaxStackSize()) {
                    throw new IllegalArgumentException(String.format("The maximum stack size for %s is %d", this.material, this.material.getMaxStackSize()));
                }
                return new ItemStack(this.material, stackSize);
            }
        }
    }
}

