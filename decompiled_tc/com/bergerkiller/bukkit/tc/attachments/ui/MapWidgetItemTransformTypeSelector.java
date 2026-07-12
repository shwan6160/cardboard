/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.config.transform.ItemTransformType;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSelectionBox;
import java.util.Arrays;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MapWidgetItemTransformTypeSelector
extends MapWidget {
    private MapWidgetSelectionBox categorySelector;
    private TransformTypeSelector typeSelector;
    private ItemTransformType selectedType = ItemTransformType.Category.ARMORSTAND.defaultType();
    public static final MapFont<Character> ITEMTRANSFORMTYPE_FONT = new MapFont<Character>(){
        private MapTexture FONT_TEXTURE = null;

        protected MapTexture loadSprite(Character key) {
            if (this.FONT_TEXTURE == null) {
                this.FONT_TEXTURE = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/font.png");
            }
            if (key.charValue() == '\u24b6') {
                return this.FONT_TEXTURE.getView(0, 0, 5, 8).clone();
            }
            if (key.charValue() == '\u24b9') {
                return this.FONT_TEXTURE.getView(6, 0, 5, 8).clone();
            }
            if (key.charValue() == '\u24ae') {
                return this.FONT_TEXTURE.getView(12, 0, 5, 8).clone();
            }
            return MINECRAFT.getSprite((Object)key);
        }

        public boolean isNewline(Character key) {
            return key != null && key.charValue() == '\n';
        }
    };

    public abstract void onSelectedTypeChanged(ItemTransformType var1);

    public static int defaultHeight() {
        return CommonCapabilities.HAS_DISPLAY_ENTITY ? 23 : 11;
    }

    public MapWidgetItemTransformTypeSelector() {
        this.setSize(100, MapWidgetItemTransformTypeSelector.defaultHeight());
    }

    public void onAttached() {
        if (CommonCapabilities.HAS_DISPLAY_ENTITY) {
            this.categorySelector = (MapWidgetSelectionBox)this.addWidget(new MapWidgetSelectionBox(){
                private boolean changingItems = false;

                @Override
                public void onAttached() {
                    super.onAttached();
                    this.setFont(ITEMTRANSFORMTYPE_FONT);
                    this.changingItems = true;
                    for (ItemTransformType.Category category : ItemTransformType.Category.values()) {
                        this.addItem(category.toString());
                    }
                    this.setSelectedIndex(Arrays.asList(ItemTransformType.Category.values()).indexOf((Object)MapWidgetItemTransformTypeSelector.this.selectedType.category()));
                    this.changingItems = false;
                }

                @Override
                public void onSelectedItemChanged() {
                    if (!this.changingItems && this.getSelectedIndex() != -1) {
                        ItemTransformType.Category newCategory = ItemTransformType.Category.values()[this.getSelectedIndex()];
                        ItemTransformType newType = MapWidgetItemTransformTypeSelector.this.selectedType.switchCategory(newCategory);
                        if (!newType.equals(MapWidgetItemTransformTypeSelector.this.selectedType)) {
                            MapWidgetItemTransformTypeSelector.this.selectedType = newType;
                            MapWidgetItemTransformTypeSelector.this.typeSelector.updateItems();
                            MapWidgetItemTransformTypeSelector.this.onSelectedTypeChanged(MapWidgetItemTransformTypeSelector.this.selectedType);
                        }
                    }
                }
            });
            this.categorySelector.setClipParent(this.isClipParent());
        }
        this.typeSelector = (TransformTypeSelector)this.addWidget(new TransformTypeSelector());
        this.typeSelector.setClipParent(this.isClipParent());
        this.onBoundsChanged();
    }

    public void onDetached() {
        this.categorySelector = null;
        this.typeSelector = null;
    }

    public void onBoundsChanged() {
        if (this.categorySelector != null) {
            int sliderHeight = (this.getHeight() - 1) / 2;
            this.categorySelector.setBounds(0, 0, this.getWidth(), sliderHeight);
            this.typeSelector.setBounds(0, this.getHeight() - sliderHeight, this.getWidth(), sliderHeight);
        } else {
            this.typeSelector.setBounds(0, 0, this.getWidth(), this.getHeight());
        }
    }

    public ItemTransformType getSelectedType() {
        return this.selectedType;
    }

    public void setSelectedType(ItemTransformType selectedType) {
        this.selectedType = selectedType;
        if (this.typeSelector != null) {
            if (this.categorySelector != null) {
                this.categorySelector.setSelectedIndex(Arrays.asList(ItemTransformType.Category.values()).indexOf((Object)selectedType.category()));
            }
            this.typeSelector.setSelectedIndex(selectedType.category().types().indexOf(selectedType));
        }
    }

    private class TransformTypeSelector
    extends MapWidgetSelectionBox {
        private boolean changingItems = false;

        private TransformTypeSelector() {
        }

        @Override
        public void onAttached() {
            super.onAttached();
            this.setFont(ITEMTRANSFORMTYPE_FONT);
            this.updateItems();
        }

        public void updateItems() {
            this.changingItems = true;
            this.clearItems();
            ItemTransformType shownSelectedType = MapWidgetItemTransformTypeSelector.this.selectedType;
            if (!CommonCapabilities.HAS_DISPLAY_ENTITY) {
                shownSelectedType = shownSelectedType.switchCategory(ItemTransformType.Category.ARMORSTAND);
            }
            for (ItemTransformType type : shownSelectedType.category().types()) {
                this.addItem(type.typeName());
            }
            this.setSelectedIndex(shownSelectedType.category().types().indexOf(shownSelectedType));
            this.changingItems = false;
        }

        @Override
        public void onSelectedItemChanged() {
            if (!this.changingItems && this.getSelectedIndex() != -1) {
                MapWidgetItemTransformTypeSelector.this.selectedType = MapWidgetItemTransformTypeSelector.this.selectedType.category().types().get(this.getSelectedIndex());
                MapWidgetItemTransformTypeSelector.this.onSelectedTypeChanged(MapWidgetItemTransformTypeSelector.this.selectedType);
            }
        }
    }
}

