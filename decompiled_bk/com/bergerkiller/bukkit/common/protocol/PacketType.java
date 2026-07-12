/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.SynchedEntityDataHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.TranslatorFieldAccessor;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;
import com.bergerkiller.reflection.net.minecraft.server.NMSPacketClasses;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class PacketType
extends ClassTemplate<Object> {
    private static Map<Class<?>, PacketTypeOptions> typesByPacketClassVisible;
    private static final ClassMap<PacketTypeOptions> typesByPacketClass;
    private static final PacketTypeOptions NO_TYPE_OPTIONS;
    public static final NMSPacketClasses.NMSPacket DEFAULT;
    public static final NMSPacketClasses.NMSClientboundPlayerAbilitiesPacket OUT_ABILITIES;
    public static final NMSPacketClasses.NMSClientboundUpdateAdvancementsPacket OUT_ADVANCEMENTS;
    public static final NMSPacketClasses.NMSClientboundBlockEventPacket OUT_BLOCK_ACTION;
    public static final NMSPacketClasses.NMSClientboundTakeItemEntityPacket OUT_COLLECT;
    public static final NMSPacketClasses.NMSClientboundCustomPayloadPacket OUT_CUSTOM_PAYLOAD;
    public static final NMSPacketClasses.NMSClientboundSetExperiencePacket OUT_EXPERIENCE;
    public static final NMSPacketClasses.NMSClientboundExplodePacket OUT_EXPLOSION;
    public static final NMSPacketClasses.NMSClientboundGameEventPacket OUT_GAME_STATE_CHANGE;
    public static final NMSPacketClasses.NMSClientboundSetHeldSlotPacket OUT_HELD_ITEM_SLOT;
    public static final NMSPacketClasses.NMSClientboundKeepAlivePacket OUT_KEEP_ALIVE;
    public static final NMSPacketClasses.NMSClientboundDisconnectPacket OUT_KICK_DISCONNECT;
    public static final NMSPacketClasses.NMSClientboundLoginPacket OUT_LOGIN;
    public static final NMSPacketClasses.NMSClientboundMapItemDataPacket OUT_MAP;
    public static final NMSPacketClasses.NMSClientboundLevelChunkWithLightPacket OUT_MAP_CHUNK;
    public static final NMSPacketClasses.NMSClientboundTabListPacket OUT_PLAYER_LIST_HEADER_FOOTER;
    public static final NMSPacketClasses.NMSClientboundSoundPacket OUT_NAMED_SOUND_EFFECT;
    public static final NMSPacketClasses.NMSClientboundOpenSignEditorPacket OUT_OPEN_SIGN_EDITOR;
    public static final NMSPacketClasses.NMSClientboundPlayerInfoUpdatePacket OUT_PLAYER_INFO_UPDATE;
    public static final NMSPacketClasses.NMSClientboundPlayerInfoRemovePacket OUT_PLAYER_INFO_REMOVE;
    public static final NMSPacketClasses.NMSClientboundPlayerPositionPacket OUT_POSITION;
    public static final NMSPacketClasses.NMSClientboundPlayerRotationPacket OUT_ROTATION;
    public static final NMSPacketClasses.NMSClientboundRespawnPacket OUT_RESPAWN;
    public static final NMSPacketClasses.NMSClientboundSetDefaultSpawnPositionPacket OUT_SPAWN_POSITION;
    public static final NMSPacketClasses.NMSClientboundAwardStatsPacket OUT_STATISTIC;
    public static final NMSPacketClasses.NMSClientboundCommandSuggestionsPacket OUT_TAB_COMPLETE;
    public static final NMSPacketClasses.NMSClientboundBlockEntityDataPacket OUT_TILE_ENTITY_DATA;
    public static final NMSPacketClasses.NMSClientboundSetHealthPacket OUT_UPDATE_HEALTH;
    public static final NMSPacketClasses.NMSClientboundSetTimePacket OUT_UPDATE_TIME;
    public static final NMSPacketClasses.NMSClientboundLevelEventPacket OUT_WORLD_EVENT;
    public static final NMSPacketClasses.NMSClientboundLevelParticlesPacket OUT_WORLD_PARTICLES;
    public static final NMSPacketClasses.NMSClientboundBlockDestructionPacket OUT_BLOCK_BREAK_ANIMATION;
    public static final NMSPacketClasses.NMSClientboundBlockUpdatePacket OUT_BLOCK_CHANGE;
    public static final NMSPacketClasses.NMSClientboundBossEventPacket OUT_BOSS;
    public static final NMSPacketClasses.NMSClientboundSetCameraPacket OUT_CAMERA;
    public static final NMSPacketClasses.NMSClientboundCustomSoundPacket OUT_CUSTOM_SOUND_EFFECT;
    public static final NMSPacketClasses.NMSClientboundResourcePackPushPacket OUT_RESOURCE_PACK_PUSH;
    public static final NMSPacketClasses.NMSClientboundResourcePackPopPacket OUT_RESOURCE_PACK_POP;
    public static final NMSPacketClasses.NMSClientboundChangeDifficultyPacket OUT_SERVER_DIFFICULTY;
    public static final NMSPacketClasses.NMSClientboundCooldownPacket OUT_SET_COOLDOWN;
    public static final NMSPacketClasses.NMSClientboundForgetLevelChunkPacket OUT_UNLOAD_CHUNK;
    public static final NMSPacketClasses.NMSClientboundSetDisplayObjectivePacket OUT_SCOREBOARD_DISPLAY_OBJECTIVE;
    public static final NMSPacketClasses.NMSClientboundSetObjectivePacket OUT_SCOREBOARD_OBJECTIVE;
    public static final NMSPacketClasses.NMSClientboundSetScorePacket OUT_SCOREBOARD_SCORE;
    public static final NMSPacketClasses.NMSClientboundResetScorePacket OUT_SCOREBOARD_SCORE_RESET;
    public static final NMSPacketClasses.NMSClientboundSetPlayerTeamPacket OUT_SCOREBOARD_TEAM;
    public static final NMSPacketClasses.NMSClientboundContainerClosePacket OUT_WINDOW_CLOSE;
    public static final NMSPacketClasses.NMSClientboundContainerSetDataPacket OUT_WINDOW_DATA;
    public static final NMSPacketClasses.NMSClientboundOpenScreenPacket OUT_WINDOW_OPEN;
    public static final NMSPacketClasses.NMSClientboundContainerSetSlotPacket OUT_WINDOW_SET_SLOT;
    public static final NMSPacketClasses.NMSClientboundContainerSetContentPacket OUT_WINDOW_ITEMS;
    public static final NMSPacketClasses.NMSClientboundAddEntityPacket OUT_ENTITY_SPAWN;
    public static final NMSPacketClasses.NMSClientboundAddPlayerPacket OUT_ENTITY_SPAWN_NAMED;
    public static final NMSPacketClasses.NMSClientboundAddExperienceOrbPacket OUT_ENTITY_SPAWN_EXPORB;
    public static final NMSPacketClasses.NMSClientboundAddMobPacket OUT_ENTITY_SPAWN_LIVING;
    public static final NMSPacketClasses.NMSClientboundAddPaintingPacket OUT_ENTITY_SPAWN_PAINTING;
    public static final NMSPacketClasses.NMSPacketPlayOutSpawnEntityWeather OUT_ENTITY_SPAWN_WITHER;
    public static final NMSPacketClasses.NMSClientboundRemoveEntitiesPacket OUT_ENTITY_DESTROY;
    public static final NMSPacketClasses.NMSClientboundSetEntityLinkPacket OUT_ENTITY_ATTACH;
    public static final NMSPacketClasses.NMSClientboundUpdateMobEffectPacket OUT_ENTITY_EFFECT_ADD;
    public static final NMSPacketClasses.NMSClientboundRemoveMobEffectPacket OUT_ENTITY_EFFECT_REMOVE;
    public static final NMSPacketClasses.NMSClientboundSetEquipmentPacket OUT_ENTITY_EQUIPMENT;
    public static final NMSPacketClasses.NMSClientboundRotateHeadPacket OUT_ENTITY_HEAD_ROTATION;
    public static final NMSPacketClasses.NMSClientboundMoveEntityPacketRot OUT_ENTITY_LOOK;
    public static final NMSPacketClasses.NMSClientboundAnimatePacket OUT_ENTITY_ANIMATION;
    public static final NMSPacketClasses.NMSClientboundSetEntityDataPacket OUT_ENTITY_METADATA;
    public static final NMSPacketClasses.NMSClientboundEntityEventPacket OUT_ENTITY_STATUS;
    public static final NMSPacketClasses.NMSClientboundEntityPositionSyncPacket OUT_ENTITY_TELEPORT;
    public static final NMSPacketClasses.NMSClientboundSetEntityMotionPacket OUT_ENTITY_VELOCITY;
    public static final NMSPacketClasses.NMSClientboundMoveEntityPacketPos OUT_ENTITY_MOVE;
    public static final NMSPacketClasses.NMSClientboundMoveEntityPacketPosRot OUT_ENTITY_MOVE_LOOK;
    public static final NMSPacketClasses.NMSClientboundUpdateAttributesPacket OUT_ENTITY_UPDATE_ATTRIBUTES;
    public static final NMSPacketClasses.NMSClientboundSetPassengersPacket OUT_MOUNT;
    public static final NMSPacketClasses.NMSClientboundMoveVehiclePacket OUT_VEHICLE_MOVE;
    public static final NMSPacketClasses.NMSPacketPlayOutUpdateSign OUT_UPDATE_SIGN;
    public static final NMSPacketClasses.NMSClientboundBundlePacket OUT_BUNDLE;
    public static final NMSPacketClasses.NMSServerboundPlayerAbilitiesPacket IN_ABILITIES;
    public static final NMSPacketClasses.NMSServerboundPaddleBoatPacket IN_BOAT_MOVE;
    public static final NMSPacketClasses.NMSServerboundChatPacket IN_CHAT;
    public static final NMSPacketClasses.NMSServerboundClientCommandPacket IN_CLIENT_COMMAND;
    public static final NMSPacketClasses.NMSServerboundCustomPayloadPacket IN_CUSTOM_PAYLOAD;
    public static final NMSPacketClasses.NMSServerboundPlayerCommandPacket IN_PLAYER_COMMAND;
    public static final NMSPacketClasses.NMSServerboundMovePlayerPacketRot IN_LOOK;
    public static final NMSPacketClasses.NMSServerboundMovePlayerPacketPos IN_POSITION;
    public static final NMSPacketClasses.NMSServerboundMovePlayerPacketPosRot IN_POSITION_LOOK;
    public static final NMSPacketClasses.NMSServerboundSetCarriedItemPacket IN_HELD_ITEM_SLOT;
    public static final NMSPacketClasses.NMSServerboundKeepAlivePacket IN_KEEP_ALIVE;
    public static final NMSPacketClasses.NMSServerboundTeleportToEntityPacket IN_SPECTATE;
    public static final NMSPacketClasses.NMSServerboundSetCreativeModeSlotPacket IN_SET_CREATIVE_SLOT;
    public static final NMSPacketClasses.NMSServerboundClientInformationPacket IN_SETTINGS;
    public static final NMSPacketClasses.NMSServerboundPlayerInputPacket IN_STEER_VEHICLE;
    public static final NMSPacketClasses.NMSServerboundCommandSuggestionPacket IN_TAB_COMPLETE;
    public static final NMSPacketClasses.NMSServerboundAcceptTeleportationPacket IN_TELEPORT_ACCEPT;
    public static final NMSPacketClasses.NMSServerboundSignUpdatePacket IN_UPDATE_SIGN;
    public static final NMSPacketClasses.NMSServerboundPlayerActionPacket IN_PLAYER_ACTION;
    public static final NMSPacketClasses.NMSServerboundSwingPacket IN_SWING;
    public static final NMSPacketClasses.NMSServerboundInteractPacket IN_INTERACT;
    public static final NMSPacketClasses.NMSServerboundAttackPacket IN_ATTACK;
    public static final NMSPacketClasses.NMSServerboundUseItemPacket IN_USE_ITEM;
    public static final NMSPacketClasses.NMSServerboundUseItemOnPacket IN_USE_ITEM_ON;
    public static final NMSPacketClasses.NMSServerboundMoveVehiclePacket IN_VEHICLE_MOVE;
    public static final NMSPacketClasses.NMSServerboundClientTickEndPacket IN_CLIENT_TICK_END;
    public static final NMSPacketClasses.NMSServerboundContainerClosePacket IN_WINDOW_CLOSE;
    public static final NMSPacketClasses.NMSServerboundContainerButtonClickPacket IN_WINDOW_ENCHANT_ITEM;
    public static final NMSPacketClasses.NMSServerboundResourcePackPacket IN_WINDOW_RESOURCEPACK_STATUS;
    public static final NMSPacketClasses.NMSServerboundContainerClickPacket IN_WINDOW_CLICK;
    private final String name;
    private final boolean outgoing;
    private final FieldAccessor<DataWatcher> dataWatcherField;

    public PacketType() {
        this((String)null);
    }

    public void init() {
    }

    protected PacketType(String packetClassName) {
        this(packetClassName, null);
    }

    protected PacketType(Class<?> packetClass) {
        this(packetClass.getSimpleName(), packetClass);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    protected PacketType(String packetClassName, Class<?> packetClass) {
        String string = this.name = packetClassName != null ? packetClassName : this.getClass().getSimpleName().substring(3);
        if (packetClass == null) {
            if (this.name.equals("Packet")) {
                packetClass = CommonUtil.getClass("net.minecraft.network.protocol." + this.name);
            } else {
                packetClass = CommonUtil.getClass("net.minecraft.network.protocol.game." + this.name);
                if (packetClass == null) {
                    packetClass = CommonUtil.getClass("net.minecraft.network.protocol.common." + this.name);
                }
            }
        }
        if (packetClass == null) {
            this.outgoing = false;
            this.dataWatcherField = null;
            return;
        }
        if (!packetClass.equals(PacketHandle.T.getType())) {
            Class<PacketType> clazz = PacketType.class;
            // MONITORENTER : com.bergerkiller.bukkit.common.protocol.PacketType.class
            typesByPacketClass.put(packetClass, typesByPacketClass.getOrDefault(packetClass, NO_TYPE_OPTIONS).add(this));
            typesByPacketClassVisible = new HashMap(typesByPacketClass.getData());
            // MONITOREXIT : clazz
        }
        this.setClass(packetClass);
        this.addImport("net.minecraft.network.protocol.game.*");
        this.outgoing = PacketType.isPacketOutgoing(this.getType());
        TranslatorFieldAccessor<DataWatcher> dataWatcherField = null;
        for (SafeField<Object> safeField : this.getFields()) {
            if (!SynchedEntityDataHandle.T.isType(safeField.getType())) continue;
            dataWatcherField = safeField.translate(DuplexConversion.dataWatcher);
            break;
        }
        this.dataWatcherField = dataWatcherField;
    }

    public boolean isOutGoing() {
        return this.outgoing;
    }

    @Override
    public String toString() {
        return this.name;
    }

    protected boolean matchPacket(Object packetHandle) {
        return true;
    }

    public void preprocess(Object packetHandle) {
    }

    public FieldAccessor<DataWatcher> getMetaDataField() {
        if (this.dataWatcherField == null) {
            throw new IllegalArgumentException("MetaData field does not exist");
        }
        return this.dataWatcherField;
    }

    public static PacketType getType(Object packetHandle) {
        Class<?> packetHandleType;
        try {
            packetHandleType = packetHandle.getClass();
        }
        catch (NullPointerException ex) {
            if (packetHandle == null) {
                throw new IllegalArgumentException("Input packet is null");
            }
            throw ex;
        }
        return LogicUtil.synchronizeCopyOnWrite(PacketType.class, () -> typesByPacketClassVisible.getOrDefault(packetHandleType, NO_TYPE_OPTIONS).find(packetHandle), () -> {
            PacketTypeOptions options = typesByPacketClass.getOrDefault(packetHandleType, NO_TYPE_OPTIONS);
            PacketType type = options.find(packetHandle);
            if (type == null) {
                return new PacketType(packetHandleType);
            }
            HashMap newMap = new HashMap(typesByPacketClassVisible);
            newMap.put(packetHandleType, options);
            typesByPacketClassVisible = newMap;
            return type;
        });
    }

    private static boolean isPacketOutgoing(Class<?> packetClass) {
        if (packetClass == null) {
            return false;
        }
        try {
            if (CommonBootstrap.evaluateMCVersion(">=", "1.20.5")) {
                return PacketType.isPacketOutgoing_1_20_5(packetClass);
            }
            return PacketType.isPacketOutgoing_1_8_to_1_20_4(packetClass);
        }
        catch (Throwable t) {
            Logging.LOGGER_NETWORK.log(Level.SEVERE, "Failed to determine outgoing for packet " + packetClass, t);
            return false;
        }
    }

    private static boolean isPacketOutgoing_1_20_5(Class<?> packetClass) throws Throwable {
        String name = MPLType.getName(packetClass);
        if (name.contains("Clientbound") || name.contains("PacketPlayOut")) {
            return true;
        }
        if (name.contains("Serverbound") || name.contains("PacketPlayIn")) {
            return false;
        }
        if (packetClass.equals(PacketHandle.T.getType())) {
            return false;
        }
        throw new IllegalStateException("Unknown packet class name format: " + name);
    }

    private static boolean isPacketOutgoing_1_8_to_1_20_4(Class<?> packetClass) throws Throwable {
        ?[] protocols;
        Class<?> enumProtocolType = CommonUtil.getClass("net.minecraft.network.EnumProtocol");
        Class<?> enumProtocolDirectionType = CommonUtil.getClass("net.minecraft.network.protocol.EnumProtocolDirection");
        Class<?> bimapClass = CommonUtil.getClass("com.google.common.collect.BiMap");
        Field f = Resolver.resolveAndGetDeclaredField(enumProtocolDirectionType, "CLIENTBOUND");
        Object clientBoundDirection = f.get(null);
        Field flowsField = null;
        if (CommonBootstrap.evaluateMCVersion(">=", "1.17")) {
            flowsField = Resolver.resolveAndGetDeclaredField(enumProtocolType, "flows");
        } else {
            flowsField = LogicUtil.tryMake(() -> {
                Field f = enumProtocolType.getDeclaredField("packetMap");
                return Map.class.isAssignableFrom(f.getType()) ? f : null;
            }, null);
            if (flowsField == null) {
                flowsField = CommonBootstrap.evaluateMCVersion(">=", "1.10.2") ? Resolver.resolveAndGetDeclaredField(enumProtocolType, "h") : (CommonBootstrap.evaluateMCVersion(">=", "1.8.3") ? Resolver.resolveAndGetDeclaredField(enumProtocolType, "j") : Resolver.resolveAndGetDeclaredField(enumProtocolType, "h"));
            }
        }
        flowsField.setAccessible(true);
        for (Object protocol : protocols = enumProtocolType.getEnumConstants()) {
            Object directionFlows = ((Map)flowsField.get(protocol)).get(clientBoundDirection);
            if (directionFlows == null) continue;
            if (bimapClass.isAssignableFrom(directionFlows.getClass())) {
                Method containsValueMethod = bimapClass.getMethod("containsValue", Object.class);
                Boolean containsValue = (Boolean)containsValueMethod.invoke(directionFlows, packetClass);
                if (!containsValue.booleanValue()) continue;
                return true;
            }
            if (directionFlows instanceof Map) {
                if (!((Map)directionFlows).containsValue(packetClass)) continue;
                return true;
            }
            PacketSearchResult packetContained = PacketType.tryCheckPacketClassContained(directionFlows, packetClass);
            if (packetContained == PacketSearchResult.FAILED) {
                for (Field f2 : directionFlows.getClass().getDeclaredFields()) {
                    if (Modifier.isStatic(f2.getModifiers()) || f2.getType().getDeclaringClass() != enumProtocolType) continue;
                    f2.setAccessible(true);
                    Object directionFlowsSub = f2.get(directionFlows);
                    packetContained = PacketType.tryCheckPacketClassContained(directionFlowsSub, packetClass);
                    if (packetContained != PacketSearchResult.FAILED) break;
                }
            }
            if (packetContained == PacketSearchResult.FAILED) {
                throw new IllegalStateException("Unable to identify packet flow direction");
            }
            if (packetContained != PacketSearchResult.FOUND) continue;
            return true;
        }
        return false;
    }

    private static PacketSearchResult tryCheckPacketClassContained(Object flows, Class<?> packetClass) throws Throwable {
        Method getPacketIdMethod = null;
        if (flows != null) {
            block0: for (Class<?> flowType = flows.getClass(); flowType != null && flowType != Object.class; flowType = flowType.getSuperclass()) {
                for (Method m : flowType.getDeclaredMethods()) {
                    if (m.getParameterCount() != 1 || !m.getParameterTypes()[0].equals(Class.class) || m.getReturnType() != Integer.class && m.getReturnType() != Integer.TYPE) continue;
                    getPacketIdMethod = m;
                    continue block0;
                }
            }
        }
        if (getPacketIdMethod == null) {
            return PacketSearchResult.FAILED;
        }
        getPacketIdMethod.setAccessible(true);
        Integer packetId = (Integer)getPacketIdMethod.invoke(flows, packetClass);
        return packetId != null && packetId != -1 ? PacketSearchResult.FOUND : PacketSearchResult.NOT_FOUND;
    }

    static {
        CommonBootstrap.initServer();
        typesByPacketClassVisible = Collections.emptyMap();
        typesByPacketClass = new ClassMap();
        NO_TYPE_OPTIONS = new PacketTypeOptions(){

            @Override
            public PacketType firstRegistered() {
                return null;
            }

            @Override
            public PacketType find(Object packetHandle) {
                return null;
            }

            @Override
            public PacketTypeOptions add(PacketType newType) {
                return new PacketTypeOptionsSingleton(newType);
            }
        };
        DEFAULT = new NMSPacketClasses.NMSPacket();
        OUT_ABILITIES = new NMSPacketClasses.NMSClientboundPlayerAbilitiesPacket();
        OUT_ADVANCEMENTS = new NMSPacketClasses.NMSClientboundUpdateAdvancementsPacket();
        OUT_BLOCK_ACTION = new NMSPacketClasses.NMSClientboundBlockEventPacket();
        OUT_COLLECT = new NMSPacketClasses.NMSClientboundTakeItemEntityPacket();
        OUT_CUSTOM_PAYLOAD = new NMSPacketClasses.NMSClientboundCustomPayloadPacket();
        OUT_EXPERIENCE = new NMSPacketClasses.NMSClientboundSetExperiencePacket();
        OUT_EXPLOSION = new NMSPacketClasses.NMSClientboundExplodePacket();
        OUT_GAME_STATE_CHANGE = new NMSPacketClasses.NMSClientboundGameEventPacket();
        OUT_HELD_ITEM_SLOT = new NMSPacketClasses.NMSClientboundSetHeldSlotPacket();
        OUT_KEEP_ALIVE = new NMSPacketClasses.NMSClientboundKeepAlivePacket();
        OUT_KICK_DISCONNECT = new NMSPacketClasses.NMSClientboundDisconnectPacket();
        OUT_LOGIN = new NMSPacketClasses.NMSClientboundLoginPacket();
        OUT_MAP = new NMSPacketClasses.NMSClientboundMapItemDataPacket();
        OUT_MAP_CHUNK = new NMSPacketClasses.NMSClientboundLevelChunkWithLightPacket();
        OUT_PLAYER_LIST_HEADER_FOOTER = new NMSPacketClasses.NMSClientboundTabListPacket();
        OUT_NAMED_SOUND_EFFECT = new NMSPacketClasses.NMSClientboundSoundPacket();
        OUT_OPEN_SIGN_EDITOR = new NMSPacketClasses.NMSClientboundOpenSignEditorPacket();
        OUT_PLAYER_INFO_UPDATE = new NMSPacketClasses.NMSClientboundPlayerInfoUpdatePacket();
        OUT_PLAYER_INFO_REMOVE = new NMSPacketClasses.NMSClientboundPlayerInfoRemovePacket();
        OUT_POSITION = new NMSPacketClasses.NMSClientboundPlayerPositionPacket();
        OUT_ROTATION = new NMSPacketClasses.NMSClientboundPlayerRotationPacket();
        OUT_RESPAWN = new NMSPacketClasses.NMSClientboundRespawnPacket();
        OUT_SPAWN_POSITION = new NMSPacketClasses.NMSClientboundSetDefaultSpawnPositionPacket();
        OUT_STATISTIC = new NMSPacketClasses.NMSClientboundAwardStatsPacket();
        OUT_TAB_COMPLETE = new NMSPacketClasses.NMSClientboundCommandSuggestionsPacket();
        OUT_TILE_ENTITY_DATA = new NMSPacketClasses.NMSClientboundBlockEntityDataPacket();
        OUT_UPDATE_HEALTH = new NMSPacketClasses.NMSClientboundSetHealthPacket();
        OUT_UPDATE_TIME = new NMSPacketClasses.NMSClientboundSetTimePacket();
        OUT_WORLD_EVENT = new NMSPacketClasses.NMSClientboundLevelEventPacket();
        OUT_WORLD_PARTICLES = new NMSPacketClasses.NMSClientboundLevelParticlesPacket();
        OUT_BLOCK_BREAK_ANIMATION = new NMSPacketClasses.NMSClientboundBlockDestructionPacket();
        OUT_BLOCK_CHANGE = new NMSPacketClasses.NMSClientboundBlockUpdatePacket();
        OUT_BOSS = new NMSPacketClasses.NMSClientboundBossEventPacket();
        OUT_CAMERA = new NMSPacketClasses.NMSClientboundSetCameraPacket();
        OUT_CUSTOM_SOUND_EFFECT = new NMSPacketClasses.NMSClientboundCustomSoundPacket();
        OUT_RESOURCE_PACK_PUSH = new NMSPacketClasses.NMSClientboundResourcePackPushPacket();
        OUT_RESOURCE_PACK_POP = new NMSPacketClasses.NMSClientboundResourcePackPopPacket();
        OUT_SERVER_DIFFICULTY = new NMSPacketClasses.NMSClientboundChangeDifficultyPacket();
        OUT_SET_COOLDOWN = new NMSPacketClasses.NMSClientboundCooldownPacket();
        OUT_UNLOAD_CHUNK = new NMSPacketClasses.NMSClientboundForgetLevelChunkPacket();
        OUT_SCOREBOARD_DISPLAY_OBJECTIVE = new NMSPacketClasses.NMSClientboundSetDisplayObjectivePacket();
        OUT_SCOREBOARD_OBJECTIVE = new NMSPacketClasses.NMSClientboundSetObjectivePacket();
        OUT_SCOREBOARD_SCORE = new NMSPacketClasses.NMSClientboundSetScorePacket();
        OUT_SCOREBOARD_SCORE_RESET = new NMSPacketClasses.NMSClientboundResetScorePacket();
        OUT_SCOREBOARD_TEAM = new NMSPacketClasses.NMSClientboundSetPlayerTeamPacket();
        OUT_WINDOW_CLOSE = new NMSPacketClasses.NMSClientboundContainerClosePacket();
        OUT_WINDOW_DATA = new NMSPacketClasses.NMSClientboundContainerSetDataPacket();
        OUT_WINDOW_OPEN = new NMSPacketClasses.NMSClientboundOpenScreenPacket();
        OUT_WINDOW_SET_SLOT = new NMSPacketClasses.NMSClientboundContainerSetSlotPacket();
        OUT_WINDOW_ITEMS = new NMSPacketClasses.NMSClientboundContainerSetContentPacket();
        OUT_ENTITY_SPAWN = new NMSPacketClasses.NMSClientboundAddEntityPacket();
        OUT_ENTITY_SPAWN_NAMED = new NMSPacketClasses.NMSClientboundAddPlayerPacket();
        OUT_ENTITY_SPAWN_EXPORB = new NMSPacketClasses.NMSClientboundAddExperienceOrbPacket();
        OUT_ENTITY_SPAWN_LIVING = new NMSPacketClasses.NMSClientboundAddMobPacket();
        OUT_ENTITY_SPAWN_PAINTING = new NMSPacketClasses.NMSClientboundAddPaintingPacket();
        OUT_ENTITY_SPAWN_WITHER = new NMSPacketClasses.NMSPacketPlayOutSpawnEntityWeather();
        OUT_ENTITY_DESTROY = new NMSPacketClasses.NMSClientboundRemoveEntitiesPacket();
        OUT_ENTITY_ATTACH = new NMSPacketClasses.NMSClientboundSetEntityLinkPacket();
        OUT_ENTITY_EFFECT_ADD = new NMSPacketClasses.NMSClientboundUpdateMobEffectPacket();
        OUT_ENTITY_EFFECT_REMOVE = new NMSPacketClasses.NMSClientboundRemoveMobEffectPacket();
        OUT_ENTITY_EQUIPMENT = new NMSPacketClasses.NMSClientboundSetEquipmentPacket();
        OUT_ENTITY_HEAD_ROTATION = new NMSPacketClasses.NMSClientboundRotateHeadPacket();
        OUT_ENTITY_LOOK = new NMSPacketClasses.NMSClientboundMoveEntityPacketRot();
        OUT_ENTITY_ANIMATION = new NMSPacketClasses.NMSClientboundAnimatePacket();
        OUT_ENTITY_METADATA = new NMSPacketClasses.NMSClientboundSetEntityDataPacket();
        OUT_ENTITY_STATUS = new NMSPacketClasses.NMSClientboundEntityEventPacket();
        OUT_ENTITY_TELEPORT = new NMSPacketClasses.NMSClientboundEntityPositionSyncPacket();
        OUT_ENTITY_VELOCITY = new NMSPacketClasses.NMSClientboundSetEntityMotionPacket();
        OUT_ENTITY_MOVE = new NMSPacketClasses.NMSClientboundMoveEntityPacketPos();
        OUT_ENTITY_MOVE_LOOK = new NMSPacketClasses.NMSClientboundMoveEntityPacketPosRot();
        OUT_ENTITY_UPDATE_ATTRIBUTES = new NMSPacketClasses.NMSClientboundUpdateAttributesPacket();
        OUT_MOUNT = new NMSPacketClasses.NMSClientboundSetPassengersPacket();
        OUT_VEHICLE_MOVE = new NMSPacketClasses.NMSClientboundMoveVehiclePacket();
        OUT_UPDATE_SIGN = new NMSPacketClasses.NMSPacketPlayOutUpdateSign();
        OUT_BUNDLE = new NMSPacketClasses.NMSClientboundBundlePacket();
        IN_ABILITIES = new NMSPacketClasses.NMSServerboundPlayerAbilitiesPacket();
        IN_BOAT_MOVE = new NMSPacketClasses.NMSServerboundPaddleBoatPacket();
        IN_CHAT = new NMSPacketClasses.NMSServerboundChatPacket();
        IN_CLIENT_COMMAND = new NMSPacketClasses.NMSServerboundClientCommandPacket();
        IN_CUSTOM_PAYLOAD = new NMSPacketClasses.NMSServerboundCustomPayloadPacket();
        IN_PLAYER_COMMAND = new NMSPacketClasses.NMSServerboundPlayerCommandPacket();
        IN_LOOK = new NMSPacketClasses.NMSServerboundMovePlayerPacketRot();
        IN_POSITION = new NMSPacketClasses.NMSServerboundMovePlayerPacketPos();
        IN_POSITION_LOOK = new NMSPacketClasses.NMSServerboundMovePlayerPacketPosRot();
        IN_HELD_ITEM_SLOT = new NMSPacketClasses.NMSServerboundSetCarriedItemPacket();
        IN_KEEP_ALIVE = new NMSPacketClasses.NMSServerboundKeepAlivePacket();
        IN_SPECTATE = new NMSPacketClasses.NMSServerboundTeleportToEntityPacket();
        IN_SET_CREATIVE_SLOT = new NMSPacketClasses.NMSServerboundSetCreativeModeSlotPacket();
        IN_SETTINGS = new NMSPacketClasses.NMSServerboundClientInformationPacket();
        IN_STEER_VEHICLE = new NMSPacketClasses.NMSServerboundPlayerInputPacket();
        IN_TAB_COMPLETE = new NMSPacketClasses.NMSServerboundCommandSuggestionPacket();
        IN_TELEPORT_ACCEPT = new NMSPacketClasses.NMSServerboundAcceptTeleportationPacket();
        IN_UPDATE_SIGN = new NMSPacketClasses.NMSServerboundSignUpdatePacket();
        IN_PLAYER_ACTION = new NMSPacketClasses.NMSServerboundPlayerActionPacket();
        IN_SWING = new NMSPacketClasses.NMSServerboundSwingPacket();
        IN_INTERACT = new NMSPacketClasses.NMSServerboundInteractPacket();
        IN_ATTACK = new NMSPacketClasses.NMSServerboundAttackPacket();
        IN_USE_ITEM = new NMSPacketClasses.NMSServerboundUseItemPacket();
        IN_USE_ITEM_ON = new NMSPacketClasses.NMSServerboundUseItemOnPacket();
        IN_VEHICLE_MOVE = new NMSPacketClasses.NMSServerboundMoveVehiclePacket();
        IN_CLIENT_TICK_END = new NMSPacketClasses.NMSServerboundClientTickEndPacket();
        IN_WINDOW_CLOSE = new NMSPacketClasses.NMSServerboundContainerClosePacket();
        IN_WINDOW_ENCHANT_ITEM = new NMSPacketClasses.NMSServerboundContainerButtonClickPacket();
        IN_WINDOW_RESOURCEPACK_STATUS = new NMSPacketClasses.NMSServerboundResourcePackPacket();
        IN_WINDOW_CLICK = new NMSPacketClasses.NMSServerboundContainerClickPacket();
    }

    private static interface PacketTypeOptions {
        public PacketType firstRegistered();

        public PacketType find(Object var1);

        public PacketTypeOptions add(PacketType var1);
    }

    private static enum PacketSearchResult {
        FAILED,
        NOT_FOUND,
        FOUND;

    }

    private static final class PacketTypeOptionsMultiple
    implements PacketTypeOptions {
        private final PacketType[] types;

        public PacketTypeOptionsMultiple(List<PacketType> types) {
            this.types = types.toArray(new PacketType[types.size()]);
        }

        @Override
        public PacketType firstRegistered() {
            return this.types[this.types.length - 1];
        }

        @Override
        public PacketType find(Object packetHandle) {
            for (PacketType type : this.types) {
                if (!type.matchPacket(packetHandle)) continue;
                return type;
            }
            return null;
        }

        @Override
        public PacketTypeOptions add(PacketType newType) {
            ArrayList<PacketType> newTypes = new ArrayList<PacketType>(this.types.length + 1);
            newTypes.add(newType);
            newTypes.addAll(Arrays.asList(this.types));
            return new PacketTypeOptionsMultiple(newTypes);
        }
    }

    private static final class PacketTypeOptionsSingleton
    implements PacketTypeOptions {
        private final PacketType type;

        public PacketTypeOptionsSingleton(PacketType type) {
            this.type = type;
        }

        @Override
        public PacketType firstRegistered() {
            return this.type;
        }

        @Override
        public PacketType find(Object packetHandle) {
            return this.type;
        }

        @Override
        public PacketTypeOptions add(PacketType newType) {
            ArrayList<PacketType> types = new ArrayList<PacketType>(2);
            types.add(newType);
            types.add(this.type);
            return new PacketTypeOptionsMultiple(types);
        }
    }
}

