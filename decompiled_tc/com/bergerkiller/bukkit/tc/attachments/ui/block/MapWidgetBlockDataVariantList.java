/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.common.wrappers.BlockProperty
 *  org.bukkit.Material
 *  org.bukkit.block.BlockFace
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui.block;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockProperty;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetArrow;
import com.bergerkiller.bukkit.tc.attachments.ui.SetValueTarget;
import com.bergerkiller.bukkit.tc.attachments.ui.block.BlockDataSelector;
import com.bergerkiller.bukkit.tc.attachments.ui.block.BlockDataTextureCache;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MapWidgetBlockDataVariantList
extends MapWidget
implements SetValueTarget,
BlockDataSelector {
    private final MapWidgetArrow nav_left = new MapWidgetArrow(BlockFace.WEST);
    private final MapWidgetArrow nav_right = new MapWidgetArrow(BlockFace.EAST);
    private final MapTexture background;
    private List<BlockData> variants;
    private final BlockDataTextureCache iconCache = BlockDataTextureCache.get(16, 16);
    private int variantIndex = 0;

    public MapWidgetBlockDataVariantList() {
        this.background = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/item_selector_bg.png");
        this.setSize(100, 18);
        this.setFocusable(true);
        this.variants = new ArrayList<BlockData>(0);
        this.nav_left.setPosition(0, 4);
        this.nav_right.setPosition(this.getWidth() - this.nav_right.getWidth(), 4);
        this.nav_left.setVisible(false);
        this.nav_right.setVisible(false);
        this.addWidget(this.nav_left);
        this.addWidget(this.nav_right);
        this.setRetainChildWidgets(true);
    }

    @Override
    public BlockData getSelectedBlockData() {
        if (this.variantIndex >= 0 && this.variantIndex < this.variants.size()) {
            return this.variants.get(this.variantIndex);
        }
        return null;
    }

    @Override
    public MapWidgetBlockDataVariantList setSelectedBlockData(BlockData blockData) {
        this.setSelectedBlockData(blockData, false);
        return this;
    }

    private void setSelectedBlockData(BlockData blockData, boolean fireEvent) {
        if (this.getSelectedBlockData() == blockData) {
            return;
        }
        if (blockData == null) {
            this.variants = new ArrayList<BlockData>(0);
            this.variantIndex = 0;
            this.invalidate();
            if (fireEvent) {
                this.onSelectedBlockDataChanged(null);
            }
            return;
        }
        this.variants.clear();
        this.variants.add(blockData);
        for (BlockProperty property : blockData.getProperties()) {
            ArrayList<BlockData> tmp = new ArrayList<BlockData>(this.variants);
            this.variants.clear();
            for (Comparable value : property.values()) {
                for (BlockData original : tmp) {
                    try {
                        this.variants.add(original.setProperty(property, (Object)value));
                    }
                    catch (Throwable throwable) {}
                }
            }
        }
        this.variantIndex = 0;
        for (int i = 0; i < this.variants.size(); ++i) {
            BlockData variant = this.variants.get(i);
            if (!variant.equals((Object)blockData)) continue;
            this.variantIndex = i;
            break;
        }
        this.invalidate();
        if (fireEvent) {
            this.onSelectedBlockDataChanged(blockData);
        }
    }

    @Override
    public String getAcceptedPropertyName() {
        return "Block Information";
    }

    @Override
    public boolean acceptTextValue(String value) {
        int nameEnd;
        value = value.trim();
        for (nameEnd = 0; nameEnd < value.length() && value.charAt(nameEnd) != '{' && value.charAt(nameEnd) != ' '; ++nameEnd) {
        }
        String itemName = value.substring(0, nameEnd);
        value = nameEnd >= value.length() ? "" : value.substring(nameEnd).trim();
        if (!ParseUtil.isNumeric((String)itemName)) {
            Material newItemMaterial = ParseUtil.parseMaterial((String)itemName, null);
            if (newItemMaterial == null) {
                return false;
            }
            BlockData newBlock = BlockData.fromMaterial((Material)newItemMaterial);
            this.setSelectedBlockData(newBlock, true);
        } else {
            try {
                this.setVariantIndex(Integer.parseInt(itemName));
            }
            catch (NumberFormatException ex) {
                return false;
            }
        }
        return true;
    }

    public void onFocus() {
        this.nav_left.setVisible(true);
        this.nav_right.setVisible(true);
    }

    public void onBlur() {
        this.nav_left.setVisible(false);
        this.nav_right.setVisible(false);
    }

    public void onDraw() {
        int selector_edge = this.nav_left.getWidth() + 1;
        MapCanvas itemView = this.view.getView(selector_edge, 0, this.getWidth() - 2 * selector_edge, this.getHeight());
        itemView.draw((MapCanvas)this.background, 0, 0);
        int x = 1;
        int y = 1;
        for (int index = this.variantIndex - 2; index <= this.variantIndex + 2; ++index) {
            if (index >= 0 && index < this.variants.size()) {
                itemView.draw((MapCanvas)this.iconCache.get(this.variants.get(index)), x, y);
            }
            x += 17;
        }
        if (this.isFocused()) {
            int fx = 35;
            int fy = 1;
            itemView.drawRectangle(fx, fy, 16, 16, (byte)18);
        }
    }

    private void changeVariantIndex(int offset) {
        this.setVariantIndex(this.variantIndex + offset);
    }

    private void setVariantIndex(int newVariantIndex) {
        if (newVariantIndex < 0) {
            newVariantIndex = 0;
        } else if (newVariantIndex >= this.variants.size()) {
            newVariantIndex = this.variants.size() - 1;
        }
        if (this.variantIndex == newVariantIndex) {
            return;
        }
        this.variantIndex = newVariantIndex;
        this.invalidate();
        this.onSelectedBlockDataChanged(this.getSelectedBlockData());
        this.display.playSound(SoundEffect.CLICK);
    }

    public void onKeyReleased(MapKeyEvent event) {
        super.onKeyReleased(event);
        if (event.getKey() == MapPlayerInput.Key.LEFT) {
            this.nav_left.stopFocus();
        } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
            this.nav_right.stopFocus();
        }
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (event.getKey() == MapPlayerInput.Key.LEFT) {
            this.changeVariantIndex(-1 - event.getRepeat() / 40);
            this.nav_left.sendFocus();
        } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
            this.changeVariantIndex(1 + event.getRepeat() / 40);
            this.nav_right.sendFocus();
        } else {
            super.onKeyPressed(event);
        }
    }
}

