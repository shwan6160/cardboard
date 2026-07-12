/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui.item;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.ui.ItemDropTarget;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetBlinkyButton;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetVerticalNavigableList;
import com.bergerkiller.bukkit.tc.attachments.ui.SetValueTarget;
import com.bergerkiller.bukkit.tc.attachments.ui.item.CustomModelDataSelector;
import com.bergerkiller.bukkit.tc.attachments.ui.item.ItemChangedListener;
import com.bergerkiller.bukkit.tc.attachments.ui.item.MapWidgetItemGrid;
import com.bergerkiller.bukkit.tc.attachments.ui.item.MapWidgetItemPreview;
import com.bergerkiller.bukkit.tc.attachments.ui.item.MapWidgetItemVariantList;
import com.bergerkiller.bukkit.tc.attachments.ui.models.ResourcePackModelListing;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MapWidgetItemSelector
extends MapWidget
implements ItemDropTarget,
SetValueTarget {
    private final MapWidgetVerticalNavigableList itemOptions = new MapWidgetVerticalNavigableList(){

        @Override
        public boolean shouldInterceptInput(MapKeyEvent event) {
            return !(this.root.getActivatedWidget() instanceof CustomModelDataSelector);
        }

        @Override
        public void onLastItemDown(MapKeyEvent event) {
            this.display.playSound(SoundEffect.PISTON_CONTRACT);
            this.setSelectedIndex(0);
            MapWidgetItemSelector.this.setGridOpened(true);
        }
    };
    private final MapWidgetItemVariantList variantList = new MapWidgetItemVariantList(){

        public void onActivate() {
            ResourcePackModelListing listing = TrainCarts.plugin.getModelListing();
            if (listing.isEmpty()) {
                MapWidgetItemSelector.this.setGridOpened(true);
                return;
            }
            for (Player owner : this.display.getOwners()) {
                if (!this.display.isControlling(owner)) continue;
                listing.buildDialog(owner).cancelOnRootRightClick(false).title("Select an item model").setCompactingEnabled(TrainCarts.plugin.getPlayer(owner).getModelSearchCompactFolders()).show().thenAccept(result -> {
                    if (result.success()) {
                        MapWidgetItemSelector.this.setSelectedItem(result.selectedBareItem());
                    }
                });
            }
        }
    };
    private final MapWidgetItemPreview preview = new MapWidgetItemPreview(){};
    private final MapWidgetItemGrid grid = new MapWidgetItemGrid(){

        @Override
        public void onSelectionChanged() {
            MapWidgetItemSelector.this.variantList.setItem(this.getSelectedItem());
        }

        public void onAttached() {
            this.setSelectedItem(MapWidgetItemSelector.this.variantList.getItem());
        }

        @Override
        public void onKeyPressed(MapKeyEvent event) {
            if (event.getKey() == MapPlayerInput.Key.ENTER || event.getKey() == MapPlayerInput.Key.BACK) {
                MapWidgetItemSelector.this.setGridOpened(false);
                return;
            }
            super.onKeyPressed(event);
        }
    };
    private final MapWidgetBlinkyButton brightnessButton;

    public MapWidgetItemSelector() {
        this.grid.setDimensions(6, 4);
        this.itemOptions.setSize(100, 18);
        this.itemOptions.setPosition((this.grid.getWidth() - this.itemOptions.getWidth()) / 2, 0);
        this.grid.setPosition(0, this.itemOptions.getHeight() + 1);
        this.grid.addCreativeItems();
        this.preview.setBounds(this.grid.getX(), this.grid.getY(), this.grid.getWidth(), this.grid.getHeight());
        this.setSize(this.grid.getWidth(), this.grid.getY() + this.grid.getHeight());
        this.itemOptions.addTab().addWidget((MapWidget)this.variantList);
        MapWidgetTabView.Tab tab = this.itemOptions.addTab(new MapWidgetTabView.Tab(){
            private final MapTexture bg_texture = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/item_options_bg.png");

            public void onDraw() {
                this.view.draw((MapCanvas)this.bg_texture, 0, 0);
            }
        });
        final MapWidgetBlinkyButton unbreakableOption = new MapWidgetBlinkyButton(){

            protected MapWidget navigateNextWidget(List<MapWidget> widgets, MapPlayerInput.Key key) {
                if (key == MapPlayerInput.Key.LEFT) {
                    return null;
                }
                return super.navigateNextWidget(widgets, key);
            }

            @Override
            public void onClick() {
                CommonItemStack item = MapWidgetItemSelector.this.variantList.getItem();
                if (item.isEmpty()) {
                    return;
                }
                item.setUnbreakable(!(item = item.clone()).isUnbreakable());
                MapWidgetItemSelector.this.variantList.setItem(item);
            }
        };
        unbreakableOption.setSize(14, 14);
        this.variantList.registerItemChangedListener(new ItemChangedListener(){
            final /* synthetic */ MapWidgetItemSelector this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onItemChanged(CommonItemStack item) {
                if (item.isUnbreakable()) {
                    unbreakableOption.setTooltip("Unbreakable");
                    unbreakableOption.setIcon("attachments/item_unbreakable.png");
                } else {
                    unbreakableOption.setTooltip("Breakable");
                    unbreakableOption.setIcon("attachments/item_breakable.png");
                }
            }
        }, true);
        tab.addWidget(unbreakableOption.setPosition(8, 2));
        final MapWidgetSubmitText nameItemTextBox = new MapWidgetSubmitText(){

            public void onAttached() {
                this.setDescription("Enter Item Display Name\nUse empty space to reset");
            }

            public void onAccept(String text) {
                CommonItemStack item = MapWidgetItemSelector.this.variantList.getItem();
                if (item.isEmpty()) {
                    return;
                }
                item = item.clone();
                if (text.trim().isEmpty()) {
                    item.setCustomName(null);
                } else {
                    item.setCustomNameMessage(text);
                }
                MapWidgetItemSelector.this.variantList.setItem(item);
            }
        };
        tab.addWidget((MapWidget)nameItemTextBox);
        final MapWidgetBlinkyButton nameItemButton = new MapWidgetBlinkyButton(this){
            final /* synthetic */ MapWidgetItemSelector this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onClick() {
                nameItemTextBox.activate();
            }
        };
        nameItemButton.setSize(14, 14);
        nameItemButton.setIcon("attachments/item_named.png");
        tab.addWidget(nameItemButton.setPosition(23, 2));
        this.variantList.registerItemChangedListener(new ItemChangedListener(){
            final /* synthetic */ MapWidgetItemSelector this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onItemChanged(CommonItemStack item) {
                if (item.hasCustomName()) {
                    nameItemButton.setTooltip("Name (\"" + item.getCustomNameMessage() + "\")");
                } else {
                    nameItemButton.setTooltip("Name (None)");
                }
            }
        }, true);
        this.brightnessButton = new MapWidgetBlinkyButton(){

            @Override
            public void onClick() {
                MapWidgetItemSelector.this.onBrightnessClicked();
            }
        };
        this.brightnessButton.setSize(14, 14);
        this.brightnessButton.setIcon("attachments/item_brightness.png");
        this.brightnessButton.setVisible(false);
        this.brightnessButton.setTooltip("Item Brightness");
        tab.addWidget(this.brightnessButton.setPosition(38, 2));
        final CustomModelDataSelector selector = new CustomModelDataSelector(){

            protected MapWidget navigateNextWidget(List<MapWidget> widgets, MapPlayerInput.Key key) {
                if (key == MapPlayerInput.Key.RIGHT) {
                    return null;
                }
                return super.navigateNextWidget(widgets, key);
            }

            @Override
            public void onValueChanged() {
                CommonItemStack item = MapWidgetItemSelector.this.variantList.getItem();
                if (item.isEmpty()) {
                    return;
                }
                item = item.clone();
                if (this.getValue() <= 0) {
                    item.clearCustomModelData();
                } else {
                    item.setCustomModelData(this.getValue());
                }
                MapWidgetItemSelector.this.variantList.setItem(item);
            }
        };
        selector.setPosition(54, 2);
        tab.addWidget((MapWidget)selector);
        this.variantList.registerItemChangedListener(new ItemChangedListener(){
            final /* synthetic */ MapWidgetItemSelector this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onItemChanged(CommonItemStack item) {
                int value = item.hasCustomModelData() ? item.getCustomModelData() : 0;
                selector.setValue(value);
            }
        }, true);
        this.variantList.registerItemChangedListener(new ItemChangedListener(){

            @Override
            public void onItemChanged(CommonItemStack item) {
                MapWidgetItemSelector.this.preview.setItem(item.toBukkit());
                MapWidgetItemSelector.this.onSelectedItemChanged();
            }
        }, false);
    }

    public void setShowBrightnessButton(boolean show) {
        this.brightnessButton.setVisible(show);
    }

    public MapWidgetItemSelector setSelectedItem(ItemStack item) {
        this.variantList.setItem(item);
        return this;
    }

    public ItemStack getSelectedItem() {
        return this.variantList.getItem().toBukkit();
    }

    public void onAttached() {
        this.addWidget((MapWidget)this.itemOptions);
        this.setGridOpened(false);
    }

    @Override
    public boolean acceptItem(ItemStack item) {
        this.setGridOpened(false);
        this.variantList.setItem(item);
        this.display.playSound(SoundEffect.CLICK_WOOD);
        return true;
    }

    @Override
    public String getAcceptedPropertyName() {
        return this.variantList.getAcceptedPropertyName();
    }

    @Override
    public boolean acceptTextValue(String value) {
        return this.variantList.acceptTextValue(value);
    }

    private void setGridOpened(boolean opened) {
        if (!opened && !this.getWidgets().contains((Object)this.preview)) {
            boolean focus = this.getWidgets().contains(this.grid);
            this.swapWidget(this.grid, this.preview);
            if (focus) {
                this.itemOptions.focus();
            }
        } else if (opened && !this.getWidgets().contains(this.grid)) {
            ((MapWidgetItemGrid)this.swapWidget(this.preview, this.grid)).activate();
        }
    }

    public abstract void onSelectedItemChanged();

    public void onBrightnessClicked() {
    }
}

