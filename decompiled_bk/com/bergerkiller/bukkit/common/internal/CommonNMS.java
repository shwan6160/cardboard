/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  org.bukkit.Chunk
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.server.dedicated.DedicatedPlayerListHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.LivingEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.item.ItemEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.player.PlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftServerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Optional;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommonNMS {
    private static Class<?> JAVA_UTIL_OPTIONAL_TYPE = CommonUtil.getClass("java.util.Optional");
    private static Class<?> GOOGLE_OPTIONAL_TYPE = CommonUtil.getClass("com.google.common.base.Optional");
    public static Class<?> DWR_OPTIONAL_TYPE = Common.evaluateMCVersion(">=", "1.13") ? JAVA_UTIL_OPTIONAL_TYPE : GOOGLE_OPTIONAL_TYPE;

    public static boolean isDWROptionalType(Class<?> type) {
        return type != null && type.equals(DWR_OPTIONAL_TYPE);
    }

    public static Object unwrapDWROptional(Object value) {
        if (DWR_OPTIONAL_TYPE != null) {
            if (DWR_OPTIONAL_TYPE == GOOGLE_OPTIONAL_TYPE) {
                if (value instanceof com.google.common.base.Optional) {
                    com.google.common.base.Optional opt = (com.google.common.base.Optional)value;
                    return opt.isPresent() ? opt.get() : null;
                }
            } else if (DWR_OPTIONAL_TYPE == JAVA_UTIL_OPTIONAL_TYPE && value instanceof Optional) {
                Optional opt = (Optional)value;
                return opt.isPresent() ? opt.get() : null;
            }
        }
        return value;
    }

    public static Object wrapDWROptional(Object value) {
        if (!(DWR_OPTIONAL_TYPE == null || value != null && DWR_OPTIONAL_TYPE.isAssignableFrom(value.getClass()))) {
            value = DWR_OPTIONAL_TYPE.equals(JAVA_UTIL_OPTIONAL_TYPE) ? Optional.ofNullable(value) : com.google.common.base.Optional.fromNullable((Object)value);
        }
        return value;
    }

    public static boolean isItemEmpty(Object rawItemStackHandle) {
        if (rawItemStackHandle == null) {
            return true;
        }
        if (ItemStackHandle.T.isEmpty.isAvailable()) {
            return ItemStackHandle.T.isEmpty.invoke(rawItemStackHandle);
        }
        return false;
    }

    public static LevelChunkHandle getHandle(Chunk chunk) {
        return LevelChunkHandle.createHandle(HandleConversion.toChunkHandle(chunk));
    }

    public static ItemStackHandle getHandle(ItemStack stack) {
        return ItemStackHandle.createHandle(Conversion.toItemStackHandle.convert(stack));
    }

    public static EntityHandle getHandle(Entity entity) {
        return CommonNMS.getHandle(entity, EntityHandle.T);
    }

    public static ItemEntityHandle getHandle(Item item) {
        return CommonNMS.getHandle((Entity)item, ItemEntityHandle.T);
    }

    public static LivingEntityHandle getHandle(LivingEntity l) {
        return CommonNMS.getHandle((Entity)l, LivingEntityHandle.T);
    }

    public static PlayerHandle getHandle(HumanEntity h) {
        return CommonNMS.getHandle((Entity)h, PlayerHandle.T);
    }

    public static ServerPlayerHandle getHandle(Player p) {
        return CommonNMS.getHandle((Entity)p, ServerPlayerHandle.T);
    }

    public static Object getRawHandle(Entity e, Template.Class<?> type) {
        return LogicUtil.tryCast(HandleConversion.toEntityHandle(e), type.getType());
    }

    public static <T extends Template.Handle> T getHandle(Entity e, Template.Class<T> type) {
        Object rawInstance = HandleConversion.toEntityHandle(e);
        if (type.isAssignableFrom(rawInstance)) {
            return type.createHandle(rawInstance);
        }
        return null;
    }

    public static ServerLevelHandle getHandle(World world) {
        return ServerLevelHandle.createHandle(HandleConversion.toWorldHandle(world));
    }

    public static ItemHandle getItem(Material material) {
        return material == null ? null : ItemHandle.createHandle(HandleConversion.toItemHandle(material));
    }

    public static DedicatedPlayerListHandle getPlayerList() {
        return CraftServerHandle.instance().getPlayerList();
    }
}

