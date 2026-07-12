/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.world.item;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.CustomModelData;
import com.bergerkiller.generated.com.mojang.authlib.GameProfileHandle;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.BlockStateHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="net.minecraft.world.item.ItemStack")
public abstract class ItemStackHandle
extends Template.Handle {
    public static final ItemStackClass T = Template.Class.create(ItemStackClass.class, Common.TEMPLATE_RESOLVER);
    public static final ItemStackHandle EMPTY_ITEM = ItemStackHandle.T.OPT_EMPTY_ITEM.isAvailable() ? ItemStackHandle.T.OPT_EMPTY_ITEM.get() : (ItemStackHandle)T.createHandle(null, true);

    public static ItemStackHandle createHandle(Object handleInstance) {
        return (ItemStackHandle)T.createHandle(handleInstance);
    }

    public static ItemStackHandle newInstance(Material type) {
        return ItemStackHandle.T.newInstance.invoke(type);
    }

    public static ItemStackHandle fromBlockData(BlockStateHandle data, int amount) {
        return ItemStackHandle.T.fromBlockData.invoke(data, amount);
    }

    public abstract Object getItem();

    public abstract Material getTypeField();

    public abstract void refreshPatchMap();

    public abstract ChatText getCustomName();

    public abstract ChatText getDisplayName();

    public abstract boolean hasCustomName();

    public abstract void setCustomName(ChatText var1);

    public abstract void hideTooltip();

    public abstract void hideAllAttributes();

    public abstract void addGlint();

    public abstract List<ChatText> getLores();

    public abstract void addLore(ChatText var1);

    public abstract void clearLores();

    public abstract int getDamageValue();

    public abstract void setDamageValue(int var1);

    public abstract boolean isUnbreakable();

    public abstract void setUnbreakable(boolean var1);

    public abstract int getRepairCost();

    public abstract void setRepairCost(int var1);

    public abstract int getMapColor();

    public abstract void setMapColor(int var1);

    public abstract int getLeatherArmorColor();

    public abstract int getPotionColor();

    public abstract int getFireworksFlightDuration();

    public abstract void setFireworksFlightDuration(int var1);

    public abstract GameProfileHandle getSkullProfile();

    public abstract void setSkullProfile(GameProfileHandle var1);

    public abstract boolean hasItemModelSet();

    public abstract IdentifierHandle getItemModel();

    public abstract IdentifierHandle getItemModelIfSet();

    public abstract void setItemModel(IdentifierHandle var1);

    public abstract boolean hasCustomModelData();

    public abstract boolean hasCustomModelDataValue();

    public abstract CustomModelData getCustomModelData();

    public abstract int getCustomModelDataValue();

    public abstract void setCustomModelData(CustomModelData var1);

    public abstract void setCustomModelDataValue(int var1);

    public abstract void clearCustomModelData();

    public abstract boolean hasCustomData();

    public abstract CommonTagCompound getCustomDataCopy();

    public abstract CommonTagCompound getCustomData();

    public abstract void setCustomData(CommonTagCompound var1);

    public abstract void updateCustomData(Consumer<CommonTagCompound> var1);

    public abstract ItemStackHandle cloneItemStack();

    public abstract ItemStackHandle cloneAndSubtract(int var1);

    public abstract ItemStack toBukkit();

    public abstract boolean isMapItem();

    public abstract int getMapId();

    public abstract void setMapId(int var1);

    public abstract String getPaintingName();

    public abstract void setPaintingName(String var1);

    public abstract UUID getMapDisplayDynamicOnlyUUID();

    public abstract UUID getMapDisplayUUID();

    public static ItemStackHandle fromBukkit(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        return ItemStackHandle.createHandle(HandleConversion.toItemStackHandle(itemStack));
    }

    public abstract int getAmountField();

    public abstract void setAmountField(int var1);

    public static final class ItemStackClass
    extends Template.Class<ItemStackHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<ItemStackHandle> OPT_EMPTY_ITEM = new Template.StaticField.Converted();
        public final Template.Field.Integer amountField = new Template.Field.Integer();
        public final Template.StaticMethod.Converted<ItemStackHandle> newInstance = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ItemStackHandle> fromBlockData = new Template.StaticMethod.Converted();
        @Template.Optional
        public final Template.Method<Boolean> isEmpty = new Template.Method();
        public final Template.Method<Object> getItem = new Template.Method();
        public final Template.Method.Converted<Material> getTypeField = new Template.Method.Converted();
        public final Template.Method<Void> refreshPatchMap = new Template.Method();
        public final Template.Method.Converted<ChatText> getCustomName = new Template.Method.Converted();
        public final Template.Method.Converted<ChatText> getDisplayName = new Template.Method.Converted();
        public final Template.Method<Boolean> hasCustomName = new Template.Method();
        public final Template.Method.Converted<Void> setCustomName = new Template.Method.Converted();
        public final Template.Method<Void> hideTooltip = new Template.Method();
        public final Template.Method<Void> hideAllAttributes = new Template.Method();
        public final Template.Method<Void> addGlint = new Template.Method();
        public final Template.Method.Converted<List<ChatText>> getLores = new Template.Method.Converted();
        public final Template.Method.Converted<Void> addLore = new Template.Method.Converted();
        public final Template.Method<Void> clearLores = new Template.Method();
        public final Template.Method<Integer> getDamageValue = new Template.Method();
        public final Template.Method<Void> setDamageValue = new Template.Method();
        public final Template.Method<Boolean> isUnbreakable = new Template.Method();
        public final Template.Method<Void> setUnbreakable = new Template.Method();
        public final Template.Method<Integer> getRepairCost = new Template.Method();
        public final Template.Method<Void> setRepairCost = new Template.Method();
        public final Template.Method<Integer> getMapColor = new Template.Method();
        public final Template.Method<Void> setMapColor = new Template.Method();
        public final Template.Method<Integer> getLeatherArmorColor = new Template.Method();
        public final Template.Method<Integer> getPotionColor = new Template.Method();
        public final Template.Method<Integer> getFireworksFlightDuration = new Template.Method();
        public final Template.Method<Void> setFireworksFlightDuration = new Template.Method();
        public final Template.Method.Converted<GameProfileHandle> getSkullProfile = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setSkullProfile = new Template.Method.Converted();
        public final Template.Method<Boolean> hasItemModelSet = new Template.Method();
        public final Template.Method.Converted<IdentifierHandle> getItemModel = new Template.Method.Converted();
        public final Template.Method.Converted<IdentifierHandle> getItemModelIfSet = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setItemModel = new Template.Method.Converted();
        public final Template.Method<Boolean> hasCustomModelData = new Template.Method();
        public final Template.Method<Boolean> hasCustomModelDataValue = new Template.Method();
        public final Template.Method.Converted<CustomModelData> getCustomModelData = new Template.Method.Converted();
        public final Template.Method<Integer> getCustomModelDataValue = new Template.Method();
        public final Template.Method.Converted<Void> setCustomModelData = new Template.Method.Converted();
        public final Template.Method<Void> setCustomModelDataValue = new Template.Method();
        public final Template.Method<Void> clearCustomModelData = new Template.Method();
        public final Template.Method<Boolean> hasCustomData = new Template.Method();
        public final Template.Method.Converted<CommonTagCompound> getCustomDataCopy = new Template.Method.Converted();
        public final Template.Method<CommonTagCompound> getCustomData = new Template.Method();
        public final Template.Method.Converted<Void> setCustomData = new Template.Method.Converted();
        public final Template.Method.Converted<Void> updateCustomData = new Template.Method.Converted();
        public final Template.Method.Converted<ItemStackHandle> cloneItemStack = new Template.Method.Converted();
        public final Template.Method.Converted<ItemStackHandle> cloneAndSubtract = new Template.Method.Converted();
        public final Template.Method<ItemStack> toBukkit = new Template.Method();
        public final Template.Method<Boolean> isMapItem = new Template.Method();
        public final Template.Method<Integer> getMapId = new Template.Method();
        public final Template.Method<Void> setMapId = new Template.Method();
        public final Template.Method<String> getPaintingName = new Template.Method();
        public final Template.Method<Void> setPaintingName = new Template.Method();
        public final Template.Method<UUID> getMapDisplayDynamicOnlyUUID = new Template.Method();
        public final Template.Method<UUID> getMapDisplayUUID = new Template.Method();
    }
}

