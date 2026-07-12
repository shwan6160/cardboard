/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.utils.LogicUtil;

public class CommonCapabilities {
    public static final boolean VEHICLES_COLLIDE_WITH_PASSENGERS = CommonBootstrap.evaluateMCVersion("<", "1.9");
    public static final boolean PLACE_PACKETS_MERGED = CommonBootstrap.evaluateMCVersion("<", "1.9");
    public static final boolean RESOURCE_PACK_MODEL_BASE_TRANSFORMS = CommonBootstrap.evaluateMCVersion(">=", "1.9");
    public static final boolean KEYED_EFFECTS = CommonBootstrap.evaluateMCVersion(">=", "1.9");
    public static final boolean CHAT_TEXT_JSON_VER2 = CommonBootstrap.evaluateMCVersion(">=", "1.9");
    public static final boolean DATAWATCHER_OBJECTS = CommonBootstrap.evaluateMCVersion(">=", "1.9");
    public static final boolean PLAYER_OFF_HAND = CommonBootstrap.evaluateMCVersion(">=", "1.9");
    public static final boolean ENTITY_MOVE_VER2 = CommonBootstrap.evaluateMCVersion(">=", "1.11.2");
    public static final boolean ENTITY_MOVE_VER3 = CommonBootstrap.evaluateMCVersion(">=", "1.14");
    public static final boolean WORLD_LIGHT_DARK_INVERTED = CommonBootstrap.evaluateMCVersion(">=", "1.11.2");
    public static final boolean ITEMSTACK_EMPTY_STATE = CommonBootstrap.evaluateMCVersion(">=", "1.11");
    public static final boolean TILE_ENTITY_LEGACY_NAMES = CommonBootstrap.evaluateMCVersion("<=", "1.10.2");
    public static final boolean MULTIPLE_PASSENGERS = CommonBootstrap.evaluateMCVersion(">=", "1.9");
    public static final boolean REVISED_CHUNK_ENTITY_SLICE = CommonBootstrap.evaluateMCVersion(">=", "1.8.3");
    public static final boolean MATERIAL_ENUM_CHANGES = CommonBootstrap.evaluateMCVersion(">=", "1.13");
    public static final boolean MAP_ID_IN_NBT = CommonBootstrap.evaluateMCVersion(">=", "1.13");
    public static final boolean PARTICLE_OPTIONS = CommonBootstrap.evaluateMCVersion(">=", "1.13");
    public static final boolean HAS_VOXELSHAPE_LOGIC = CommonBootstrap.evaluateMCVersion(">=", "1.13");
    public static final boolean HAS_PREPARE_ANVIL_EVENT = CommonBootstrap.evaluateMCVersion(">=", "1.9");
    public static final boolean HAS_ADVANCEMENTS = CommonBootstrap.evaluateMCVersion(">=", "1.12");
    public static final boolean EMPTY_ITEM_NAME = CommonBootstrap.evaluateMCVersion(">=", "1.13");
    public static final boolean ENTITY_USES_DIMENSION_MANAGER;
    @Deprecated
    public static final boolean HAS_DIMENSION_MANAGER;
    public static final boolean BLOCK_SLAB_HAS_OWN_BLOCK;
    public static final boolean HAS_CHUNK_TICKET_API;
    public static final boolean UTIL_COLLECTIONS_REMOVED;
    public static final boolean NEW_LIGHT_ENGINE;
    public static final boolean LORE_IS_CHAT_COMPONENT;
    public static final boolean PLAYER_CHUNK_MAP_ENTITY_TRACKER;
    public static final boolean ASYNCHRONOUS_CHUNK_LOADER;
    public static final boolean HAS_WINDOW_TYPE_REGISTRY;
    public static final boolean HAS_MATERIAL_SIGN_TYPES;
    public static final boolean HAS_CUSTOM_MODEL_DATA;
    public static final boolean ENTITY_USES_ENTITYTYPES_IN_CONSTRUCTOR;
    public static final boolean CAN_CANCEL_CHUNK_UNLOAD_EVENT;
    public static final boolean IMMUTABLE_NBT_PRIMITIVES;
    public static final boolean ENTITY_FIRE_DAMAGE_IN_MOVE_HANDLER;
    public static final boolean VEHICLE_EXIT_CANCELLABLE;
    public static final boolean PLAYER_SPAWN_WORLD_IS_DIMENSION_KEY;
    public static final boolean PLAYER_SPAWN_HAS_ANGLE;
    public static final boolean PACKET_DESTROY_MULTIPLE;
    public static final boolean ENTITY_REMOVE_WITH_REASON;
    public static final boolean MOJANGMAP_METHODS;
    public static final boolean HAS_BUKKIT_PLAYER_PROFILE;
    public static final boolean ENTITY_SPAWN_PACKETS_MERGED;
    public static final boolean PLAYER_INFO_PACKET_SPLIT;
    public static final boolean HAS_BLOCKDATA_METADATA;
    public static final boolean HAS_DISPLAY_ENTITY;
    public static final boolean HAS_BUNDLE_PACKET;
    public static final boolean HAS_SIGN_BACK_TEXT;
    public static final boolean HAS_DISPLAY_ENTITY_LOCATION_INTERPOLATION;
    public static final boolean HAS_SIGN_OPEN_EVENT_PAPER;
    public static final boolean HAS_SIGN_OPEN_EVENT;
    public static final boolean HAS_ITEMSTACK_GLINT_OVERRIDE;
    public static final boolean HAS_ITEM_MODEL_COMPONENT;
    public static final boolean HAS_RESOURCEPACK_ITEMS_FOLDER;
    public static final boolean IS_RESPAWN_POINT_PACKED;
    public static final boolean IS_MINECART_BLOCK_COMBINED_KEY;
    public static final boolean INTERACT_PACKET_ATTACK_SPLIT;

