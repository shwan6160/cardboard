/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.NamespacedKey
 *  org.bukkit.inventory.ItemStack
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.parser.WrappedBrigadierParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.data.ItemStackPredicate;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CommandBuildContextSupplier;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.MinecraftArgumentTypes;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.google.common.base.Suppliers;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apiguardian.api.API;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ItemStackPredicateParser<C>
implements ArgumentParser.FutureArgumentParser<C, ItemStackPredicate> {
    private static final Class<?> CRAFT_ITEM_STACK_CLASS = CraftBukkitReflection.needOBCClass("inventory.CraftItemStack");
    private static final Supplier<Class<?>> ARGUMENT_ITEM_PREDICATE_CLASS = Suppliers.memoize(() -> MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft((String)"item_predicate")));
    private static final Class<?> ARGUMENT_ITEM_PREDICATE_RESULT_CLASS = CraftBukkitReflection.firstNonNullOrNull(CraftBukkitReflection.findNMSClass("ArgumentItemPredicate$b"), CraftBukkitReflection.findMCClass("commands.arguments.item.ArgumentItemPredicate$b"), CraftBukkitReflection.findMCClass("commands.arguments.item.ItemPredicateArgument$Result"));
    private static final @Nullable Method CREATE_PREDICATE_METHOD = ARGUMENT_ITEM_PREDICATE_RESULT_CLASS == null ? null : CraftBukkitReflection.firstNonNullOrNull(CraftBukkitReflection.findMethod(ARGUMENT_ITEM_PREDICATE_RESULT_CLASS, "create", CommandContext.class), CraftBukkitReflection.findMethod(ARGUMENT_ITEM_PREDICATE_RESULT_CLASS, "a", CommandContext.class));
    private static final Method AS_NMS_COPY_METHOD = CraftBukkitReflection.needMethod(CRAFT_ITEM_STACK_CLASS, "asNMSCopy", ItemStack.class);
    private final ArgumentParser<C, ItemStackPredicate> parser = this.createParser();

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, ItemStackPredicate> itemStackPredicateParser() {
        return ParserDescriptor.of(new ItemStackPredicateParser<C>(), ItemStackPredicate.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, ItemStackPredicate> itemStackPredicateComponent() {
        return CommandComponent.builder().parser(ItemStackPredicateParser.itemStackPredicateParser());
    }

    private ArgumentParser<C, ItemStackPredicate> createParser() {
        Supplier inst = () -> {
            Constructor<?> ctr = ARGUMENT_ITEM_PREDICATE_CLASS.get().getDeclaredConstructors()[0];
            try {
                if (ctr.getParameterCount() == 0) {
                    return (ArgumentType)ctr.newInstance(new Object[0]);
                }
                return (ArgumentType)ctr.newInstance(CommandBuildContextSupplier.commandBuildContext());
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to initialize ItemPredicate parser.", e);
            }
        };
        return new WrappedBrigadierParser(inst).flatMapSuccess((ctx, result) -> {
            if (result instanceof Predicate) {
                return ArgumentParseResult.successFuture(new ItemStackPredicateImpl((Predicate)result));
            }
            Object commandSourceStack = ctx.get("_cloud_brigadier_native_sender");
            CommandContext<Object> dummy = ItemStackPredicateParser.createDummyContext(ctx, commandSourceStack);
            Objects.requireNonNull(CREATE_PREDICATE_METHOD, "ItemPredicateArgument$Result#create");
            try {
                Predicate predicate = (Predicate)CREATE_PREDICATE_METHOD.invoke(result, dummy);
                return ArgumentParseResult.successFuture(new ItemStackPredicateImpl(predicate));
            }
            catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private static <C> @NonNull CommandContext<Object> createDummyContext(@NonNull com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext<C> ctx, @NonNull Object commandSourceStack) {
        return new CommandContext<Object>(commandSourceStack, ctx.rawInput().input(), Collections.emptyMap(), null, null, Collections.emptyList(), StringRange.at(0), null, null, false);
    }

    private static <C> void registerParserSupplier(@NonNull CommandManager<C> commandManager) {
        commandManager.parserRegistry().registerParser(ItemStackPredicateParser.itemStackPredicateParser());
    }

    @Override
    public @NonNull CompletableFuture<ArgumentParseResult<@NonNull ItemStackPredicate>> parseFuture(@NonNull com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        return this.parser.parseFuture(commandContext, commandInput);
    }

    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return this.parser.suggestionProvider();
    }

    private static final class ItemStackPredicateImpl
    implements ItemStackPredicate {
        private final Predicate<Object> predicate;

        ItemStackPredicateImpl(@NonNull Predicate<Object> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(@NonNull ItemStack itemStack) {
            try {
                return this.predicate.test(AS_NMS_COPY_METHOD.invoke(null, itemStack));
            }
            catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

