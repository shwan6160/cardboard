/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.papermc.paper.command.brigadier.CommandSourceStack
 *  io.papermc.paper.plugin.bootstrap.BootstrapContext
 *  io.papermc.paper.plugin.configuration.PluginMeta
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.entity.Entity
 *  org.bukkit.plugin.Plugin
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper;

import com.bergerkiller.bukkit.common.dep.cloud.CloudCapability;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.SenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.SenderMapperHolder;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.BrigadierManagerHolder;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandContextKeys;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitDefaultCaptionsProvider;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitParsers;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.CloudBukkitCapabilities;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.PluginHolder;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.BukkitHelper;
import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinator;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandRegistrationHandler;
import com.bergerkiller.bukkit.common.dep.cloud.paper.ModernPaperBrigadier;
import com.bergerkiller.bukkit.common.dep.cloud.paper.PaperCommandPreprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.paper.PluginMetaHolder;
import com.bergerkiller.bukkit.common.dep.cloud.paper.parser.KeyedWorldParser;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.NamedTextColor;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.format.TextColor;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.configuration.PluginMeta;
import java.util.List;
import java.util.logging.Level;
import org.apiguardian.api.API;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@API(status=API.Status.EXPERIMENTAL)
public class PaperCommandManager<C>
extends CommandManager<C>
implements SenderMapperHolder<CommandSourceStack, C>,
PluginMetaHolder,
PluginHolder,
BrigadierManagerHolder<C, CommandSourceStack> {
    private final PluginMeta pluginMeta;
    private final SenderMapper<CommandSourceStack, C> senderMapper;

    public static <C> Builder<C> builder(SenderMapper<CommandSourceStack, C> senderMapper) {
        return new Builder(senderMapper);
    }

    public static Builder<CommandSourceStack> builder() {
        return new Builder<CommandSourceStack>(SenderMapper.identity());
    }

    private PaperCommandManager(@NonNull PluginMeta pluginMeta, @NonNull ExecutionCoordinator<C> executionCoordinator, @NonNull SenderMapper<CommandSourceStack, C> senderMapper) {
        super(executionCoordinator, CommandRegistrationHandler.nullCommandRegistrationHandler());
        this.pluginMeta = pluginMeta;
        this.senderMapper = senderMapper;
        this.commandRegistrationHandler(new ModernPaperBrigadier<C, CommandSourceStack>(CommandSourceStack.class, this, senderMapper, () -> this.lockRegistration()));
        CloudBukkitCapabilities.CAPABLE.forEach(x$0 -> this.registerCapability((CloudCapability)x$0));
        this.registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
        BukkitParsers.register(this);
        this.registerDefaultExceptionHandlers();
        this.captionRegistry().registerProvider(new BukkitDefaultCaptionsProvider());
        this.registerCommandPreProcessor(ctx -> ctx.commandContext().store(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER, this.senderMapper().reverse(ctx.commandContext().sender()).getSender()));
        this.registerCommandPreProcessor(new PaperCommandPreprocessor<CommandSourceStack, C>(this, this.senderMapper(), commandSourceStack -> {
            @Nullable Entity executor = commandSourceStack.getExecutor();
            if (executor != null) {
                return executor;
            }
            return commandSourceStack.getSender();
        }));
        this.parserRegistry().registerParser(KeyedWorldParser.keyedWorldParser());
    }

    @Override
    public final boolean hasPermission(@NonNull C sender, @NonNull String permission) {
        return this.senderMapper().reverse(sender).getSender().hasPermission(permission);
    }

    @Override
    public final @NonNull SenderMapper<CommandSourceStack, C> senderMapper() {
        return this.senderMapper;
    }

    private void registerDefaultExceptionHandlers() {
        this.registerDefaultExceptionHandlers(triplet -> this.senderMapper().reverse(((CommandContext)triplet.first()).sender()).getSender().sendMessage((Component)Component.text(((CommandContext)triplet.first()).formatCaption((Caption)triplet.second(), (List)triplet.third()), (TextColor)NamedTextColor.RED)), pair -> this.owningPlugin().getLogger().log(Level.SEVERE, (String)pair.first(), (Throwable)pair.second()));
    }

    @Override
    public final PluginMeta owningPluginMeta() {
        return this.pluginMeta;
    }

    @Override
    public final boolean hasBrigadierManager() {
        return true;
    }

    @Override
    public final @NonNull CloudBrigadierManager<C, ? extends CommandSourceStack> brigadierManager() {
        return ((BrigadierManagerHolder)((Object)this.commandRegistrationHandler())).brigadierManager();
    }

    public static final class Builder<C> {
        private final SenderMapper<CommandSourceStack, C> senderMapper;

        private Builder(SenderMapper<CommandSourceStack, C> senderMapper) {
            this.senderMapper = senderMapper;
        }

        public CoordinatedBuilder<C> executionCoordinator(ExecutionCoordinator<C> executionCoordinator) {
            return new CoordinatedBuilder(this.senderMapper, executionCoordinator);
        }
    }

    public static final class CoordinatedBuilder<C> {
        private final SenderMapper<CommandSourceStack, C> senderMapper;
        private final ExecutionCoordinator<C> executionCoordinator;

        private CoordinatedBuilder(SenderMapper<CommandSourceStack, C> senderMapper, ExecutionCoordinator<C> executionCoordinator) {
            this.senderMapper = senderMapper;
            this.executionCoordinator = executionCoordinator;
        }

        public @NonNull PaperCommandManager<C> buildOnEnable(@NonNull Plugin plugin) {
            PaperCommandManager mgr = new PaperCommandManager(plugin.getPluginMeta(), this.executionCoordinator, this.senderMapper);
            ((ModernPaperBrigadier)mgr.commandRegistrationHandler()).registerPlugin(plugin);
            BukkitHelper.ensurePluginEnabledOrEnabling(plugin);
            return mgr;
        }

        public @NonNull Bootstrapped<C> buildBootstrapped(@NonNull BootstrapContext context) {
            Bootstrapped mgr = new Bootstrapped(context.getPluginMeta(), this.executionCoordinator, this.senderMapper);
            ((ModernPaperBrigadier)mgr.commandRegistrationHandler()).registerBootstrap(context);
            return mgr;
        }
    }

    public static final class Bootstrapped<C>
    extends PaperCommandManager<C> {
        private Bootstrapped(@NonNull PluginMeta pluginMeta, @NonNull ExecutionCoordinator<C> executionCoordinator, @NonNull SenderMapper<CommandSourceStack, C> senderMapper) {
            super(pluginMeta, executionCoordinator, senderMapper);
        }

        public void onEnable() {
            BukkitHelper.ensurePluginEnabledOrEnabling(this.owningPlugin());
        }
    }
}

