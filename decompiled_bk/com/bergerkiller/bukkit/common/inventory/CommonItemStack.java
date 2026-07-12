/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemFlag
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.inventory.CommonItemMaterials;
import com.bergerkiller.bukkit.common.map.util.ModelInfoLookup;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.CustomModelData;
import com.bergerkiller.bukkit.common.wrappers.ItemRenderOptions;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CommonItemStack
implements Cloneable {
    private ItemStack bukkitItemStack;
    private Optional<ItemStackHandle> itemStackHandle = null;

    public static boolean isCraftItemStack(ItemStack itemStack) {
        return CraftItemStackHandle.T.isAssignableFrom(itemStack);
    }

    public static CommonItemStack of(ItemStack itemStack) {
        return new CommonItemStack(itemStack);
    }

    public static CommonItemStack copyOf(ItemStack itemStack) {
        return CommonItemStack.of(ItemUtil.cloneItem(itemStack));
    }

    public static CommonItemStack empty() {
        return new CommonItemStack(null);
    }

    public static CommonItemStack create(Material type, int amount) {
        return CommonItemStack.of(ItemUtil.createItem(type, amount));
    }

    public static CommonItemStack createPlayerSkull(GameProfileHandle profile) {
        return CommonItemStack.create(CommonItemMaterials.SKULL, 1).setSkullProfile(profile);
    }

    private CommonItemStack(ItemStack bukkitItemStack) {
        this.bukkitItemStack = bukkitItemStack;
    }

    private void invalidateItemStackHandle() {
        this.itemStackHandle = null;
    }

    public void clear() {
        this.bukkitItemStack = null;
        this.invalidateItemStackHandle();
    }

    public CommonItemStack setTo(CommonItemStack commonItemStack) {
        this.bukkitItemStack = commonItemStack.bukkitItemStack;
        this.itemStackHandle = commonItemStack.itemStackHandle;
        return this;
    }

    public CommonItemStack setTo(ItemStack itemStack) {
        this.bukkitItemStack = itemStack;
        this.invalidateItemStackHandle();
        return this;
    }

    public CommonItemStack setToCopyOf(CommonItemStack commonItemStack) {
        this.bukkitItemStack = commonItemStack.isEmpty() ? null : commonItemStack.bukkitItemStack.clone();
        this.invalidateItemStackHandle();
        return this;
    }

    public CommonItemStack setToCopyOf(ItemStack itemStack) {
        this.bukkitItemStack = ItemUtil.cloneItem(itemStack);
        this.invalidateItemStackHandle();
        return this;
    }

    public boolean isCraftItemStack() {
        return CommonItemStack.isCraftItemStack(this.bukkitItemStack);
    }

    public Optional<ItemStackHandle> getHandle() {
        return this.getHandle(false);
    }

    private Optional<ItemStackHandle> getHandle(boolean detach) {
        Object nmsHandle;
        Optional<ItemStackHandle> cached = this.itemStackHandle;
        if (cached != null) {
            return cached;
        }
        ItemStack bukkitItemStack = this.bukkitItemStack;
        if (!CommonItemStack.isCraftItemStack(bukkitItemStack)) {
            if (ItemUtil.isEmpty(bukkitItemStack)) {
                this.itemStackHandle = cached = Optional.empty();
                return cached;
            }
            if (detach) {
                Object nmsHandle2 = CraftItemStackHandle.T.handle.get(bukkitItemStack = CraftItemStackHandle.asCraftCopy(bukkitItemStack));
                if (nmsHandle2 != null) {
                    return Optional.of(ItemStackHandle.createHandle(nmsHandle2));
                }
                return Optional.empty();
            }
            this.bukkitItemStack = bukkitItemStack = CraftItemStackHandle.asCraftCopy(bukkitItemStack);
        }
        if ((nmsHandle = CraftItemStackHandle.T.handle.get(bukkitItemStack)) == null) {
            this.itemStackHandle = cached = Optional.empty();
            return cached;
        }
        this.itemStackHandle = cached = Optional.of(ItemStackHandle.createHandle(nmsHandle));
        return cached;
    }

    public Optional<ItemStackHandle> getHandleIfCraftItemStack() {
        Optional<ItemStackHandle> cached = this.itemStackHandle;
        if (cached != null) {
            return cached;
        }
        ItemStack bukkitItemStack = this.bukkitItemStack;
        if (CommonItemStack.isCraftItemStack(bukkitItemStack)) {
            Object nmsHandle = CraftItemStackHandle.T.handle.get(bukkitItemStack);
            if (nmsHandle != null) {
                this.itemStackHandle = cached = Optional.of(ItemStackHandle.createHandle(nmsHandle));
                return cached;
            }
        } else if (ItemUtil.isEmpty(bukkitItemStack)) {
            this.itemStackHandle = cached = Optional.empty();
            return cached;
        }
        return Optional.empty();
    }

    public boolean isEmpty() {
        return ItemUtil.isEmpty(this.bukkitItemStack);
    }

    public Material getType() {
        return this.bukkitItemStack == null ? Material.AIR : this.bukkitItemStack.getType();
    }

    public boolean isType(Material type) {
        return this.getType() == type;
    }

    public boolean isFilledMap() {
        return this.getHandleIfCraftItemStack().map(ItemStackHandle::isMapItem).orElseGet(() -> this.isType(CommonItemMaterials.FILLED_MAP));
    }

    public boolean isMapDisplay() {
        return this.getHandleIfCraftItemStack().map(h -> h.isMapItem() && h.getCustomData().getUUID("mapDisplay") != null).orElse(Boolean.FALSE);
    }

    public CommonItemStack setType(Material type) {
        ItemStack bukkitItemStack = this.toBukkit(true);
        if (bukkitItemStack.getType() == type) {
            return this;
        }
        bukkitItemStack.setType(type);
        this.invalidateItemStackHandle();
        this.getHandle(false).ifPresent(ItemStackHandle::refreshPatchMap);
        return this;
    }

    public int getAmount() {
        return this.bukkitItemStack == null ? 0 : this.bukkitItemStack.getAmount();
    }

    public CommonItemStack setAmount(int amount) {
        ItemStack item = this.toBukkit();
        if (item == null || item.getType() == Material.AIR) {
            throw new IllegalStateException("Cannot change the amount of an item without type");
        }
        item.setAmount(amount);
        if (amount == 0) {
            this.invalidateItemStackHandle();
        }
        return this;
    }

    public CommonItemStack subtractAmount(int amountToSubtract) {
        return this.addAmount(-amountToSubtract);
    }

    public CommonItemStack addAmount(int amountToAdd) {
        return this.setAmount(Math.max(this.getAmount() + amountToAdd, 0));
    }

    public int getDamage() {
        return this.bukkitItemStack == null ? 0 : (int)this.bukkitItemStack.getDurability();
    }

    public CommonItemStack setDamage(int damage) {
        if (this.isDamageSupported()) {
            this.getHandle().ifPresent(h -> h.setDamageValue(damage));
            return this;
        }
        throw new IllegalStateException("This item does not support durability");
    }

    public int getMaxStackSize() {
        return this.bukkitItemStack == null ? 0 : this.bukkitItemStack.getMaxStackSize();
    }

    public boolean isDamageSupported() {
        ItemStack bukkitItemStack = this.bukkitItemStack;
        if (bukkitItemStack == null) {
            return false;
        }
        ItemHandle item = CommonNMS.getItem(bukkitItemStack.getType());
        return item != null && item.usesDurability();
    }

    public int getRepairCost() {
        return this.getHandle(true).map(ItemStackHandle::getRepairCost).orElse(0);
    }

    public CommonItemStack setRepairCost(int cost) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Can not set repair cost on an empty item")).setRepairCost(cost);
        return this;
    }

    public boolean isUnbreakable() {
        return this.getHandle(true).map(ItemStackHandle::isUnbreakable).orElse(Boolean.FALSE);
    }

    public CommonItemStack setUnbreakable(boolean unbreakable) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Can not set unbreakable on an empty item")).setUnbreakable(unbreakable);
        return this;
    }

    public int getMaxDamage() {
        return ItemUtil.getMaxDurability(this.bukkitItemStack);
    }

    public ChatText getDisplayName() {
        return this.getHandle(true).map(ItemStackHandle::getDisplayName).orElse(ChatText.empty());
    }

    public String getDisplayNameMessage() {
        return this.getDisplayName().getMessage();
    }

    public boolean hasCustomName() {
        return this.getHandle(true).map(ItemStackHandle::hasCustomName).orElse(Boolean.FALSE);
    }

    public ChatText getCustomName() {
        return this.getHandle(true).map(ItemStackHandle::getCustomName).orElse(null);
    }

    public String getCustomNameMessage() {
        ChatText text = this.getCustomName();
        return text == null ? null : text.getMessage();
    }

    public CommonItemStack setCustomName(ChatText displayName) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Can not set display name on an empty item")).setCustomName(displayName);
        return this;
    }

    public CommonItemStack setCustomNameMessage(String displayName) {
        return this.setCustomName(displayName == null ? null : ChatText.fromMessage(displayName));
    }

    public List<ChatText> getLores() {
        return this.getHandle(true).map(ItemStackHandle::getLores).orElse(Collections.emptyList());
    }

    public CommonItemStack addLoreMessage(String loreMessage) {
        return this.addLore(ChatText.fromMessage(loreMessage));
    }

    public CommonItemStack addLoreLine() {
        return this.addLore(ChatText.empty());
    }

    public CommonItemStack addLore(ChatText loreLine) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Can not set lores on an empty item")).addLore(loreLine);
        return this;
    }

    public CommonItemStack clearLores() {
        this.getHandle().ifPresent(ItemStackHandle::clearLores);
        return this;
    }

    public GameProfileHandle getSkullProfile() {
        return this.getHandle(true).map(ItemStackHandle::getSkullProfile).orElse(null);
    }

    public CommonItemStack setSkullProfile(GameProfileHandle profile) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Can not set skull on an empty item")).setSkullProfile(profile);
        return this;
    }

    public String getPaintingName() {
        return this.getHandle(true).map(ItemStackHandle::getPaintingName).orElse(null);
    }

    public CommonItemStack setPaintingName(String name) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Can not set painting name on an empty item")).setPaintingName(name);
        return this;
    }

    public boolean hasItemModel() {
        return this.getHandle(true).map(ItemStackHandle::hasItemModelSet).orElse(Boolean.FALSE);
    }

    public IdentifierHandle getItemModel() {
        if (CommonCapabilities.HAS_ITEM_MODEL_COMPONENT) {
            return this.getHandle(true).map(ItemStackHandle::getItemModel).orElseGet(() -> ModelInfoLookup.lookupVanillaItemModel(this));
        }
        return ModelInfoLookup.lookupVanillaItemModel(this);
    }

    public Optional<IdentifierHandle> getItemModelIfSet() {
        return this.getHandle(true).map(ItemStackHandle::getItemModelIfSet);
    }

    public CommonItemStack setItemModel(String itemModelName) {
        return this.setItemModel(itemModelName != null ? IdentifierHandle.createNew(itemModelName) : (IdentifierHandle)null);
    }

    public CommonItemStack setItemModel(IdentifierHandle itemModelKey) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Can not set item model on an empty item")).setItemModel(itemModelKey);
        return this;
    }

    public CommonItemStack mimicAsType(Material type) {
        if (CommonCapabilities.HAS_ITEM_MODEL_COMPONENT) {
            IdentifierHandle itemModel = this.getItemModel();
            this.setType(type);
            this.setItemModel(itemModel);
        }
        return this;
    }

    public static boolean canSetItemModel() {
        return CommonCapabilities.HAS_ITEM_MODEL_COMPONENT;
    }

    public boolean hasCustomModelData() {
        return this.getHandle(true).map(ItemStackHandle::hasCustomModelDataValue).orElse(Boolean.FALSE);
    }

    public int getCustomModelData() {
        return this.getHandle(true).map(ItemStackHandle::getCustomModelDataValue).orElse(-1);
    }

    public CustomModelData getCustomModelDataComponents() {
        return this.getHandle(true).map(ItemStackHandle::getCustomModelData).orElseGet(CustomModelData::new);
    }

    public CommonItemStack setCustomModelData(int value) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Can not set custom model data on an empty item")).setCustomModelDataValue(value);
        return this;
    }

    public CommonItemStack setCustomModelDataComponents(CustomModelData value) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Can not set custom model data on an empty item")).setCustomModelData(value);
        return this;
    }

    public CommonItemStack clearCustomModelData() {
        this.getHandle().ifPresent(ItemStackHandle::clearCustomModelData);
        return this;
    }

    public boolean hasCustomData() {
        return this.getHandleIfCraftItemStack().map(ItemStackHandle::hasCustomData).orElse(Boolean.FALSE);
    }

    public ItemRenderOptions lookupRenderOptions() {
        return ModelInfoLookup.lookupItemRenderOptions(this);
    }

    public int getFilledMapId() {
        return this.getHandle(true).map(ItemStackHandle::getMapId).orElse(-1);
    }

    public CommonItemStack setFilledMapId(int mapId) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Item is empty and cannot store a map id")).setMapId(mapId);
        return this;
    }

    public int getFilledMapColor() {
        return this.getHandle(true).map(ItemStackHandle::getMapColor).orElse(-1);
    }

    public CommonItemStack setFilledMapColor(int rgb) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Item is empty and cannot store a map color")).setMapColor(rgb);
        return this;
    }

    public CommonItemStack addGlint() {
        this.getHandle().ifPresent(ItemStackHandle::addGlint);
        return this;
    }

    public CommonItemStack addUnsafeEnchantment(Enchantment enchantment, int level) {
        ItemStack bukkitItem = this.toBukkit();
        if (bukkitItem == null) {
            throw new IllegalStateException("Cannot add enchantments to an empty item");
        }
        bukkitItem.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public CommonItemStack addEnchantment(Enchantment enchantment, int level) {
        ItemStack bukkitItem = this.toBukkit();
        if (bukkitItem == null) {
            throw new IllegalStateException("Cannot add enchantments to an empty item");
        }
        bukkitItem.addEnchantment(enchantment, level);
        return this;
    }

    public CommonItemStack hideAllAttributes() {
        this.getHandle(false).ifPresent(ItemStackHandle::hideAllAttributes);
        return this;
    }

    public CommonItemStack hideTooltip() {
        this.getHandle(false).ifPresent(ItemStackHandle::hideTooltip);
        return this;
    }

    @Deprecated
    public CommonItemStack setEmptyCustomName() {
        return this.hideTooltip();
    }

    public Set<ItemFlag> getItemFlags() {
        ItemStack bukkitItem = this.toBukkit();
        if (bukkitItem == null) {
            return Collections.emptySet();
        }
        ItemMeta meta = bukkitItem.getItemMeta();
        if (meta == null) {
            return Collections.emptySet();
        }
        return meta.getItemFlags();
    }

    public CommonItemStack addItemFlags(ItemFlag ... itemFlags) {
        ItemStack bukkitItem = this.toBukkit();
        if (bukkitItem == null) {
            throw new IllegalStateException("Cannot set item flags on an empty item");
        }
        ItemMeta meta = bukkitItem.getItemMeta();
        if (meta == null) {
            throw new IllegalStateException("Item of type " + bukkitItem.getType() + " cannot store item flags");
        }
        meta.addItemFlags(itemFlags);
        bukkitItem.setItemMeta(meta);
        return this;
    }

    public CommonItemStack resetItemFlags() {
        return this.removeItemFlags(ItemFlag.values());
    }

    public CommonItemStack removeItemFlags(ItemFlag ... itemFlags) {
        ItemStack bukkitItem = this.toBukkit();
        if (bukkitItem == null) {
            throw new IllegalStateException("Cannot set item flags on an empty item");
        }
        ItemMeta meta = bukkitItem.getItemMeta();
        if (meta == null) {
            throw new IllegalStateException("Item of type " + bukkitItem.getType() + " cannot store item flags");
        }
        meta.removeItemFlags(itemFlags);
        bukkitItem.setItemMeta(meta);
        return this;
    }

    public CommonTagCompound getCustomData() {
        return this.getHandleIfCraftItemStack().map(ItemStackHandle::getCustomData).orElse(CommonTagCompound.EMPTY);
    }

    public CommonTagCompound getCustomDataCopy() {
        return this.getHandleIfCraftItemStack().map(ItemStackHandle::getCustomDataCopy).orElseGet(CommonTagCompound::new);
    }

    public CommonItemStack setCustomData(CommonTagCompound customDataTag) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Item is empty and cannot store custom data")).setCustomData(customDataTag);
        return this;
    }

    public CommonItemStack updateCustomData(Consumer<CommonTagCompound> consumer) {
        this.getHandle().orElseThrow(() -> new IllegalStateException("Item is empty and cannot store custom data")).updateCustomData(consumer);
        return this;
    }

    public ItemStack toBukkit() {
        return this.toBukkit(false);
    }

    public ItemStack toBukkit(boolean returnEmptyItem) {
        ItemStack bukkitItemStack = this.bukkitItemStack;
        if (bukkitItemStack == null) {
            if (!returnEmptyItem) {
                this.bukkitItemStack = bukkitItemStack = ItemUtil.emptyItem();
            }
        } else if (!returnEmptyItem && ItemUtil.isEmpty(bukkitItemStack)) {
            bukkitItemStack = null;
        }
        return bukkitItemStack;
    }

    public boolean equalsBukkitItem(ItemStack itemStack) {
        if (this.bukkitItemStack == null) {
            return ItemUtil.isEmpty(itemStack);
        }
        return this.bukkitItemStack.equals((Object)itemStack);
    }

    public boolean equalsIgnoreAmount(CommonItemStack itemStack) {
        if (itemStack.bukkitItemStack == null) {
            return this.isEmpty();
        }
        return this.equalsIgnoreAmount(itemStack.bukkitItemStack);
    }

    public boolean equalsIgnoreAmount(ItemStack itemStack) {
        if (this.bukkitItemStack == null) {
            return ItemUtil.isEmpty(itemStack);
        }
        return this.bukkitItemStack.isSimilar(itemStack);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CommonItemStack) {
            CommonItemStack other = (CommonItemStack)o;
            if (this.bukkitItemStack == null) {
                return other.isEmpty();
            }
            return this.bukkitItemStack.equals((Object)other.bukkitItemStack);
        }
        return false;
    }

    public int hashCode() {
        return this.isEmpty() ? 0 : this.bukkitItemStack.hashCode();
    }

    public String toString() {
        return this.isEmpty() ? "Empty" : this.bukkitItemStack.toString();
    }

    public CommonItemStack clone() {
        ItemStack bukkitItemStack = this.bukkitItemStack;
        return CommonItemStack.of(bukkitItemStack != null ? bukkitItemStack.clone() : null);
    }

    public int testTransferTo(CommonItemStack to) {
        if (this.isEmpty()) {
            return 0;
        }
        if (to.bukkitItemStack == null) {
            return this.getAmount();
        }
        if (!this.equalsIgnoreAmount(to)) {
            return 0;
        }
        return Math.min(this.getAmount(), to.getMaxStackSize() - to.getAmount());
    }

    public int transferAllTo(CommonItemStack to) {
        return this.transferTo(to, -1);
    }

    public int transferTo(CommonItemStack to, int maxAmount) {
        if (this.isEmpty() || maxAmount == 0) {
            return 0;
        }
        ItemStack fromItemStack = this.bukkitItemStack;
        ItemStack toItemStack = to.bukkitItemStack;
        if (toItemStack == null) {
            int excess;
            if (maxAmount < 0 || (excess = fromItemStack.getAmount() - maxAmount) <= 0) {
                to.setTo(this);
                this.clear();
                return to.getAmount();
            }
            to.setToCopyOf(this);
            to.setAmount(maxAmount);
            this.setAmount(excess);
            return maxAmount;
        }
        if (!this.equalsIgnoreAmount(to)) {
            return 0;
        }
        int amountToTransfer = fromItemStack.getAmount();
        if (maxAmount >= 0) {
            amountToTransfer = Math.min(amountToTransfer, maxAmount);
        }
        if ((amountToTransfer = Math.min(amountToTransfer, to.getMaxStackSize() - toItemStack.getAmount())) <= 0) {
            return 0;
        }
        to.addAmount(amountToTransfer);
        if (amountToTransfer == fromItemStack.getAmount()) {
            this.clear();
        } else {
            this.subtractAmount(amountToTransfer);
        }
        return amountToTransfer;
    }

    public int testTransferTo(Inventory to) {
        if (this.isEmpty()) {
            return 0;
        }
        int maxCanTransfer = this.getAmount();
        int totalCanTransfer = 0;
        int toSlotCount = to.getSize();
        for (int toSlot = 0; toSlot < toSlotCount; ++toSlot) {
            CommonItemStack toItem = CommonItemStack.of(to.getItem(toSlot));
            if ((totalCanTransfer += this.testTransferTo(toItem)) < maxCanTransfer) continue;
            totalCanTransfer = maxCanTransfer;
            break;
        }
        return totalCanTransfer;
    }

    public int transferAllTo(Inventory to) {
        return this.transferTo(to, -1);
    }

    public int transferTo(Inventory to, int maxAmount) {
        int remainingAmount = this.getAmount();
        if (maxAmount >= 0) {
            remainingAmount = Math.min(remainingAmount, maxAmount);
        }
        int totalTransferred = this.transferToInvInternal(to, remainingAmount, false);
        remainingAmount -= totalTransferred;
        return totalTransferred += this.transferToInvInternal(to, remainingAmount, true);
    }

    private int transferToInvInternal(Inventory to, int remainingAmount, boolean emptySlots) {
        if (remainingAmount <= 0) {
            return 0;
        }
        int totalTransferred = 0;
        int inventorySize = to.getSize();
        for (int i = 0; i < inventorySize; ++i) {
            int transferred;
            CommonItemStack item = CommonItemStack.of(to.getItem(i));
            if (emptySlots != item.isEmpty() || (transferred = this.transferTo(item, remainingAmount)) <= 0) continue;
            totalTransferred += transferred;
            to.setItem(i, item.toBukkit());
            if ((remainingAmount -= transferred) <= 0) break;
        }
        return totalTransferred;
    }

    public int takeAllFrom(Inventory inventory) {
        return this.takeFrom(inventory, -1);
    }

    public int takeFrom(Inventory inventory, int maxAmount) {
        int remainingAmount = this.getMaxStackSize() - this.getAmount();
        if (maxAmount >= 0) {
            remainingAmount = Math.min(remainingAmount, maxAmount);
        }
        if (remainingAmount <= 0) {
            return 0;
        }
        int amountTaken = 0;
        int inventorySize = inventory.getSize();
        for (int i = 0; i < inventorySize; ++i) {
            CommonItemStack item = CommonItemStack.of(inventory.getItem(i));
            int transferred = item.transferTo(this, remainingAmount);
            if (transferred <= 0) continue;
            amountTaken += transferred;
            inventory.setItem(i, item.toBukkit());
            if ((remainingAmount -= transferred) <= 0) break;
        }
        return amountTaken;
    }

    public static CommonItemStack take(Inventory inventory, Predicate<CommonItemStack> filter) {
        return CommonItemStack.take(inventory, filter, -1);
    }

    public static CommonItemStack take(Inventory inventory, Predicate<CommonItemStack> filter, int maxAmount) {
        if (maxAmount == 0) {
            return CommonItemStack.empty();
        }
        if (filter == null) {
            filter = LogicUtil.alwaysTruePredicate();
        }
        CommonItemStack result = CommonItemStack.empty();
        boolean foundItem = false;
        int inventorySize = inventory.getSize();
        int remainingAmount = 0;
        for (int i = 0; i < inventorySize; ++i) {
            int transferred;
            CommonItemStack item = CommonItemStack.of(inventory.getItem(i));
            if (!foundItem) {
                if (item.isEmpty() || !filter.test(item)) continue;
                remainingAmount = item.getMaxStackSize();
                if (maxAmount >= 0 && maxAmount < remainingAmount) {
                    remainingAmount = maxAmount;
                }
            }
            if ((transferred = item.transferTo(result, remainingAmount)) <= 0) continue;
            foundItem = true;
            inventory.setItem(i, item.toBukkit());
            if ((remainingAmount -= transferred) <= 0) break;
        }
        return result;
    }

    public static int transferAll(Inventory from, CommonItemStack to, Predicate<CommonItemStack> filter) {
        return CommonItemStack.transfer(from, to, filter, -1);
    }

    public static int transfer(Inventory from, CommonItemStack to, Predicate<CommonItemStack> filter, int maxAmount) {
        if (to.isEmpty()) {
            to.setTo(CommonItemStack.take(from, filter, maxAmount));
            return to.getAmount();
        }
        if (filter == null || filter.test(to)) {
            return to.takeFrom(from, maxAmount);
        }
        return 0;
    }

    public static int transferAll(Inventory from, Inventory to, Predicate<CommonItemStack> filter) {
        return CommonItemStack.transfer(from, to, filter, -1);
    }

    public static int transfer(Inventory from, Inventory to, Predicate<CommonItemStack> filter, int maxAmount) {
        if (maxAmount == 0) {
            return 0;
        }
        if (filter == null) {
            filter = LogicUtil.alwaysTruePredicate();
        }
        int fromSlotCount = from.getSize();
        int remainingAmount = maxAmount;
        int totalTransferred = 0;
        for (int fromSlot = 0; fromSlot < fromSlotCount; ++fromSlot) {
            int transferred;
            CommonItemStack fromItem = CommonItemStack.of(from.getItem(fromSlot));
            if (fromItem.isEmpty() || !filter.test(fromItem) || (transferred = fromItem.transferTo(to, remainingAmount)) <= 0) continue;
            totalTransferred += transferred;
            if (remainingAmount >= 0) {
                remainingAmount -= transferred;
            }
            from.setItem(fromSlot, fromItem.toBukkit());
        }
        return totalTransferred;
    }

    public static Stream<CommonItemStack> streamOfContents(Inventory inventory) {
        int slotCount = inventory.getSize();
        return IntStream.range(0, slotCount).mapToObj(arg_0 -> ((Inventory)inventory).getItem(arg_0)).filter(Objects::nonNull).map(CommonItemStack::of);
    }
}

