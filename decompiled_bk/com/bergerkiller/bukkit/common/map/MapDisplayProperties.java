/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.ItemFrame
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.map;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.map.CommonMapUUIDStore;
import com.bergerkiller.bukkit.common.inventory.CommonItemMaterials;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.util.ItemStackMapDisplayProperties;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public abstract class MapDisplayProperties {
    public abstract CommonItemStack getCommonMapItem();

    public final ItemStack getMapItem() {
        return this.getCommonMapItem().toBukkit();
    }

    public String getPluginName() {
        return this.getMetadata().getValue("mapDisplayPlugin", String.class, null);
    }

    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(this.getPluginName());
    }

    public String getMapDisplayClassName() {
        return this.getMetadata().getValue("mapDisplayClass", String.class, null);
    }

    public UUID getUniqueId() {
        return this.getMetadata().getUUID("mapDisplay");
    }

    public Class<? extends MapDisplay> getMapDisplayClass() {
        Plugin plugin;
        String name = this.getMapDisplayClassName();
        if (name != null && (plugin = this.getPlugin()) != null && plugin.isEnabled()) {
            try {
                return plugin.getClass().getClassLoader().loadClass(name).asSubclass(MapDisplay.class);
            }
            catch (ClassCastException | ClassNotFoundException exception) {
                // empty catch block
            }
        }
        return null;
    }

    public CommonTagCompound getMetadata() {
        return this.getCommonMapItem().getCustomData();
    }

    public CommonTagCompound getMetadataCopy() {
        return this.getCommonMapItem().getCustomDataCopy();
    }

    public void setMetadata(CommonTagCompound metadata) {
        this.getCommonMapItem().setCustomData(metadata);
    }

    public void updateMetadata(Consumer<CommonTagCompound> consumer) {
        this.getCommonMapItem().updateCustomData(consumer);
    }

    public boolean containsKey(String key, Class<?> type) {
        return this.getMetadata().getValue(key, type) != null;
    }

    public void set(String key, Object value) {
        this.updateMetadata(metadata -> metadata.putValue(key, value));
    }

    public <T> T get(String key, Class<T> type) {
        return (T)this.getMetadata().getValue(key, type);
    }

    public <T> T get(String key, Class<T> type, T defaultValue) {
        return this.getMetadata().getValue(key, type, defaultValue);
    }

    public <T> T get(String key, T defaultValue) {
        return this.getMetadata().getValue(key, defaultValue);
    }

    public void setDisplayName(ChatText displayName) {
        this.getCommonMapItem().setCustomName(displayName);
    }

    public void setDisplayName(String displayName) {
        this.getCommonMapItem().setCustomName(displayName == null ? null : ChatText.fromMessage(displayName));
    }

    public void setMapColor(int red, int green, int blue) {
        this.setMapColor((red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF);
    }

    public void setMapColor(int rgbColor) {
        this.getCommonMapItem().setFilledMapColor(rgbColor);
    }

    public void fillItemFrames(ItemFrame startItemFrame) {
        CommonPlugin.getInstance().getMapController().fillItemFrames(startItemFrame, ItemUtil.cloneItem(this.getMapItem()));
    }

    public static MapDisplayProperties of(ItemStack mapItem) {
        return MapDisplayProperties.of(CommonItemStack.of(mapItem));
    }

    public static MapDisplayProperties of(CommonItemStack mapItem) {
        return new ItemStackMapDisplayProperties(mapItem);
    }

    public static MapDisplayProperties createNew(Class<? extends MapDisplay> mapDisplayClass) {
        Plugin plugin = CommonUtil.getPluginByClass(mapDisplayClass);
        if (plugin == null) {
            throw new IllegalArgumentException("The class " + mapDisplayClass.getName() + " does not belong to a Java Plugin");
        }
        return MapDisplayProperties.createNew(plugin, mapDisplayClass);
    }

    public static MapDisplayProperties createNew(Plugin plugin, Class<? extends MapDisplay> mapDisplayClass) {
        if (!CommonPlugin.getInstance().isMapDisplaysEnabled()) {
            throw new UnsupportedOperationException("Map displays are disabled in BKCommonLib's config.yml!");
        }
        try {
            mapDisplayClass.getConstructor(new Class[0]);
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("The class " + mapDisplayClass.getName() + " does not have an empty constructor. Override onAttached() and use properties instead!");
        }
        CommonItemStack mapItem = CommonItemStack.create(CommonItemMaterials.FILLED_MAP, 1);
        mapItem.setFilledMapId(0);
        mapItem.updateCustomData(tag -> {
            tag.putValue("mapDisplayPlugin", plugin.getName());
            tag.putValue("mapDisplayClass", mapDisplayClass.getName());
            tag.putUUID("mapDisplay", CommonMapUUIDStore.generateDynamicMapUUID());
        });
        return MapDisplayProperties.of(mapItem);
    }
}

