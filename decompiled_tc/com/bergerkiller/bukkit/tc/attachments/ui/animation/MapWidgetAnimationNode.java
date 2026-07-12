/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.attachments.ui.animation;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationNode;
import org.bukkit.util.Vector;

public class MapWidgetAnimationNode
extends MapWidget {
    private AnimationNode _node = null;
    private double _maxPosition = 1.0;
    private boolean _selected = false;
    private boolean _multiSelectRoot = false;

    public MapWidgetAnimationNode() {
        this.setSize(100, 5);
    }

    public MapWidgetAnimationNode setMaximumPosition(double maximum) {
        if (this._maxPosition != maximum) {
            this._maxPosition = maximum;
            this.invalidate();
        }
        return this;
    }

    public MapWidgetAnimationNode setValue(AnimationNode node) {
        this._node = node;
        this.invalidate();
        return this;
    }

    public AnimationNode getValue() {
        return this._node;
    }

    public void setSelected(boolean selected) {
        if (this._selected != selected) {
            this._selected = selected;
            this.invalidate();
        }
    }

    public boolean isSelected() {
        return this._selected;
    }

    public void setIsMultiSelectRoot(boolean root) {
        if (this._multiSelectRoot != root) {
            this._multiSelectRoot = root;
            this.invalidate();
        }
    }

    public void onDraw() {
        byte btm_color;
        byte mid_color;
        byte top_color;
        Column[] columns = MapWidgetAnimationNode.calculateColumns(this.getWidth());
        if (this._multiSelectRoot && this.isSelected()) {
            top_color = MapColorPalette.getColor((int)219, (int)145, (int)92);
            mid_color = MapColorPalette.getColor((int)188, (int)124, (int)79);
            btm_color = MapColorPalette.getColor((int)154, (int)101, (int)64);
        } else if (this._node != null && this.isSelected() && !this._node.hasSceneMarker()) {
            top_color = MapColorPalette.getColor((int)213, (int)219, (int)92);
            mid_color = MapColorPalette.getColor((int)183, (int)188, (int)79);
            btm_color = MapColorPalette.getColor((int)150, (int)154, (int)64);
        } else if (this._node != null && this.isSelected() && this._node.hasSceneMarker()) {
            top_color = MapColorPalette.getColor((int)216, (int)76, (int)178);
            mid_color = MapColorPalette.getColor((int)186, (int)65, (int)153);
            btm_color = MapColorPalette.getColor((int)178, (int)63, (int)127);
        } else if (this._node != null && this._node.hasSceneMarker()) {
            top_color = MapColorPalette.getColor((int)97, (int)63, (int)148);
            mid_color = MapColorPalette.getColor((int)83, (int)54, (int)127);
            btm_color = MapColorPalette.getColor((int)68, (int)44, (int)104);
        } else {
            top_color = MapColorPalette.getColor((int)51, (int)127, (int)216);
            mid_color = MapColorPalette.getColor((int)44, (int)109, (int)186);
            btm_color = MapColorPalette.getColor((int)36, (int)82, (int)159);
        }
        this.view.drawLine(0, 0, this.getWidth() - 1, 0, top_color);
        this.view.fillRectangle(0, 1, this.getWidth(), this.getHeight() - 2, mid_color);
        this.view.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, btm_color);
        for (int y = 1; y < this.getHeight(); y += 2) {
            this.view.drawPixel(columns[1].x - 1, y, (byte)119);
            this.view.drawPixel(columns[2].x - 1, y, (byte)119);
        }
        if (this._node != null) {
            byte color_z;
            byte color_y;
            byte color_x;
            double time = this._node.getDuration();
            Vector pos = this._node.getPosition();
            Vector rot = this._node.getRotationVector();
            String timeStr = Util.stringifyAnimationNodeTime(time);
            byte light_green_color = MapColorPalette.getColor((int)56, (int)178, (int)127);
            byte dt_color = this._node.hasSceneMarker() ? MapColorPalette.getColor((int)133, (int)180, (int)20) : light_green_color;
            int drawTimeOffset = 1;
            int numDigits = 0;
            for (int ch_idx = 0; ch_idx < timeStr.length(); ++ch_idx) {
                char c = timeStr.charAt(ch_idx);
                if (c != '.' && c != ',') {
                    MapTexture sprite = MapFont.TINY.getSprite((Object)Character.valueOf(c));
                    this.view.draw((MapCanvas)sprite, drawTimeOffset, 0, dt_color);
                    drawTimeOffset += sprite.getWidth();
                    if (++numDigits != 4) continue;
                    break;
                }
                if (numDigits > 3) continue;
                this.view.drawPixel(drawTimeOffset, 4, dt_color);
                drawTimeOffset += 2;
            }
            if (this._node.isActive()) {
                color_x = 18;
                color_y = light_green_color;
                color_z = 50;
            } else {
                color_x = MapColorPalette.getColor((int)199, (int)199, (int)199);
                color_y = MapColorPalette.getColor((int)180, (int)180, (int)180);
                color_z = MapColorPalette.getColor((int)158, (int)144, (int)141);
            }
            this.view.drawLine(columns[1].mid, 1, columns[1].getPos(pos.getX(), this._maxPosition), 1, color_x);
            this.view.drawLine(columns[1].mid, 2, columns[1].getPos(pos.getY(), this._maxPosition), 2, color_y);
            this.view.drawLine(columns[1].mid, 3, columns[1].getPos(pos.getZ(), this._maxPosition), 3, color_z);
            this.view.drawLine(columns[2].mid, 1, columns[2].getRot(rot.getX()), 1, color_x);
            this.view.drawLine(columns[2].mid, 2, columns[2].getRot(rot.getY()), 2, color_y);
            this.view.drawLine(columns[2].mid, 3, columns[2].getRot(rot.getZ()), 3, color_z);
        }
    }

    protected static Column[] calculateColumns(int width) {
        width -= 2;
        int time_width = 20;
        width -= time_width;
        while (width > 0) {
            if ((width & 1) == 1) {
                --width;
                ++time_width;
                continue;
            }
            if ((width >> 1 & 1) == 1) break;
            --width;
            ++time_width;
        }
        return new Column[]{new Column(0, time_width), new Column(time_width + 1, width >> 1), new Column(time_width + (width >> 1) + 2, width >> 1)};
    }

    protected static final class Column {
        public final int x;
        public final int width;
        public final int mid;

        public Column(int x, int width) {
            this.x = x;
            this.width = width;
            this.mid = x + (width - 1 >> 1);
        }

        public int getPos(double value, double maximum) {
            int pixels = this.mid;
            if (value != 0.0 && maximum > 0.0) {
                pixels += (int)(value * (double)(this.width >> 1) / maximum);
            }
            return pixels;
        }

        public int getRot(double angle) {
            while (angle > 180.0) {
                angle -= 360.0;
            }
            while (angle < -180.0) {
                angle += 360.0;
            }
            return this.getPos(angle, 180.0);
        }
    }
}

