/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.NamespacedKey
 *  org.bukkit.block.Block
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit.parser;

import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.parser.WrappedBrigadierParser;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.data.BlockPredicate;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CommandBuildContextSupplier;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.MinecraftArgumentTypes;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.RegistryReflection;
import com.bergerkiller.bukkit.common.dep.cloud.component.CommandComponent;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandInput;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParseResult;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ArgumentParser;
import com.bergerkiller.bukkit.common.dep.cloud.parser.ParserDescriptor;
import com.bergerkiller.bukkit.common.dep.cloud.suggestion.SuggestionProvider;
import com.google.common.base.Suppliers;
import com.mojang.brigadier.arguments.ArgumentType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apiguardian.api.API;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class BlockPredicateParser<C>
implements ArgumentParser.FutureArgumentParser<C, BlockPredicate> {
    private final ArgumentParser<C, BlockPredicate> parser = this.createParser();

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull ParserDescriptor<C, BlockPredicate> blockPredicateParser() {
        return ParserDescriptor.of(new BlockPredicateParser<C>(), BlockPredicate.class);
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static <C> @NonNull CommandComponent.Builder<C, BlockPredicate> blockPredicateComponent() {
        return CommandComponent.builder().parser(BlockPredicateParser.blockPredicateParser());
    }

    private ArgumentParser<C, BlockPredicate> createParser() {
        Supplier inst = () -> {
            Constructor<?> ctr = ((Class)Reflection.ARGUMENT_BLOCK_PREDICATE_CLASS.get()).getDeclaredConstructors()[0];
            try {
                if (ctr.getParameterCount() == 0) {
                    return (ArgumentType)ctr.newInstance(new Object[0]);
                }
                return (ArgumentType)ctr.newInstance(CommandBuildContextSupplier.commandBuildContext());
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to initialize BlockPredicate parser.", e);
            }
        };
        return new WrappedBrigadierParser(inst).flatMapSuccess((ctx, result) -> {
            if (result instanceof Predicate) {
                return ArgumentParseResult.successFuture(new BlockPredicateImpl((Predicate)result));
            }
            Object commandSourceStack = ctx.get("_cloud_brigadier_native_sender");
            try {
                Object server = Reflection.GET_SERVER_METHOD.invoke(commandSourceStack, new Object[0]);
                Object obj = Reflection.GET_TAG_REGISTRY_METHOD != null ? Reflection.GET_TAG_REGISTRY_METHOD.invoke(server, new Object[0]) : RegistryReflection.builtInRegistryByName("block");
                Objects.requireNonNull(Reflection.CREATE_PREDICATE_METHOD, "create on BlockPredicateArgument$Result");
                Predicate predicate = (Predicate)Reflection.CREATE_PREDICATE_METHOD.invoke(result, obj);
                return ArgumentParseResult.successFuture(new BlockPredicateImpl(predicate));
            }
            catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public @NonNull CompletableFuture<ArgumentParseResult<@NonNull BlockPredicate>> parseFuture(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        return this.parser.parseFuture(commandContext, commandInput);
    }

    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return this.parser.suggestionProvider();
    }

    private static <C> void registerParserSupplier(@NonNull CommandManager<C> commandManager) {
        commandManager.parserRegistry().registerParser(BlockPredicateParser.blockPredicateParser());
    }

    private static final class BlockPredicateImpl
    implements BlockPredicate {
        private final Predicate<Object> predicate;

        BlockPredicateImpl(@NonNull Predicate<Object> predicate) {
            this.predicate = predicate;
        }

        private boolean testImpl(@NonNull Block block, boolean loadChunks) {
            try {
                Object blockInWorld = Reflection.SHAPE_DETECTOR_BLOCK_CTR.newInstance(Reflection.GET_HANDLE_METHOD.invoke((Object)block.getWorld(), new Object[0]), Reflection.BLOCK_POSITION_CTR.newInstance(block.getX(), block.getY(), block.getZ()), loadChunks);
                return this.predicate.test(blockInWorld);
            }
            catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean test(@NonNull Block block) {
            return this.testImpl(block, false);
        }

        @Override
        public @NonNull BlockPredicate loadChunks() {
            return new BlockPredicate(){

                @Override
                public @NonNull BlockPredicate loadChunks() {
                    return this;
                }

                @Override
                public boolean test(Block block) {
                    return this.testImpl(block, true);
                }
            };
        }
    }

    private static final class Reflection {
        private static final Class<?> TAG_CONTAINER_CLASS;
        private static final Class<?> CRAFT_WORLD_CLASS;
        private static final Class<?> MINECRAFT_SERVER_CLASS;
        private static final Class<?> COMMAND_LISTENER_WRAPPER_CLASS;
        private static final Supplier<Class<?>> ARGUMENT_BLOCK_PREDICATE_CLASS;
        private static final Class<?> ARGUMENT_BLOCK_PREDICATE_RESULT_CLASS;
        private static final Class<?> SHAPE_DETECTOR_BLOCK_CLASS;
        private static final Class<?> LEVEL_READER_CLASS;
        private static final Class<?> BLOCK_POSITION_CLASS;
        private static final Constructor<?> BLOCK_POSITION_CTR;
        private static final Constructor<?> SHAPE_DETECTOR_BLOCK_CTR;
        private static final Method GET_HANDLE_METHOD;
        private static final @Nullable Method CREATE_PREDICATE_METHOD;
        private static final Method GET_SERVER_METHOD;
        private static final @Nullable Method GET_TAG_REGISTRY_METHOD;

        private Reflection() {
        }

        static {
            Class tagContainerClass = CraftBukkitReflection.MAJOR_REVISION > 12 && CraftBukkitReflection.MAJOR_REVISION < 16 ? CraftBukkitReflection.needNMSClass("TagRegistry") : CraftBukkitReflection.firstNonNullOrThrow(() -> "tagContainerClass", CraftBukkitReflection.findNMSClass("ITagRegistry"), CraftBukkitReflection.findMCClass("tags.ITagRegistry"), CraftBukkitReflection.findMCClass("tags.TagContainer"), CraftBukkitReflection.findMCClass("core.IRegistry"), CraftBukkitReflection.findMCClass("core.Registry"));
            TAG_CONTAINER_CLASS = tagContainerClass;
            CRAFT_WORLD_CLASS = CraftBukkitReflection.needOBCClass("CraftWorld");
            MINECRAFT_SERVER_CLASS = CraftBukkitReflection.needNMSClassOrElse("MinecraftServer", "net.minecraft.server.MinecraftServer");
            COMMAND_LISTENER_WRAPPER_CLASS = CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find CommandSourceStack class", CraftBukkitReflection.findNMSClass("CommandListenerWrapper"), CraftBukkitReflection.findMCClass("commands.CommandListenerWrapper"), CraftBukkitReflection.findMCClass("commands.CommandSourceStack"));
            ARGUMENT_BLOCK_PREDICATE_CLASS = Suppliers.memoize(() -> MinecraftArgumentTypes.getClassByKey(NamespacedKey.minecraft((String)"block_predicate")));
            ARGUMENT_BLOCK_PREDICATE_RESULT_CLASS = CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find BlockPredicateArgument$Result class", CraftBukkitReflection.findNMSClass("ArgumentBlockPredicate$b"), CraftBukkitReflection.findMCClass("commands.arguments.blocks.ArgumentBlockPredicate$b"), CraftBukkitReflection.findMCClass("commands.arguments.blocks.BlockPredicateArgument$Result"));
            SHAPE_DETECTOR_BLOCK_CLASS = CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find BlockInWorld class", CraftBukkitReflection.findNMSClass("ShapeDetectorBlock"), CraftBukkitReflection.findMCClass("world.level.block.state.pattern.ShapeDetectorBlock"), CraftBukkitReflection.findMCClass("world.level.block.state.pattern.BlockInWorld"));
            LEVEL_READER_CLASS = CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find LevelReader class", CraftBukkitReflection.findNMSClass("IWorldReader"), CraftBukkitReflection.findMCClass("world.level.IWorldReader"), CraftBukkitReflection.findMCClass("world.level.LevelReader"));
            BLOCK_POSITION_CLASS = CraftBukkitReflection.firstNonNullOrThrow(() -> "Couldn't find BlockPos class", CraftBukkitReflection.findNMSClass("BlockPosition"), CraftBukkitReflection.findMCClass("core.BlockPosition"), CraftBukkitReflection.findMCClass("core.BlockPos"));
            BLOCK_POSITION_CTR = CraftBukkitReflection.needConstructor(BLOCK_POSITION_CLASS, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            SHAPE_DETECTOR_BLOCK_CTR = CraftBukkitReflection.needConstructor(SHAPE_DETECTOR_BLOCK_CLASS, LEVEL_READER_CLASS, BLOCK_POSITION_CLASS, Boolean.TYPE);
            GET_HANDLE_METHOD = CraftBukkitReflection.needMethod(CRAFT_WORLD_CLASS, "getHandle", new Class[0]);
            CREATE_PREDICATE_METHOD = CraftBukkitReflection.firstNonNullOrNull(CraftBukkitReflection.findMethod(ARGUMENT_BLOCK_PREDICATE_RESULT_CLASS, "create", TAG_CONTAINER_CLASS), CraftBukkitReflection.findMethod(ARGUMENT_BLOCK_PREDICATE_RESULT_CLASS, "a", TAG_CONTAINER_CLASS));
            GET_SERVER_METHOD = CraftBukkitReflection.streamMethods(COMMAND_LISTENER_WRAPPER_CLASS).filter(it -> it.getReturnType().equals(MINECRAFT_SERVER_CLASS) && it.getParameterCount() == 0).findFirst().orElseThrow(() -> new IllegalStateException("Could not find CommandSourceStack#getServer."));
            GET_TAG_REGISTRY_METHOD = CraftBukkitReflection.firstNonNullOrNull(CraftBukkitReflection.findMethod(MINECRAFT_SERVER_CLASS, "getTagRegistry", new Class[0]), CraftBukkitReflection.findMethod(MINECRAFT_SERVER_CLASS, "getTags", new Class[0]), CraftBukkitReflection.streamMethods(MINECRAFT_SERVER_CLASS).filter(it -> it.getReturnType().equals(TAG_CONTAINER_CLASS) && it.getParameterCount() == 0).findFirst().orElse(null));
        }
    }
}