    static {
        HAS_DIMENSION_MANAGER = ENTITY_USES_DIMENSION_MANAGER = CommonBootstrap.evaluateMCVersion(">=", "1.13.1") && !CommonBootstrap.evaluateMCVersion(">=", "1.16");
        BLOCK_SLAB_HAS_OWN_BLOCK = CommonBootstrap.evaluateMCVersion(">=", "1.13");
        HAS_CHUNK_TICKET_API = CommonBootstrap.evaluateMCVersion(">=", "1.13.1");
        UTIL_COLLECTIONS_REMOVED = CommonBootstrap.evaluateMCVersion(">=", "1.14");
        NEW_LIGHT_ENGINE = CommonBootstrap.evaluateMCVersion(">=", "1.14");
        LORE_IS_CHAT_COMPONENT = CommonBootstrap.evaluateMCVersion(">=", "1.14");
        PLAYER_CHUNK_MAP_ENTITY_TRACKER = CommonBootstrap.evaluateMCVersion(">=", "1.14");
        ASYNCHRONOUS_CHUNK_LOADER = CommonBootstrap.evaluateMCVersion(">=", "1.14");
        HAS_WINDOW_TYPE_REGISTRY = CommonBootstrap.evaluateMCVersion(">=", "1.14");
        HAS_MATERIAL_SIGN_TYPES = CommonBootstrap.evaluateMCVersion(">=", "1.14");
        HAS_CUSTOM_MODEL_DATA = CommonBootstrap.evaluateMCVersion(">=", "1.14");
        ENTITY_USES_ENTITYTYPES_IN_CONSTRUCTOR = CommonBootstrap.evaluateMCVersion(">=", "1.14");
        CAN_CANCEL_CHUNK_UNLOAD_EVENT = CommonBootstrap.evaluateMCVersion("<=", "1.13.2");
        IMMUTABLE_NBT_PRIMITIVES = CommonBootstrap.evaluateMCVersion(">=", "1.15");
        ENTITY_FIRE_DAMAGE_IN_MOVE_HANDLER = CommonBootstrap.evaluateMCVersion("<", "1.16");
        VEHICLE_EXIT_CANCELLABLE = CommonBootstrap.evaluateMCVersion("<", "1.16") || CommonBootstrap.evaluateMCVersion(">=", "1.17");
        PLAYER_SPAWN_WORLD_IS_DIMENSION_KEY = CommonBootstrap.evaluateMCVersion(">=", "1.16");
        PLAYER_SPAWN_HAS_ANGLE = CommonBootstrap.evaluateMCVersion(">=", "1.16.2");
        PACKET_DESTROY_MULTIPLE = CommonBootstrap.evaluateMCVersion("<", "1.17") || CommonBootstrap.evaluateMCVersion(">=", "1.17.1");
        ENTITY_REMOVE_WITH_REASON = CommonBootstrap.evaluateMCVersion(">=", "1.17");
        MOJANGMAP_METHODS = CommonBootstrap.evaluateMCVersion(">=", "1.18");
        HAS_BUKKIT_PLAYER_PROFILE = CommonBootstrap.evaluateMCVersion(">=", "1.18.1");
        ENTITY_SPAWN_PACKETS_MERGED = CommonBootstrap.evaluateMCVersion(">=", "1.19");
        PLAYER_INFO_PACKET_SPLIT = CommonBootstrap.evaluateMCVersion(">=", "1.19.3");
        HAS_BLOCKDATA_METADATA = CommonBootstrap.evaluateMCVersion(">=", "1.19.4");
        HAS_DISPLAY_ENTITY = CommonBootstrap.evaluateMCVersion(">=", "1.19.4");
        HAS_BUNDLE_PACKET = CommonBootstrap.evaluateMCVersion(">=", "1.19.4");
        HAS_SIGN_BACK_TEXT = CommonBootstrap.evaluateMCVersion(">=", "1.20");
        HAS_DISPLAY_ENTITY_LOCATION_INTERPOLATION = CommonBootstrap.evaluateMCVersion(">=", "1.20.2");
        HAS_SIGN_OPEN_EVENT_PAPER = LogicUtil.tryCreate(() -> Class.forName("io.papermc.paper.event.player.PlayerOpenSignEvent"), err -> null) != null;
        HAS_SIGN_OPEN_EVENT = HAS_SIGN_OPEN_EVENT_PAPER || CommonBootstrap.evaluateMCVersion(">=", "1.20.2");
        HAS_ITEMSTACK_GLINT_OVERRIDE = CommonBootstrap.evaluateMCVersion(">=", "1.20.5");
        HAS_ITEM_MODEL_COMPONENT = CommonBootstrap.evaluateMCVersion(">=", "1.21.2");
        HAS_RESOURCEPACK_ITEMS_FOLDER = CommonBootstrap.evaluateMCVersion(">=", "1.21.4");
        IS_RESPAWN_POINT_PACKED = CommonBootstrap.evaluateMCVersion(">=", "1.21.5");
        IS_MINECART_BLOCK_COMBINED_KEY = CommonBootstrap.evaluateMCVersion(">=", "1.21.5");
        INTERACT_PACKET_ATTACK_SPLIT = CommonBootstrap.evaluateMCVersion(">=", "26.1");
    }
}

