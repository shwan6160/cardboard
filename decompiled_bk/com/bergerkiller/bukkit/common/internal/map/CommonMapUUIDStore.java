/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class CommonMapUUIDStore {
    @Deprecated
    public static boolean isMap(ItemStack item) {
        return CommonItemStack.of(item).isFilledMap();
    }

    @Deprecated
    public static boolean isMapDisplay(ItemStack item) {
        return CommonItemStack.of(item).isMapDisplay();
    }

    public static int getItemMapId(ItemStack item) {
        Object nmsHandle = ((Template.Field)CraftItemStackHandle.T.handle.raw).get(item);
        return nmsHandle == null ? -1 : ItemStackHandle.T.getMapId.invoke(nmsHandle);
    }

    public static void setItemMapId(ItemStack item, int mapId) {
        ItemStackHandle.T.setMapId.invoke(((Template.Field)CraftItemStackHandle.T.handle.raw).get(item), mapId);
    }

    public static UUID getMapUUID(CommonItemStack item) {
        return item.getHandleIfCraftItemStack().map(ItemStackHandle::getMapDisplayUUID).orElse(null);
    }

    public static UUID getMapUUID(ItemStack item) {
        Object nmsItemStack;
        if (item == null) {
            return null;
        }
        if (!CraftItemStackHandle.T.isAssignableFrom(item)) {
            if (CommonMapUUIDStore.isMap(item)) {
                item = ItemUtil.createItem(item);
            } else {
                return null;
            }
        }
        if ((nmsItemStack = ((Template.Field)CraftItemStackHandle.T.handle.raw).get(item)) == null) {
            return null;
        }
        return ItemStackHandle.T.getMapDisplayUUID.invoke(nmsItemStack);
    }

    public static UUID getStaticMapUUID(int mapId) {
        long leastBits = mapId;
        long mostBits = 0L;
        return new UUID(mostBits, leastBits);
    }

    public static boolean isStaticMapId(UUID uuid) {
        return uuid.getMostSignificantBits() == 0L && (uuid.getLeastSignificantBits() & 0xFFFFFFFF00000000L) == 0L;
    }

    public static int getStaticMapId(UUID uuid) {
        int id;
        if (uuid.getMostSignificantBits() != 0L) {
            return -1;
        }
        long least = uuid.getLeastSignificantBits();
        if (least - (long)(id = (int)(least & 0xFFFFFFFFL)) != 0L) {
            return -1;
        }
        return id;
    }

    public static UUID generateDynamicMapUUID() {
        UUID uuid;
        while (CommonMapUUIDStore.isStaticMapId(uuid = UUID.randomUUID())) {
        }
        return uuid;
    }
}

