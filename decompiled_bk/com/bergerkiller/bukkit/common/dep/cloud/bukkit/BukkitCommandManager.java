/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.CloudCapability;
import com.bergerkiller.bukkit.common.dep.cloud.CommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.SenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.SenderMapperHolder;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.BrigadierManagerHolder;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandPreprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitDefaultCaptionsProvider;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitParsers;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitPluginRegistrationHandler;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.CloudBukkitCapabilities;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.CloudBukkitListener;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.CloudCommodoreManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.PluginHolder;
import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import com.bergerkiller.bukkit.common.dep.cloud.context.CommandContext;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinator;
import com.bergerkiller.bukkit.common.dep.cloud.internal.CommandRegistrationHandler;
import com.bergerkiller.bukkit.common.dep.cloud.state.RegistrationState;
import java.util.List;
import java.util.logging.Level;
import org.apiguardian.api.API;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class BukkitCommandManager<C>
extends CommandManager<C>
implements BrigadierManagerHolder<C, Object>,
SenderMapperHolder<CommandSender, C>,
PluginHolder {
    private final Plugin owningPlugin;
    private final SenderMapper<CommandSender, C> senderMapper;
    private boolean splitAliases = false;

    @API(status=API.Status.INTERNAL, since="2.0.0")
    protected BukkitCommandManager(@NonNull Plugin owningPlugin, @NonNull ExecutionCoordinator<C> commandExecutionCoordinator, @NonNull SenderMapper<CommandSender, C> senderMapper) throws InitializationException {
        super(commandExecutionCoordinator, new BukkitPluginRegistrationHandler());
        try {
            ((BukkitPluginRegistrationHandler)this.commandRegistrationHandler()).initialize(this);
        }
        catch (ReflectiveOperationException exception) {
            throw new InitializationException("Failed to initialize command registration handler", exception);
        }
        this.owningPlugin = owningPlugin;
        this.senderMapper = senderMapper;
        CloudBukkitCapabilities.CAPABLE.forEach(x$0 -> this.registerCapability((CloudCapability)x$0));
        this.registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
        this.registerCommandPreProcessor(new BukkitCommandPreprocessor(this));
        BukkitParsers.register(this);
        this.owningPlugin.getServer().getPluginManager().registerEvents(new CloudBukkitListener(this), this.owningPlugin);
        this.registerDefaultExceptionHandlers();
        this.captionRegistry().registerProvider(new BukkitDefaultCaptionsProvider());
    }

    @Override
    public final @NonNull Plugin owningPlugin() {
        return this.owningPlugin;
    }

    @Override
    public final @NonNull SenderMapper<CommandSender, C> senderMapper() {
        return this.senderMapper;
    }

    @Override
    public final boolean hasPermission(@NonNull C sender, @NonNull String permission) {
        if (permission.isEmpty()) {
            return true;
        }
        return this.senderMapper.reverse(sender).hasPermission(permission);
    }

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    protected final boolean splitAliases() {
        return this.splitAliases;
    }

    @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
    protected final void splitAliases(boolean value) {
        this.requireState(RegistrationState.BEFORE_REGISTRATION);
        this.splitAliases = value;
    }

    protected final void checkBrigadierCompatibility() throws BrigadierInitializationException {
        if (!this.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            throw new BrigadierInitializationException("Missing capability " + CloudBukkitCapabilities.class.getSimpleName() + "." + CloudBukkitCapabilities.BRIGADIER + " (Minecraft version too old? Brigadier was added in 1.13). See the Javadocs for more details");
        }
    }

    public synchronized void registerBrigadier() throws BrigadierInitializationException {
        this.requireState(RegistrationState.BEFORE_REGISTRATION);
        this.checkBrigadierCompatibility();
        if (!this.hasCapability(CloudBukkitCapabilities.COMMODORE_BRIGADIER)) {
            throw new BrigadierInitializationException("Missing capability " + CloudBukkitCapabilities.class.getSimpleName() + "." + CloudBukkitCapabilities.COMMODORE_BRIGADIER + " (Minecraft version too new). See the Javadocs for more details");
        }
        CommandRegistrationHandler handler = this.commandRegistrationHandler();
        if (handler instanceof CloudCommodoreManager) {
            throw new IllegalStateException("Brigadier is already registered! Holder: " + handler);
        }
        try {
            CloudCommodoreManager cloudCommodoreManager = new CloudCommodoreManager(this);
            cloudCommodoreManager.initialize(this);
            this.commandRegistrationHandler(cloudCommodoreManager);
            this.splitAliases(true);
        }
        catch (Exception e) {
            throw new BrigadierInitializationException("Unexpected exception initializing " + CloudCommodoreManager.class.getSimpleName(), e);
        }
    }

    @Override
    @API(status=API.Status.STABLE, since="2.0.0")
    public boolean hasBrigadierManager() {
        return this.commandRegistrationHandler() instanceof CloudCommodoreManager;
    }

    @Override
    @API(status=API.Status.STABLE, since="2.0.0")
    public @NonNull CloudBrigadierManager<C, ?> brigadierManager() {
        if (this.commandRegistrationHandler() instanceof CloudCommodoreManager) {
            return ((CloudCommodoreManager)this.commandRegistrationHandler()).brigadierManager();
        }
        throw new BrigadierManagerHolder.BrigadierManagerNotPresent("The CloudBrigadierManager is either not supported in the current environment, or it is not enabled.");
    }

    private void registerDefaultExceptionHandlers() {
        this.registerDefaultExceptionHandlers(triplet -> this.senderMapper().reverse(((CommandContext)triplet.first()).sender()).sendMessage(ChatColor.RED + ((CommandContext)triplet.first()).formatCaption((Caption)triplet.second(), (List)triplet.third())), pair -> this.owningPlugin().getLogger().log(Level.SEVERE, (String)pair.first(), (Throwable)pair.second()));
    }

    final void lockIfBrigadierCapable() {
        if (this.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            this.lockRegistration();
        }
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static final class InitializationException
    extends IllegalStateException {
        @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
        public InitializationException(String message, @Nullable Throwable cause) {
            super(message, cause);
        }
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static final class BrigadierInitializationException
    extends IllegalStateException {
        @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
        public BrigadierInitializationException(@NonNull String reason) {
            super(reason);
        }

        @API(status=API.Status.INTERNAL, consumers={"com.bergerkiller.bukkit.common.dep.cloud.*"})
        public BrigadierInitializationException(@NonNull String reason, @Nullable Throwable cause) {
            super(reason, cause);
        }
    }
}

