/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui.block;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetBlinkyButton;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetVerticalNavigableList;
import com.bergerkiller.bukkit.tc.attachments.ui.block.BlockDataSelector;
import com.bergerkiller.bukkit.tc.attachments.ui.block.MapWidgetBlockDataVariantList;
import com.bergerkiller.bukkit.tc.attachments.ui.block.MapWidgetBlockGrid;
import com.bergerkiller.bukkit.tc.attachments.ui.block.MapWidgetBlockStateListTooltip;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MapWidgetBlockDataSelector
extends MapWidget
implements BlockDataSelector {
    private final MapWidgetVerticalNavigableList blockOptions;
    private final MapWidgetBlockStateListTooltip blockStateListTooltip;
    private final MapWidgetBlockDataVariantList variantList;
    private final MapWidgetBlockGrid blockSelector;

    public MapWidgetBlockDataSelector() {
        this.setBounds(0, 0, 100, 103);
        this.setRetainChildWidgets(true);
        this.blockOptions = (MapWidgetVerticalNavigableList)this.addWidget((MapWidget)new MapWidgetVerticalNavigableList(){

            @Override
            public void onLastItemDown(MapKeyEvent event) {
                MapWidgetBlockDataSelector.this.activateBlockGrid();
            }

            @Override
            public void onNavigated(MapKeyEvent event, MapWidgetTabView.Tab tab) {
                if (tab.getIndex() == 0) {
                    MapWidgetBlockDataSelector.this.blockStateListTooltip.setVisible(true);
                } else {
                    MapWidgetBlockDataSelector.this.blockStateListTooltip.setVisible(false);
                }
            }
        });
        this.blockOptions.setBounds(0, 0, 100, 18);
        this.blockStateListTooltip = (MapWidgetBlockStateListTooltip)this.addWidget(new MapWidgetBlockStateListTooltip());
        this.blockStateListTooltip.setBounds(0, 18, 128, 0);
        this.variantList = new MapWidgetBlockDataVariantList(){

            @Override
            public void onSelectedBlockDataChanged(BlockData blockData) {
                MapWidgetBlockDataSelector.this.blockStateListTooltip.setSelectedBlockData(blockData);
                MapWidgetBlockDataSelector.this.variantList.setSelectedBlockData(blockData);
                MapWidgetBlockDataSelector.this.onSelectedBlockDataChanged(blockData);
            }

            @Override
            public void onKeyPressed(MapKeyEvent event) {
                if (event.getKey() == MapPlayerInput.Key.ENTER) {
                    MapWidgetBlockDataSelector.this.activateBlockGrid();
                } else {
                    super.onKeyPressed(event);
                }
            }

            public boolean onItemDrop(Player player, ItemStack item) {
                return MapWidgetBlockDataSelector.this.onItemDrop(player, item);
            }
        };
        this.variantList.setPosition(0, 0);
        this.blockOptions.addTab().addWidget((MapWidget)this.variantList);
        this.blockOptions.setSelectedIndex(0);
        MapWidgetTabView.Tab tab = this.blockOptions.addTab(new MapWidgetTabView.Tab(){
            private final MapTexture bg_texture = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/item_options_bg.png");

            public void onDraw() {
                this.view.draw((MapCanvas)this.bg_texture, 0, 0);
            }
        });
        this.blockSelector = (this.addWidget(new MapWidgetBlockGrid(){

            @Override
            public void onSelectedBlockDataChanged(BlockData blockData) {
                MapWidgetBlockDataSelector.this.variantList.setSelectedBlockData(blockData);
                MapWidgetBlockDataSelector.this.blockStateListTooltip.setSelectedBlockData(blockData);
                MapWidgetBlockDataSelector.this.onSelectedBlockDataChanged(blockData);
            }

            @Override
            public void onKeyPressed(MapKeyEvent event) {
                if (event.getKey() == MapPlayerInput.Key.BACK || event.getKey() == MapPlayerInput.Key.ENTER) {
                    MapWidgetBlockDataSelector.this.blockStateListTooltip.setVisible(true);
                    MapWidgetBlockDataSelector.this.variantList.focus();
                } else {
                    super.onKeyPressed(event);
                }
            }

            public boolean onItemDrop(Player player, ItemStack item) {
                return MapWidgetBlockDataSelector.this.onItemDrop(player, item);
            }

            public void onBlockInteract(PlayerInteractEvent event) {
                MapWidgetBlockDataSelector.this.onBlockInteract(event);
            }
        })).setDimensions(6, 4);
        this.blockSelector.addAllBlocks();
        this.blockSelector.setPosition(0, 20);
    }

    public void showBrightnessButton() {
        MapWidgetBlinkyButton brightnessButton = new MapWidgetBlinkyButton(){

            @Override
            public void onClick() {
                MapWidgetBlockDataSelector.this.onBrightnessClicked();
            }
        };
        brightnessButton.setSize(14, 14);
        brightnessButton.setIcon("attachments/item_brightness.png");
        brightnessButton.setTooltip("Block Brightness");
        brightnessButton.setPosition(42, 2);
        this.blockOptions.getTab(1).addWidget((MapWidget)brightnessButton);
    }

    private void activateBlockGrid() {
        this.blockStateListTooltip.setVisible(false);
        this.blockOptions.setSelectedIndex(0);
        this.blockSelector.activate();
    }

    @Override
    public BlockData getSelectedBlockData() {
        return this.variantList.getSelectedBlockData();
    }

    @Override
    public MapWidgetBlockDataSelector setSelectedBlockData(BlockData blockData) {
        this.blockSelector.setSelectedBlockData(blockData);
        this.blockStateListTooltip.setSelectedBlockData(blockData);
        this.variantList.setSelectedBlockData(blockData);
        return this;
    }

    public boolean onItemDrop(Player player, ItemStack item) {
        BlockData data = BlockData.fromItemStack((ItemStack)item);
        if (data != null && data != BlockData.AIR) {
            this.setSelectedBlockData(data);
            this.display.playSound(SoundEffect.CLICK_WOOD);
            this.onSelectedBlockDataChanged(data);
            return true;
        }
        return false;
    }

    public void onBlockInteract(PlayerInteractEvent event) {
        Block block;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (block = event.getClickedBlock()) != null) {
            BlockData data = WorldUtil.getBlockData((Block)block);
            this.setSelectedBlockData(data);
            this.display.playSound(SoundEffect.CLICK_WOOD);
            this.onSelectedBlockDataChanged(data);
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    public void onBrightnessClicked() {
    }
}

