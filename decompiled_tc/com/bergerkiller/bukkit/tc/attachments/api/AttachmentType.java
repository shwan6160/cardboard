/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.attachments.api;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.PositionMenu;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface AttachmentType {
    public static final String MODEL_TYPE_ID = "MODEL";

    public String getID();

    default public Plugin getPlugin() {
        return CommonUtil.getPluginByClass(this.getClass());
    }

    default public String getName() {
        return this.getID();
    }

    default public double getSortPriority() {
        return 0.0;
    }

    default public boolean isListed(Player player) {
        return this.hasPermission(player);
    }

    default public boolean hasPermission(Player player) {
        return true;
    }

    default public MapTexture getIcon(ConfigurationNode config) {
        return MapTexture.createEmpty((int)16, (int)16);
    }

    default public void migrateConfiguration(ConfigurationNode config) {
    }

    default public void getDefaultConfig(ConfigurationNode config) {
    }

    default public void createAppearanceTab(MapWidgetTabView.Tab tab, MapWidgetAttachmentNode attachment) {
    }

    default public void createPositionMenu(PositionMenu.Builder builder) {
    }

    public Attachment createController(ConfigurationNode var1);

    default public void onRegister(AttachmentTypeRegistry registry) {
    }

    default public void onUnregister(AttachmentTypeRegistry registry) {
    }
}

