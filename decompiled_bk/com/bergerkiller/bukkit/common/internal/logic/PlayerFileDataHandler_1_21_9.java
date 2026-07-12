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
import com.bergerkiller.mountiplex.reflection.ClassHook;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastConstructor;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

class PlayerFileDataHandler_1_21_9
extends PlayerFileDataHandler {
    private final FastField<Object> playerListFileDataField;
    private final FastMethod<UUID> nameAndId_getId = new FastMethod();
    private final FastMethod<String> nameAndId_getName = new FastMethod();
    private final FastConstructor<Object> nameAndId_ctor_id_name = new FastConstructor();

    public PlayerFileDataHandler_1_21_9() throws Exception {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClassName("net.minecraft.server.level.ServerLevel");
        resolver.setVariable("version", Common.MC_VERSION);
        if (Common.IS_PAPERSPIGOT_SERVER) {
            resolver.setVariable("paper", "true");
        }
        String fieldName = "playerIo";
        Class<?> playerFileDataType = CommonUtil.getClass("net.minecraft.world.level.storage.PlayerDataStorage");
        String realFieldName = Resolver.resolveFieldName(PlayerListHandle.T.getType(), fieldName);
        this.playerListFileDataField = (FastField)LogicUtil.unsafeCast(SafeField.create(PlayerListHandle.T.getType(), realFieldName, playerFileDataType).getFastField());
        Class<?> nameAndIdType = CommonUtil.getClass("net.minecraft.server.players.NameAndId");
        if (nameAndIdType == null) {
            throw new UnsupportedOperationException("NameAndId class not found");
        }
        this.nameAndId_getId.init(Resolver.resolveAndGetDeclaredMethod(nameAndIdType, "id", new Class[0]));
        this.nameAndId_getName.init(Resolver.resolveAndGetDeclaredMethod(nameAndIdType, "name", new Class[0]));
        this.nameAndId_ctor_id_name.init(nameAndIdType.getConstructor(UUID.class, String.class));
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
    @ClassHook.HookImportList(value={@ClassHook.HookImport(value="net.minecraft.server.players.NameAndId"), @ClassHook.HookImport(value="net.minecraft.nbt.CompoundTag")})
    @ClassHook.HookLoadVariables(value="com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER")
    protected static class PlayerFileDataHook
    extends ClassHook<PlayerFileDataHook>
    implements PlayerFileDataHandler.Hook {
        public PlayerDataController controller = null;
        private PlayerFileDataHandler_1_21_9 handler = null;

        protected PlayerFileDataHook() {
        }

        private void setHandler(PlayerFileDataHandler_1_21_9 handler) {
            this.handler = handler;
            ((PlayerFileDataHook)this.base).handler = handler;
        }

        @ClassHook.HookMethod(value="public Optional<CompoundTag> load(NameAndId nameandid)")
        public Optional<Object> loadOffline(Object nameAndId) {
            if (this.controller != null) {
                CommonTagCompound compound = null;
                UUID uuid = (UUID)this.handler.nameAndId_getId.invoke(nameAndId);
                String name = (String)this.handler.nameAndId_getName.invoke(nameAndId);
                try {
                    compound = this.controller.onLoadOffline(name, uuid.toString());
                }
                catch (Throwable t) {
                    Logging.LOGGER.log(Level.SEVERE, "Failed to handle onLoadOffline() on " + this.controller, t);
                }
                return compound == null ? Optional.empty() : Optional.of(compound.getRawHandle());
            }
            return ((PlayerFileDataHook)this.base).loadOffline(nameAndId);
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

        @Override
        public CommonTagCompound base_load(HumanEntity human) {
            throw new UnsupportedOperationException("Not supported on 1.21.9+");
        }

        @Override
        public CommonTagCompound base_load_offline(String playerName, String playerUUIDStr) {
            UUID playerUUID;
            try {
                playerUUID = UUID.fromString(playerUUIDStr);
            }
            catch (IllegalArgumentException ex) {
                return null;
            }
            Object nameAndId = this.handler.nameAndId_ctor_id_name.newInstance(playerUUID, playerName);
            return ((PlayerFileDataHook)this.base).loadOffline(nameAndId).map(CommonTagCompound::create).orElse(null);
        }

        @Override
        public void base_save(HumanEntity human) {
            ((PlayerFileDataHook)this.base).save(HandleConversion.toEntityHandle((Entity)human));
        }
    }
}

