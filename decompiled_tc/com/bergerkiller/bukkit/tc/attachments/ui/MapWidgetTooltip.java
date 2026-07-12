/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapFont$Alignment
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.awt.Dimension;

public class MapWidgetTooltip
extends MapWidget {
    private String _text = null;

    public MapWidgetTooltip() {
        this.setDepthOffset(2);
    }

    public MapWidgetTooltip setText(String text) {
        if (!LogicUtil.bothNullOrEqual((Object)this._text, (Object)text)) {
            this._text = text;
            this.invalidate();
            this.calcBounds();
        }
        return this;
    }

    public String getText() {
        return this._text;
    }

    public void onAttached() {
        super.onAttached();
        this.calcBounds();
    }

    public void onDraw() {
        if (this._text == null) {
            return;
        }
        this.view.fill((byte)119);
        this.view.setAlignment(MapFont.Alignment.MIDDLE);
        this.view.draw(MapFont.MINECRAFT, this.getWidth() / 2, 0, (byte)34, (CharSequence)this._text);
    }

    private void calcBounds() {
        if (this.parent == null || this._text == null || this.view == null) {
            this.setBounds(0, 0, 0, 0);
            return;
        }
        Dimension textSize = this.view.calcFontSize(MapFont.MINECRAFT, (CharSequence)this._text);
        int parent_x = this.parent.getAbsoluteX();
        int parent_y = this.parent.getAbsoluteY();
        int pos_y = textSize.getHeight() > (double)(128 - (parent_y + this.parent.getHeight())) ? parent_y - (int)textSize.getHeight() : parent_y + this.parent.getHeight();
        int pos_x = parent_x + this.parent.getWidth() / 2 - (int)textSize.getWidth() / 2;
        if (pos_x < 0) {
            pos_x = 0;
        } else if (pos_x + (int)textSize.getWidth() > 128) {
            pos_x = 128 - (int)textSize.getWidth();
        }
        this.setBounds(pos_x -= parent_x, pos_y -= parent_y, (int)textSize.getWidth(), (int)textSize.getHeight());
    }
}

