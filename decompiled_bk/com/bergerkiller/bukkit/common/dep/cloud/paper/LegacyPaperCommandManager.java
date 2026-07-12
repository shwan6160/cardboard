/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.bergerkiller.bukkit.common.dep.cloud.paper;

import com.bergerkiller.bukkit.common.dep.cloud.SenderMapper;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.BrigadierManagerHolder;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.BrigadierSetting;
import com.bergerkiller.bukkit.common.dep.cloud.brigadier.CloudBrigadierManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.BukkitCommandManager;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.CloudBukkitCapabilities;
import com.bergerkiller.bukkit.common.dep.cloud.bukkit.internal.CraftBukkitReflection;
import com.bergerkiller.bukkit.common.dep.cloud.execution.ExecutionCoordinator;
import com.bergerkiller.bukkit.common.dep.cloud.paper.LegacyPaperBrigadier;
import com.bergerkiller.bukkit.common.dep.cloud.paper.ModernPaperBrigadier;
import com.bergerkiller.bukkit.common.dep.cloud.paper.PaperCommandPreprocessor;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.SuggestionListener;
import com.bergerkiller.bukkit.common.dep.cloud.paper.suggestion.SuggestionListenerFactory;
import com.bergerkiller.bukkit.common.dep.cloud.state.RegistrationState;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LegacyPaperCommandManager<C>
extends BukkitCommandManager<C> {
    private @Nullable BrigadierManagerHolder<C, ?> brigadierManagerHolder = null;

    @API(status=API.Status.STABLE, since="2.0.0")
    public LegacyPaperCommandManager(@NonNull Plugin owningPlugin, @NonNull ExecutionCoordinator<C> commandExecutionCoordinator, @NonNull SenderMapper<CommandSender, C> senderMapper) throws BukkitCommandManager.InitializationException {
        super(owningPlugin, commandExecutionCoordinator, senderMapper);
        this.registerCommandPreProcessor(new PaperCommandPreprocessor(this, this.senderMapper(), Function.identity()));
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static @NonNull LegacyPaperCommandManager<@NonNull CommandSender> createNative(@NonNull Plugin owningPlugin, @NonNull ExecutionCoordinator<CommandSender> commandExecutionCoordinator) throws BukkitCommandManager.InitializationException {
        return new LegacyPaperCommandManager<CommandSender>(owningPlugin, commandExecutionCoordinator, SenderMapper.identity());
    }

    @Override
    public synchronized void registerBrigadier() throws BukkitCommandManager.BrigadierInitializationException {
        this.registerBrigadier(true);
    }

    @Deprecated
    public synchronized void registerLegacyPaperBrigadier() throws BukkitCommandManager.BrigadierInitializationException {
        this.registerBrigadier(false);
    }

    private void registerBrigadier(boolean allowModern) {
        this.requireState(RegistrationState.BEFORE_REGISTRATION);
        this.checkBrigadierCompatibility();
        if (this.brigadierManagerHolder != null) {
            throw new IllegalStateException("Brigadier is already registered! Holder: " + this.brigadierManagerHolder);
        }
        if (!this.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            super.registerBrigadier();
        } else {
            if (allowModern && CraftBukkitReflection.classExists("io.papermc.paper.command.brigadier.CommandSourceStack")) {
                try {
                    ModernPaperBrigadier brig = new ModernPaperBrigadier(CommandSender.class, this, this.senderMapper(), () -> this.lockRegistration());
                    this.brigadierManagerHolder = brig;
                    brig.registerPlugin(this.owningPlugin());
                    this.commandRegistrationHandler(brig);
                }
                catch (Exception e) {
                    throw new BukkitCommandManager.BrigadierInitializationException("Failed to register ModernPaperBrigadier", e);
                }
            }
            try {
                this.brigadierManagerHolder = new LegacyPaperBrigadier(this);
                Bukkit.getPluginManager().registerEvents((Listener)this.brigadierManagerHolder, this.owningPlugin());
                this.brigadierManagerHolder.brigadierManager().settings().set(BrigadierSetting.FORCE_EXECUTABLE, true);
            }
            catch (Exception e) {
                throw new BukkitCommandManager.BrigadierInitializationException("Failed to register LegacyPaperBrigadier", e);
            }
        }
    }

    @Override
    @API(status=API.Status.STABLE, since="2.0.0")
    public boolean hasBrigadierManager() {
        return this.brigadierManagerHolder != null || super.hasBrigadierManager();
    }

    @Override
    @API(status=API.Status.STABLE, since="2.0.0")
    public @NonNull CloudBrigadierManager<C, ?> brigadierManager() {
        if (this.brigadierManagerHolder != null) {
            return this.brigadierManagerHolder.brigadierManager();
        }
        return super.brigadierManager();
    }

    public void registerAsynchronousCompletions() throws IllegalStateException {
        this.requireState(RegistrationState.BEFORE_REGISTRATION);
        if (!this.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            throw new IllegalStateException("Failed to register asynchronous command completion listener.");
        }
        SuggestionListenerFactory suggestionListenerFactory = SuggestionListenerFactory.create(this);
        SuggestionListener suggestionListener = suggestionListenerFactory.createListener();
        Bukkit.getServer().getPluginManager().registerEvents(suggestionListener, this.owningPlugin());
    }
}

