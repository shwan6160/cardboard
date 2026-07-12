/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui.animation;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.ui.animation.MapWidgetAnimationNode;
import org.bukkit.plugin.java.JavaPlugin;

public class MapWidgetAnimationHeader
extends MapWidget {
    public MapWidgetAnimationHeader() {
        this.setSize(100, 5);
    }

    public void onDraw() {
        MapWidgetAnimationNode.Column[] columns = MapWidgetAnimationNode.calculateColumns(this.getWidth());
        byte top_color = MapColorPalette.getColor((int)163, (int)233, (int)247);
        byte mid_color = MapColorPalette.getColor((int)140, (int)201, (int)213);
        byte btm_color = MapColorPalette.getColor((int)115, (int)164, (int)174);
        this.view.drawLine(0, 0, this.getWidth() - 1, 0, top_color);
        this.view.fillRectangle(0, 1, this.getWidth(), this.getHeight() - 2, mid_color);
        this.view.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, btm_color);
        this.view.drawLine(columns[1].x - 1, 0, columns[1].x - 1, this.getHeight() - 1, (byte)119);
        this.view.drawLine(columns[2].x - 1, 0, columns[2].x - 1, this.getHeight() - 1, (byte)119);
        MapTexture labelTex = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/anim_header.png");
        int labelDX = labelTex.getWidth() >> 1;
        int labelHeight = labelTex.getHeight() / 3;
        int top_y = this.getHeight() - labelHeight >> 1;
        for (int i = 0; i < columns.length; ++i) {
            this.view.draw(labelTex.getView(0, i * labelHeight, labelTex.getWidth(), labelHeight), columns[i].mid - labelDX, top_y);
        }
    }
}

