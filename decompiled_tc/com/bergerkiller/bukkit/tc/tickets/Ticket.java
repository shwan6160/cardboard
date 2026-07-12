/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.map.MapDisplay
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.tickets;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.tickets.TCTicketDisplay;
import com.bergerkiller.bukkit.tc.tickets.TicketStore;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Ticket {
    private String _name;
    private String _realm = "";
    private boolean _playerBound = false;
    private int _maxNumberOfUses = 1;
    private long _expirationTime = -1L;
    private String _backgroundImagePath = "";
    private ConfigurationNode _properties = new ConfigurationNode();

    Ticket(String name) {
        this._name = name;
    }

    public String getName() {
        return this._name;
    }

    public boolean setName(String name) {
        if (this._name.equals(name)) {
            return true;
        }
        String oldName = this._name;
        this._name = name;
        if (TicketStore.renameTicket(oldName, this._name)) {
            return true;
        }
        this._name = oldName;
        return false;
    }

    public boolean remove() {
        return TicketStore.removeTicket(this._name);
    }

    public String getRealm() {
        return this._realm;
    }

    public void setRealm(String realm) {
        this._realm = realm;
    }

    public String getBackgroundImagePath() {
        return this._backgroundImagePath;
    }

    public void setBackgroundImagePath(String path) {
        this._backgroundImagePath = path;
    }

    public void setBackgroundImagePluginPath(JavaPlugin plugin, String path) {
        this._backgroundImagePath = plugin.getName() + ":" + path;
    }

    public MapTexture loadBackgroundImage() {
        if (this._backgroundImagePath.isEmpty()) {
            return Ticket.getDefaultBackgroundImage();
        }
        int index = this._backgroundImagePath.indexOf(58);
        if (index != -1) {
            String pluginName = this._backgroundImagePath.substring(0, index);
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (plugin instanceof JavaPlugin) {
                try {
                    MapTexture bg = MapTexture.loadPluginResource((JavaPlugin)((JavaPlugin)plugin), (String)this._backgroundImagePath.substring(index + 1));
                    if (bg.getWidth() >= 128 && bg.getHeight() >= 128) {
                        return bg;
                    }
                }
                catch (RuntimeException bg) {
                    // empty catch block
                }
                return Ticket.getDefaultBackgroundImage();
            }
        }
        File imagesDir = TrainCarts.plugin.getDataFile(new String[]{"images"});
        File imageFile = new File(this._backgroundImagePath);
        if (!imageFile.isAbsolute()) {
            imageFile = new File(imagesDir, this._backgroundImagePath);
        }
        if (!TCConfig.allowExternalTicketImagePaths) {
            boolean validLocation;
            try {
                File a = imageFile.getAbsoluteFile().getCanonicalFile();
                File b = imagesDir.getAbsoluteFile().getCanonicalFile();
                validLocation = a.toPath().startsWith(b.toPath());
            }
            catch (IOException ex) {
                validLocation = false;
            }
            if (!validLocation) {
                return Ticket.getDefaultBackgroundImage();
            }
        }
        try {
            MapTexture bg = MapTexture.fromImageFile((String)imageFile.getAbsolutePath());
            if (bg.getWidth() >= 128 && bg.getHeight() >= 128) {
                return bg;
            }
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return Ticket.getDefaultBackgroundImage();
    }

    public static MapTexture getDefaultBackgroundImage() {
        return TrainCarts.plugin.loadTexture("com/bergerkiller/bukkit/tc/textures/tickets/train_ticket_bg.png");
    }

    public boolean isPlayerBound() {
        return this._playerBound;
    }

    public void setPlayerBound(boolean playerBound) {
        this._playerBound = playerBound;
    }

    public int getMaxNumberOfUses() {
        return this._maxNumberOfUses;
    }

    public void setMaxNumberOfUses(int maxNumberOfUses) {
        this._maxNumberOfUses = maxNumberOfUses;
    }

    public long getExpirationTime() {
        return this._expirationTime;
    }

    public void setExpirationTime(long expirationTimeMillis) {
        this._expirationTime = expirationTimeMillis;
    }

    public ConfigurationNode getProperties() {
        return this._properties;
    }

    public void setProperties(ConfigurationNode properties) {
        this._properties = properties.clone();
        TicketStore.markChanged();
    }

    public void load(ConfigurationNode config) {
        this._realm = (String)config.get("ticketRealm", (Object)"");
        this._playerBound = (Boolean)config.get("playerBound", (Object)false);
        this._maxNumberOfUses = (Integer)config.get("maxNumberOfUses", (Object)1);
        this._expirationTime = (Long)config.get("expirationTimeMillis", (Object)-1L);
        this._backgroundImagePath = (String)config.get("backgroundImagePath", (Object)"");
        this._properties = config.getNode("properties").clone();
    }

    public void save(ConfigurationNode config) {
        config.set("ticketRealm", (Object)this._realm);
        config.set("playerBound", (Object)this._playerBound);
        config.set("maxNumberOfUses", (Object)this._maxNumberOfUses);
        config.set("expirationTimeMillis", (Object)this._expirationTime);
        config.set("backgroundImagePath", (Object)this._backgroundImagePath);
        ConfigurationNode savedProps = config.getNode("properties");
        for (Map.Entry entry : this._properties.getValues().entrySet()) {
            savedProps.set((String)entry.getKey(), entry.getValue());
        }
    }

    public ItemStack createItem(Player owner, ItemStack baseItem) {
        return CommonItemStack.copyOf((ItemStack)baseItem).updateCustomData(tag -> {
            tag.putValue("plugin", (Object)"TrainCarts");
            tag.putValue("ticketName", (Object)this.getName());
            tag.putValue("ticketCreationTime", (Object)System.currentTimeMillis());
            tag.putValue("ticketNumberOfUses", (Object)0);
            tag.putUUID("ticketOwner", owner.getUniqueId());
            tag.putValue("ticketOwnerName", (Object)owner.getDisplayName());
        }).setCustomNameMessage("Train Ticket for " + this.getName()).toBukkit();
    }

    public ItemStack createItem(Player owner) {
        return CommonItemStack.of((ItemStack)MapDisplay.createMapItem(TCTicketDisplay.class)).updateCustomData(tag -> {
            tag.putValue("plugin", (Object)"TrainCarts");
            tag.putValue("ticketName", (Object)this.getName());
            tag.putValue("ticketCreationTime", (Object)System.currentTimeMillis());
            tag.putValue("ticketNumberOfUses", (Object)0);
            tag.putUUID("ticketOwner", owner.getUniqueId());
            tag.putValue("ticketOwnerName", (Object)owner.getDisplayName());
        }).setCustomNameMessage("Train Ticket for " + this.getName()).toBukkit();
    }
}

