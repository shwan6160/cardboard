/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.logic.PlayerFileDataHandler;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.players.PlayerListHandle;
import com.bergerkiller.generated.net.minecraft.util.ProblemReporterHandle;
import com.bergerkiller.generated.net.minecraft.world.level.storage.ValueInputHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;
import java.util.Optional;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

class PlayerFileDataHandler_1_21_6
extends PlayerFileDataHandler {
    private final FastMethod<Object> getServerRegistryAccess = new FastMethod();
    private final FastField<Object> playerListFileDataField;

    public PlayerFileDataHandler_1_21_6() {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.server.level.ServerLevel");
        resolver.setVariable("version", Common.MC_VERSION);
        if (Common.IS_PAPERSPIGOT_SERVER) {
            resolver.setVariable("paper", "true");
        }
        MethodDeclaration getServerRegistryAccessMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess("public static net.minecraft.core.RegistryAccess getServerRegistryAccess() {\n    return org.bukkit.craftbukkit.CraftRegistry.getMinecraftRegistry();\n}", resolver));
        this.getServerRegistryAccess.init(getServerRegistryAccessMethod);
        this.getServerRegistryAccess.forceInitialization();
        String fieldName = "playerIo";
        Class<?> playerFileDataType = CommonUtil.getClass("net.minecraft.world.level.storage.PlayerDataStorage");
        String realFieldName = Resolver.resolveFieldName(PlayerListHandle.T.getType(), fieldName);
        this.playerListFileDataField = (FastField)LogicUtil.unsafeCast(SafeField.create(PlayerListHandle.T.getType(), realFieldName, playerFileDataType).getFastField());
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void forceInitialization() {
        this.playerListFileDataField.forceInitialization();
    }

    @Override
    public PlayerDataController get() {
        PlayerFileDataHook hook = this.update(HookAction.GET);
        return hook == null ? null : hook.controller;
    }

    @Override
    public PlayerFileDataHandler.Hook hook(PlayerDataController controller) {
        PlayerFileDataHook hook = this.update(HookAction.HOOK);
        if (hook != null) {
            hook.controller = controller;
        }
        return hook;
    }

    @Override
    public PlayerFileDataHandler.Hook mock(PlayerDataController controller) {
        return this.update(HookAction.MOCK);
    }

    @Override
    public void unhook(PlayerFileDataHandler.Hook hook, PlayerDataController controller) {
        if (hook instanceof PlayerFileDataHook) {
            PlayerFileDataHook p_hook = (PlayerFileDataHook)hook;
            if (p_hook.controller == controller) {
                this.update(HookAction.UNHOOK);
            }
        }
    }

    @Override
    public CommonTagCompound migratePlayerData(CommonTagCompound playerProfileData) {
        Object playerList = CBCraftServer.getPlayerList.invoke(Bukkit.getServer(), new Object[0]);
        return PlayerListHandle.createHandle(playerList).migratePlayerData(playerProfileData);
    }

    public PlayerFileDataHook update(HookAction action) {
        Object playerList = CBCraftServer.getPlayerList.invoke(Bukkit.getServer(), new Object[0]);
        Object playerFileData = this.playerListFileDataField.get(playerList);
        PlayerFileDataHook hook = PlayerFileDataHook.get(playerFileData, PlayerFileDataHook.class);
        if (action == HookAction.GET) {
            return hook;
        }
        if (hook == null && action != HookAction.UNHOOK) {
            hook = new PlayerFileDataHook();
            hook.setHandler(this);
            if (action == HookAction.MOCK) {
                hook.mock(playerFileData);
            } else {
                this.playerListFileDataField.set(playerList, hook.hook(playerFileData));
            }
        } else if (hook != null && action == HookAction.UNHOOK) {
            this.playerListFileDataField.set(playerList, PlayerFileDataHook.unhook(playerFileData));
            hook = new PlayerFileDataHook();
            hook.setHandler(this);
            hook.mock(playerFileData);
        }
        return hook;
    }

    public static enum HookAction {
        HOOK,
        UNHOOK,
        MOCK,
        GET;

    }

    @ClassHook.HookPackage(value="net.minecraft.server")
    @ClassHook.HookImportList(value={@ClassHook.HookImport(value="net.minecraft.world.level.storage.ValueInput"), @ClassHook.HookImport(value="net.minecraft.world.level.storage.ValueOutput"), @ClassHook.HookImport(value="net.minecraft.util.ProblemReporter"), @ClassHook.HookImport(value="net.minecraft.core.RegistryAccess"), @ClassHook.HookImport(value="net.minecraft.nbt.CompoundTag")})
    @ClassHook.HookLoadVariables(value="com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
    protected static class PlayerFileDataHook
    extends ClassHook<PlayerFileDataHook>
    implements PlayerFileDataHandler.Hook {
        public PlayerDataController controller = null;
        private PlayerFileDataHandler_1_21_6 handler = null;
        private final ThreadLocal<LocalCallState> activeCallState = new ThreadLocal();

        protected PlayerFileDataHook() {
        }

        private void setHandler(PlayerFileDataHandler_1_21_6 handler) {
            this.handler = handler;
            ((PlayerFileDataHook)this.base).handler = handler;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @ClassHook.HookMethodCondition(value="paper")
        @ClassHook.HookMethod(value="public Optional<CompoundTag> load(String name, String uuid, ProblemReporter problemReporter)")
        public Optional<Object> loadOfflinePaper(String name, String uuid, Object problemReporter) {
            if (this.controller != null) {
                CommonTagCompound compound = null;
                try {
                    this.activeCallState.set(new LocalCallState(problemReporter, null));
                    compound = this.controller.onLoadOffline(name, uuid);
                }
                catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoadOffline() on " + this.controller, t);
                }
                finally {
                    this.activeCallState.remove();
                }
                return compound == null ? Optional.empty() : Optional.of(compound.getRawHandle());
            }
            return ((PlayerFileDataHook)this.base).loadOfflinePaper(name, uuid, problemReporter);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @ClassHook.HookMethodCondition(value="!paper")
        @ClassHook.HookMethod(value="public Optional<ValueInput> load(String name, String uuid, ProblemReporter problemreporter, RegistryAccess registryAccess)")
        public Optional<Object> loadOfflineSpigot(String name, String uuid, Object problemReporter, Object registryAccess) {
            if (this.controller != null) {
                CommonTagCompound compound = null;
                try {
                    this.activeCallState.set(new LocalCallState(problemReporter, registryAccess));
                    compound = this.controller.onLoadOffline(name, uuid);
                }
                catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoadOffline() on " + this.controller, t);
                }
                finally {
                    this.activeCallState.remove();
                }
                return compound == null ? Optional.empty() : Optional.of(ValueInputHandle.forNBT(problemReporter, registryAccess, compound).getRaw());
            }
            return ((PlayerFileDataHook)this.base).loadOfflineSpigot(name, uuid, problemReporter, registryAccess);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @ClassHook.HookMethod(value="public java.util.Optional<ValueInput> load(net.minecraft.world.entity.player.Player entityhuman, ProblemReporter problemreporter)")
        public Optional<Object> load(Object entityHuman, Object problemReporter) {
            Player player;
            if (this.controller != null && (player = LogicUtil.tryCast(WrapperConversion.toEntity(entityHuman), Player.class)) != null) {
                CommonTagCompound compound = null;
                try {
                    this.activeCallState.set(new LocalCallState(problemReporter, null));
                    compound = this.controller.onLoad(player);
                }
                catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoad() on " + this.controller, t);
                }
                finally {
                    this.activeCallState.remove();
                }
                return compound == null ? Optional.empty() : Optional.of(ValueInputHandle.forNBTOnWorld(problemReporter, player.getWorld(), compound).getRaw());
            }
            return ((PlayerFileDataHook)this.base).load(entityHuman, problemReporter);
        }

        @ClassHook.HookMethod(value="public abstract void save(net.minecraft.world.entity.player.Player paramEntityHuman)")
        public void save(Object entityHuman) {
            Player player;
            if (this.controller != null && (player = LogicUtil.tryCast(WrapperConversion.toEntity(entityHuman), Player.class)) != null) {
                try {
                    this.controller.onSave(player);
                }
                catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to handle onSave() on " + this.controller, t);
                }
                return;
            }
            ((PlayerFileDataHook)this.base).save(entityHuman);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CommonTagCompound base_load(HumanEntity human) {
            LocalCallState localCallState = this.activeCallState.get();
            if (localCallState != null) {
                return this.base_load_with_reporter(human, localCallState.problemReporter);
            }
            try (ProblemReporterHandle problemReporter = ProblemReporterHandle.createScoped();){
                CommonTagCompound commonTagCompound = this.base_load_with_reporter(human, problemReporter.getRaw());
                return commonTagCompound;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public CommonTagCompound base_load_offline(String playerName, String playerUUID) {
            LocalCallState localCallState = this.activeCallState.get();
            if (localCallState != null) {
                return this.base_load_offline_with_reporter(playerName, playerUUID, localCallState.problemReporter, localCallState.registryAccess);
            }
            try (ProblemReporterHandle problemReporter = ProblemReporterHandle.createScoped();){
                CommonTagCompound commonTagCompound = this.base_load_offline_with_reporter(playerName, playerUUID, problemReporter.getRaw(), null);
                return commonTagCompound;
            }
        }

        @Override
        public void base_save(HumanEntity human) {
            ((PlayerFileDataHook)this.base).save(HandleConversion.toEntityHandle((Entity)human));
        }

        private CommonTagCompound base_load_with_reporter(HumanEntity human, Object problemReporter) {
            return ((PlayerFileDataHook)this.base).load(HandleConversion.toEntityHandle((Entity)human), problemReporter).map(ValueInputHandle::createHandle).map(ValueInputHandle::asNBT).orElse(null);
        }

        private CommonTagCompound base_load_offline_with_reporter(String name, String uuid, Object problemReporter, Object registryAccess) {
            if (Common.IS_PAPERSPIGOT_SERVER) {
                return ((PlayerFileDataHook)this.base).loadOfflinePaper(name, uuid, problemReporter).map(ValueInputHandle::createHandle).map(ValueInputHandle::asNBT).orElse(null);
            }
            if (registryAccess == null) {
                registryAccess = this.handler.getServerRegistryAccess.invoke(null);
            }
            return ((PlayerFileDataHook)this.base).loadOfflineSpigot(name, uuid, problemReporter, registryAccess).map(ValueInputHandle::createHandle).map(ValueInputHandle::asNBT).orElse(null);
        }

        private static class LocalCallState {
            public final Object problemReporter;
            public final Object registryAccess;

            public LocalCallState(Object problemReporter, Object registryAccess) {
                this.problemReporter = problemReporter;
                this.registryAccess = registryAccess;
            }
        }
    }
}

