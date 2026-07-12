/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.common.wrappers.BlockProperty
 */
package com.bergerkiller.bukkit.tc.attachments.ui.block;

import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockProperty;
import com.bergerkiller.bukkit.tc.attachments.ui.block.BlockDataSelector;
import java.awt.Dimension;

public class MapWidgetBlockStateListTooltip
extends MapWidget
implements BlockDataSelector {
    private static final int ROW_HEIGHT = 8;
    private static final int NAME_STATE_GAP = 2;
    private BlockData block = null;

    public MapWidgetBlockStateListTooltip() {
        this.setFocusable(false);
        this.setDepthOffset(2);
    }

    @Override
    public void onSelectedBlockDataChanged(BlockData blockData) {
    }

    @Override
    public BlockData getSelectedBlockData() {
        return this.block;
    }

    @Override
    public MapWidgetBlockStateListTooltip setSelectedBlockData(BlockData blockData) {
        if (this.block == blockData) {
            return this;
        }
        this.block = blockData;
        this.updateBounds();
        this.invalidate();
        return this;
    }

    public void onAttached() {
        this.updateBounds();
    }

    public void onDraw() {
        if (this.block == null) {
            return;
        }
        int y = 0;
        this.drawText(y, this.block.getBlockName());
        y += 10;
        for (BlockProperty property : this.block.getProperties()) {
            String text = property.name() + " = " + property.valueName(this.block.getProperty(property));
            this.drawText(y, text);
            y += 8;
        }
    }

    private void drawText(int y, String text) {
        Dimension size = this.view.calcFontSize(MapFont.MINECRAFT, (CharSequence)text);
        int x = (this.getWidth() - size.width) / 2;
        this.view.fillRectangle(x, y, size.width + 1, size.height, (byte)119);
        this.view.draw(MapFont.MINECRAFT, x + 1, y, (byte)34, (CharSequence)text);
        y += 8;
    }

    private void updateBounds() {
        if (this.getParent() == null) {
            return;
        }
        int x = (this.getParent().getWidth() - this.getWidth()) / 2;
        int y = this.getY();
        if (this.block != null) {
            this.setBounds(x, y, this.getWidth(), 2 + (1 + this.block.getProperties().size()) * 8);
        } else {
            this.setBounds(x, y, this.getWidth(), 0);
        }
    }
}

