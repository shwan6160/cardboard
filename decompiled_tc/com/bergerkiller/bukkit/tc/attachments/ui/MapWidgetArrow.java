/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;

public class MapWidgetArrow
extends MapWidget {
    private MapTexture tex_disabled;
    private MapTexture tex_enabled;
    private MapTexture tex_focused;
    private int focus_ticks = 0;
    private BlockFace direction = null;

    public MapWidgetArrow() {
        this.setDirection(BlockFace.NORTH);
    }

    public MapWidgetArrow(BlockFace direction) {
        this.setDirection(direction);
    }

    public MapWidgetArrow setDirection(BlockFace direction) {
        if (this.direction == direction) {
            return this;
        }
        this.direction = direction;
        MapTexture tex = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/arrow.png");
        int w = tex.getWidth() / 3;
        int h = tex.getHeight();
        MapCanvas in_text_disabled = tex.getView(0, 0, w, h);
        MapCanvas in_text_enabled = tex.getView(w, 0, w, h);
        MapCanvas in_text_focused = tex.getView(2 * w, 0, w, h);
        this.tex_disabled = MapTexture.rotate((MapCanvas)in_text_disabled, (int)FaceUtil.faceToYaw((BlockFace)direction));
        this.tex_enabled = MapTexture.rotate((MapCanvas)in_text_enabled, (int)FaceUtil.faceToYaw((BlockFace)direction));
        this.tex_focused = MapTexture.rotate((MapCanvas)in_text_focused, (int)FaceUtil.faceToYaw((BlockFace)direction));
        this.setSize(this.tex_enabled.getWidth(), this.tex_enabled.getHeight());
        return this;
    }

    public void onDetached() {
        super.onDetached();
        this.stopFocus();
    }

    public void stopFocus() {
        if (this.focus_ticks > 0) {
            this.focus_ticks = 0;
            this.invalidate();
        }
    }

    public void sendFocus() {
        if (this.focus_ticks == 0) {
            this.invalidate();
        }
        this.focus_ticks = 20;
    }

    public void onTick() {
        if (this.focus_ticks > 0) {
            --this.focus_ticks;
            if (this.focus_ticks == 0) {
                this.invalidate();
            }
        }
    }

    public void onDraw() {
        if (this.isEnabled()) {
            if (this.focus_ticks > 0) {
                this.view.draw((MapCanvas)this.tex_focused, 0, 0);
            } else {
                this.view.draw((MapCanvas)this.tex_enabled, 0, 0);
            }
        } else {
            this.view.draw((MapCanvas)this.tex_disabled, 0, 0);
        }
    }
}

