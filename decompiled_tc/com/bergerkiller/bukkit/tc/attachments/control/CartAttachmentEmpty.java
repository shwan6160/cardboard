/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CartAttachmentEmpty
extends CartAttachment {
    public static final AttachmentType TYPE = new AttachmentType(){

        @Override
        public String getID() {
            return "EMPTY";
        }

        @Override
        public double getSortPriority() {
            return -1.0;
        }

        @Override
        public MapTexture getIcon(ConfigurationNode config) {
            return MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/empty.png");
        }

        @Override
        public Attachment createController(ConfigurationNode config) {
            return new CartAttachmentEmpty();
        }
    };

    @Override
    public void makeVisible(Player viewer) {
    }

    @Override
    public void makeHidden(Player viewer) {
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onMove(boolean absolute) {
    }
}

