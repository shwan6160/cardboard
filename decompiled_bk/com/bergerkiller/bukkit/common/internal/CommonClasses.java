/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer;
import com.bergerkiller.bukkit.common.internal.logic.EntityAddRemoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityMoveHandler;
import com.bergerkiller.bukkit.common.internal.logic.EntityTypingHandler;
import com.bergerkiller.bukkit.common.internal.logic.PlayerFileDataHandler;
import com.bergerkiller.bukkit.common.internal.logic.PortalHandler;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

class CommonClasses {
    CommonClasses() {
    }

    public static void init() {
        if (!Common.IS_COMPATIBLE) {
            return;
        }
        CommonUtil.loadClass(Common.class);
        CommonClasses.loadLogic("EntityAddRemoveHandler", "EntityMoveHandler", "EntityTypingHandler", "RegionHandler");
        CommonClasses.loadUtil("Block", "Chunk", "Common", "EntityProperty", "Entity", "Item", "Material", "Native", "NBT", "Packet");
        CommonClasses.loadUtil("Recipe", "Stream", "World");
        CommonClasses.loadCommon("entity.CommonEntityType", "collections.CollectionBasics");
        CommonClasses.loadCommon("scoreboards.CommonScoreboard", "scoreboards.CommonTeam");
        CommonClasses.loadCommon("protocol.PacketType");
        CommonClasses.loadCommon("internal.CommonDisabledEntity");
        CommonClasses.loadCommon("wrappers.DataWatcher");
    }

    private static void loadLogic(String ... classNames) {
        for (int i = 0; i < classNames.length; ++i) {
            classNames[i] = "internal.logic." + classNames[i];
        }
        CommonClasses.loadCommon(classNames);
    }

    private static void loadUtil(String ... classNames) {
        for (int i = 0; i < classNames.length; ++i) {
            classNames[i] = "utils." + classNames[i] + "Util";
        }
        CommonClasses.loadCommon(classNames);
    }

    private static void loadCommon(String ... classNames) {
        for (int i = 0; i < classNames.length; ++i) {
            classNames[i] = "com.bergerkiller.bukkit.common." + classNames[i];
        }
        Common.loadClasses(classNames);
    }

    public static void initializeLogicClasses(Logger logger) {
        try {
            RegionHandler.INSTANCE.forceInitialization();
        }
        catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the RegionHandler", t);
        }
        try {
            PortalHandler.INSTANCE.forceInitialization();
        }
        catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the PortalHandler", t);
        }
        try {
            PlayerFileDataHandler.INSTANCE.forceInitialization();
        }
        catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the PlayerFileDataHandler", t);
        }
        try {
            LightingHandler.instance().getClass();
        }
        catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the LightingHandler", t);
        }
        try {
            EntityTypingHandler.INSTANCE.getClass();
        }
        catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the EntityTypingHandler", t);
        }
        try {
            EntityMoveHandler.assertInitialized();
        }
        catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the EntityMoveHandler", t);
        }
        try {
            EntityAddRemoveHandler.INSTANCE.forceInitialization();
        }
        catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the EntityAddRemoveHandler", t);
        }
        try {
            BlockDataSerializer.INSTANCE.forceInitialization();
        }
        catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to initialize the BlockDataSerializer", t);
        }
    }
}

