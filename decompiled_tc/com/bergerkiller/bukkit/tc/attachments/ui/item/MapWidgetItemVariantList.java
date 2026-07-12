/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapFont$Alignment
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  org.bukkit.Material
 *  org.bukkit.block.BlockFace
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui.item;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetArrow;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetTooltip;
import com.bergerkiller.bukkit.tc.attachments.ui.SetValueTarget;
import com.bergerkiller.bukkit.tc.attachments.ui.item.ItemChangedListener;
import com.bergerkiller.bukkit.tc.attachments.ui.models.ResourcePackModelListing;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedItemModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MapWidgetItemVariantList
extends MapWidget
implements SetValueTarget,
ItemChangedListener {
    private final List<ItemChangedListener> itemChangedListeners = new ArrayList<ItemChangedListener>();
    private final MapWidgetArrow nav_left = new MapWidgetArrow(BlockFace.WEST);
    private final MapWidgetArrow nav_right = new MapWidgetArrow(BlockFace.EAST);
    private final MapWidgetTooltip below_tooltip = new MapWidgetTooltip();
    private final MapTexture background;
    private List<CommonItemStack> variants;
    private Map<CommonItemStack, MapTexture> iconCache = new HashMap<CommonItemStack, MapTexture>();
    private int variantIndex = 0;

    public MapWidgetItemVariantList() {
        this.background = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/item_selector_bg.png");
        this.setSize(100, 18);
        this.setFocusable(true);
        this.variants = new ArrayList<CommonItemStack>(0);
        this.nav_left.setPosition(0, 4);
        this.nav_right.setPosition(this.getWidth() - this.nav_right.getWidth(), 4);
        this.nav_left.setVisible(false);
        this.nav_right.setVisible(false);
        this.addWidget(this.nav_left);
        this.addWidget(this.nav_right);
        this.addWidget(this.below_tooltip);
        this.setRetainChildWidgets(true);
        this.itemChangedListeners.add(this);
    }

    public CommonItemStack getItem() {
        if (this.variantIndex >= 0 && this.variantIndex < this.variants.size()) {
            return this.variants.get(this.variantIndex);
        }
        return CommonItemStack.empty();
    }

    public void setItem(ItemStack item) {
        this.setItem(CommonItemStack.of((ItemStack)item));
    }

    public void setItem(CommonItemStack item) {
        this.loadVariants(item);
        if (item.isEmpty()) {
            this.invalidate();
            this.fireItemChangeEvent();
            return;
        }
        this.variantIndex = 0;
        for (int i = 0; i < this.variants.size(); ++i) {
            CommonItemStack variant = this.variants.get(i);
            if (variant.equalsIgnoreAmount(item)) {
                this.variantIndex = i;
                break;
            }
            if (!item.isDamageSupported() || variant.getDamage() != item.getDamage()) continue;
            this.variantIndex = i;
        }
        this.invalidate();
        this.fireItemChangeEvent();
    }

    /*
     * WARNING - void declaration
     */
    private void loadVariants(CommonItemStack item) {
        if (item.isEmpty()) {
            this.variants = new ArrayList<CommonItemStack>();
            this.variantIndex = 0;
            return;
        }
        ResourcePackModelListing models = TrainCarts.plugin.getModelListing();
        if (models.isBareItem(item.toBukkit())) {
            this.variants = new ArrayList<CommonItemStack>(models.root().bareItemStacks().keySet());
            return;
        }
        if (item.isDamageSupported()) {
            void var4_7;
            int maxDamage = item.getMaxDamage();
            this.variants = new ArrayList<CommonItemStack>(maxDamage + 1);
            boolean bl = false;
            while (var4_7 <= maxDamage) {
                this.variants.add(item.clone().setDamage((int)var4_7));
                ++var4_7;
            }
            return;
        }
        this.variants = ItemUtil.getItemVariants((Material)item.getType()).stream().filter(Objects::nonNull).map(CommonItemStack::of).map(CommonItemStack::clone).collect(Collectors.toList());
        if (this.variants.size() == 1) {
            this.variants.get(0).toBukkit().setItemMeta(item.toBukkit().getItemMeta());
        } else {
            for (CommonItemStack commonItemStack : this.variants) {
                for (Map.Entry enchantment : item.toBukkit().getEnchantments().entrySet()) {
                    commonItemStack.addEnchantment((Enchantment)enchantment.getKey(), ((Integer)enchantment.getValue()).intValue());
                }
            }
            if (item.hasCustomName()) {
                ChatText customName = item.getCustomName();
                for (CommonItemStack variant : this.variants) {
                    variant.setCustomName(customName);
                }
            }
            if (item.isUnbreakable()) {
                for (CommonItemStack commonItemStack : this.variants) {
                    commonItemStack.setUnbreakable(true);
                }
            }
            if (item.hasCustomModelData()) {
                int customModelData = item.getCustomModelData();
                for (CommonItemStack variant : this.variants) {
                    variant.setCustomModelData(customModelData);
                }
            }
        }
    }

    @Override
    public String getAcceptedPropertyName() {
        return "Item Information";
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
            CommonItemStack newItem = CommonItemStack.create((Material)newItemMaterial, (int)1);
            for (nameEnd = 0; nameEnd < value.length() && value.charAt(nameEnd) != '{' && value.charAt(nameEnd) != ' '; ++nameEnd) {
            }
            String damageValueStr = value.substring(0, nameEnd).trim();
            if (!damageValueStr.isEmpty() && newItem.isDamageSupported() && ParseUtil.isNumeric((String)damageValueStr)) {
                try {
                    int damage = Integer.parseInt(damageValueStr);
                    if (damage < 0 || damage > newItem.getMaxDamage()) {
                        return false;
                    }
                    newItem.setDamage(damage);
                }
                catch (NumberFormatException ex) {
                    return false;
                }
            }
            this.setItem(newItem);
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
        CommonItemStack item;
        int selector_edge = this.nav_left.getWidth() + 1;
        MapCanvas itemView = this.view.getView(selector_edge, 0, this.getWidth() - 2 * selector_edge, this.getHeight());
        itemView.draw((MapCanvas)this.background, 0, 0);
        int x = 1;
        int y = 1;
        for (int index = this.variantIndex - 2; index <= this.variantIndex + 2; ++index) {
            if (index >= 0 && index < this.variants.size()) {
                CommonItemStack item2 = this.variants.get(index);
                MapTexture icon = this.iconCache.get(item2);
                if (icon == null) {
                    icon = MapTexture.createEmpty((int)16, (int)16);
                    icon.fillItem(TCConfig.resourcePack, item2.toBukkit());
                    this.iconCache.put(item2, icon);
                }
                itemView.draw((MapCanvas)icon, x, y);
            }
            x += 17;
        }
        if (this.variantIndex >= 0 && this.variantIndex < this.variants.size() && (item = this.variants.get(this.variantIndex)).isDamageSupported()) {
            itemView.setAlignment(MapFont.Alignment.MIDDLE);
            itemView.draw(MapFont.TINY, 44, 12, (byte)18, (CharSequence)Integer.toString(item.getDamage()));
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
        this.fireItemChangeEvent();
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

    public void registerItemChangedListener(ItemChangedListener listener, boolean fireEventNow) {
        this.itemChangedListeners.add(listener);
        if (fireEventNow) {
            listener.onItemChanged(this.getItem());
        }
    }

    private void fireItemChangeEvent() {
        CommonItemStack item = this.getItem();
        ListedItemModel itemMeta = TrainCarts.plugin.getModelListing().getBareItemModel(item.toBukkit());
        if (itemMeta != null) {
            this.below_tooltip.setText(itemMeta.name());
            this.below_tooltip.setVisible(true);
        } else {
            this.below_tooltip.setText("");
            this.below_tooltip.setVisible(false);
        }
        for (ItemChangedListener listener : this.itemChangedListeners) {
            listener.onItemChanged(item);
        }
    }

    @Override
    public void onItemChanged(CommonItemStack item) {
    }
}

