/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.map.MapResourcePack
 *  com.bergerkiller.bukkit.common.map.MapResourcePack$Resource
 *  com.bergerkiller.bukkit.common.map.MapResourcePack$ResourceType
 *  com.bergerkiller.bukkit.common.map.util.ItemModel$MinecraftModel
 *  com.bergerkiller.bukkit.common.map.util.ItemModelOverride
 *  com.bergerkiller.bukkit.common.map.util.Model$ModelOverride
 *  com.bergerkiller.bukkit.common.map.util.ModelInfoLookup
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui.models;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.util.ItemModel;
import com.bergerkiller.bukkit.common.map.util.ItemModelOverride;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.map.util.ModelInfoLookup;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.tc.attachments.ui.models.ResourcePackModelListingDialog;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.DialogBuilder;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.DialogResult;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedItemModel;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedRoot;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedRootLoader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ResourcePackModelListing
extends ListedRootLoader {
    private final Plugin plugin;
    private MapResourcePack resourcePack;
    private static final boolean CAN_SORT_DUPLICATE_ITEMS = Common.hasCapability((String)"Common:CommonItemStack:ItemModel");
    private static final boolean CAN_CHECK_VANILLA = Common.hasCapability((String)"Common:MapResourcePack:OpenResource");

    public ResourcePackModelListing() {
        this(null);
    }

    public ResourcePackModelListing(Plugin plugin) {
        this.plugin = plugin;
        this.resourcePack = null;
    }

    public DialogBuilder buildDialog(Player player) {
        if (this.plugin == null) {
            throw new IllegalStateException("No plugin was specified on constructor, cannot show dialog");
        }
        return new DialogBuilder(this.plugin, player, this);
    }

    public DialogBuilder buildDialog(Player player, Plugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin is null");
        }
        return new DialogBuilder(plugin, player, this);
    }

    public void showCreativeDialog(Player player) {
        this.buildDialog(player).asCreativeMenu().show();
    }

    public static void closeDialog(Player player) {
        ResourcePackModelListingDialog.close(player);
    }

    public static void closeAllDialogs() {
        ResourcePackModelListingDialog.closeAll();
    }

    public static void closeAllDialogs(Plugin plugin) {
        ResourcePackModelListingDialog.closeAllByPlugin(plugin);
    }

    public static CompletableFuture<DialogResult> showDialog(DialogBuilder dialogOptions) {
        return ResourcePackModelListingDialog.show(dialogOptions);
    }

    public boolean isEmpty() {
        return this.root.itemModels().isEmpty();
    }

    public void clear() {
        this.root = new ListedRoot();
    }

    public ListedRoot root() {
        return this.root;
    }

    public boolean isBareItem(ItemStack item) {
        return this.root.bareItemStacks().containsKey(item);
    }

    public ListedItemModel getBareItemModel(ItemStack bareItem) {
        return this.root.bareItemStacks().get(bareItem);
    }

    public MapResourcePack loadedResourcePack() {
        return this.resourcePack;
    }

    public ResourcePackModelListing filter(String query) {
        ResourcePackModelListing filteredListing = new ResourcePackModelListing(this.plugin);
        filteredListing.resourcePack = this.resourcePack;
        filteredListing.loadFromListing(this.root, query);
        return filteredListing;
    }

    public void load(MapResourcePack resourcePack) {
        this.clear();
        this.resourcePack = resourcePack;
        int totalCount = Common.hasCapability((String)"Common:ResourcePack:ItemModel") ? this.loadModernItemModels(resourcePack) : this.loadLegacyPredicates(resourcePack);
        if (totalCount > 0) {
            this.logLoading("Resource pack item model lists loaded (" + totalCount + ")");
        }
    }

    private int loadModernItemModels(MapResourcePack resourcePack) {
        Set allOverridedModels = resourcePack.listOverriddenItemModelNames();
        if (allOverridedModels.isEmpty()) {
            return 0;
        }
        this.logLoading("Loading resource pack item model lists");
        HashMap<String, List> itemModels = new HashMap<String, List>();
        for (String string : allOverridedModels) {
            for (ItemModelOverride override : resourcePack.getItemModelConfig(string).listAllOverrides()) {
                Optional itemStack = override.getItemStack();
                if (!itemStack.isPresent()) continue;
                boolean strictNameSpaceCheck = override.isMatchingAlways();
                for (ItemModel.MinecraftModel model : override.getOverrideModels()) {
                    if (!model.hasValidModels() || model.model.startsWith("minecraft:") && (!CAN_CHECK_VANILLA || ResourcePackModelListing.isVanillaModel(resourcePack, model.model)) || strictNameSpaceCheck && !model.model.contains(":")) continue;
                    itemModels.computeIfAbsent(model.model, m -> new ArrayList()).add((CommonItemStack)itemStack.get());
                }
            }
        }
        for (Map.Entry entry : itemModels.entrySet()) {
            if (((List)entry.getValue()).size() > 1 && CAN_SORT_DUPLICATE_ITEMS) {
                ((List)entry.getValue()).sort(new DuplicateItemComparator());
            }
            String credit = resourcePack.getModelInfo((String)entry.getKey()).getCredit();
            this.root.addListedItem((String)entry.getKey(), (CommonItemStack)((List)entry.getValue()).get(0), credit);
        }
        return itemModels.size();
    }

    private static boolean isVanillaModel(MapResourcePack resourcePack, String modelName) {
        if (!CAN_CHECK_VANILLA) {
            return true;
        }
        MapResourcePack.Resource resource = resourcePack.openResource(MapResourcePack.ResourceType.MODELS, modelName);
        return resource == null || resource.isVanilla();
    }

    @Deprecated
    private int loadLegacyPredicates(MapResourcePack resourcePack) {
        boolean logged = false;
        HashSet allOverridedModels = new HashSet();
        for (MapResourcePack p = resourcePack; p != null && p != MapResourcePack.VANILLA; p = p.getBase()) {
            if (!logged) {
                logged = true;
                this.logLoading("Loading resource pack item model lists");
            }
            allOverridedModels.addAll(p.listResources(MapResourcePack.ResourceType.MODELS, "item", false));
        }
        if (allOverridedModels.isEmpty()) {
            return 0;
        }
        int totalCount = 0;
        for (Material material : ItemUtil.getItemTypes()) {
            for (ItemStack item : ItemUtil.getItemVariants((Material)material)) {
                String path = "item/" + ModelInfoLookup.lookupItemRenderOptions((CommonItemStack)CommonItemStack.of((ItemStack)item)).lookupModelName();
                if (!allOverridedModels.contains(path)) continue;
                for (Model.ModelOverride override : resourcePack.getModelInfo(path).getOverrides()) {
                    if (override.model == null || (override.model.startsWith("minecraft:") ? override.model.substring(10).equals(path) : override.model.equals(path))) continue;
                    String credit = resourcePack.getModelInfo(override.model).getCredit();
                    ItemStack modelItem = override.applyToItem(item);
                    this.root.addListedItem(override.model, CommonItemStack.of((ItemStack)modelItem), credit);
                    ++totalCount;
                }
            }
        }
        return totalCount;
    }

    private void logLoading(String message) {
        if (this.plugin != null) {
            this.plugin.getLogger().log(Level.INFO, "[Resource Pack Models] " + message);
        } else {
            System.out.println("[Resource Pack Models] " + message);
        }
    }

    private static class DuplicateItemComparator
    implements Comparator<CommonItemStack> {
        private DuplicateItemComparator() {
        }

        @Override
        public int compare(CommonItemStack item1, CommonItemStack item2) {
            boolean hasItemModel = item1.hasItemModel();
            if (hasItemModel != item2.hasItemModel()) {
                return hasItemModel ? 1 : -1;
            }
            boolean hasCustomData = item1.hasCustomModelData();
            if (hasCustomData != item2.hasCustomModelData()) {
                return hasCustomData ? 1 : -1;
            }
            return 0;
        }
    }
}

