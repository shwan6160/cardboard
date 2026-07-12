/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.block.InputDialogSubmitText
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.DyeColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.DragType
 *  org.bukkit.event.inventory.InventoryAction
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui.models;

import com.bergerkiller.bukkit.common.block.InputDialogSubmitText;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.attachments.ui.models.ResourcePackModelListing;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.DialogBuilder;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.DialogResult;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedEntry;
import com.bergerkiller.bukkit.tc.attachments.ui.models.listing.ListedItemModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

class ResourcePackModelListingDialog
implements Listener {
    private static final int DISPLAYED_ITEM_COUNT = 36;
    private static final CommonItemStack BG_ITEM1 = CommonItemStack.empty();
    private static final CommonItemStack BG_ITEM2 = ResourcePackModelListingDialog.createGlassPaneItem(DyeColor.BROWN);
    private static final CommonItemStack BG_ITEM3 = ResourcePackModelListingDialog.createGlassPaneItem(DyeColor.GRAY);
    private static Map<Player, ResourcePackModelListingDialog> shownTo = new HashMap<Player, ResourcePackModelListingDialog>();
    private final DialogBuilder options;
    private final CompletableFuture<DialogResult> future;
    private final UIButton btnPrevPage = new PrevPageButton();
    private final UIButton btnBetweenpages = new BetweenPageButton();
    private final UIButton btnNextPage = new NextPageButton();
    private final UIButton btnBack = new BackButton();
    private final UIButton btnSearch = new SearchButton();
    private final List<UIButton> buttons = Arrays.asList(this.btnPrevPage, this.btnBetweenpages, this.btnNextPage, this.btnBack, this.btnSearch);
    private Inventory inventory;
    private ResourcePackModelListing currentListing;
    private ListedEntry current;
    private List<? extends ListedEntry> currentItems;
    private boolean futureDisabled = false;

    public static CompletableFuture<DialogResult> show(DialogBuilder dialogOptions) {
        ResourcePackModelListingDialog dialog = new ResourcePackModelListingDialog(dialogOptions.clone());
        ResourcePackModelListingDialog prev = shownTo.put(dialog.player(), dialog);
        if (prev != null) {
            prev.close();
        }
        dialog.open();
        return dialog.future;
    }

    public static void closeAll() {
        ArrayList<ResourcePackModelListingDialog> dialogs = new ArrayList<ResourcePackModelListingDialog>(shownTo.values());
        shownTo.clear();
        dialogs.forEach(ResourcePackModelListingDialog::close);
    }

    public static void closeAllByPlugin(Plugin plugin) {
        for (ResourcePackModelListingDialog dialog : new ArrayList<ResourcePackModelListingDialog>(shownTo.values())) {
            if (dialog.options.plugin() != plugin) continue;
            shownTo.remove(dialog.options.player());
            dialog.close();
        }
    }

    public static void close(Player player) {
        ResourcePackModelListingDialog prev = shownTo.remove(player);
        if (prev != null) {
            prev.close();
        }
    }

    private ResourcePackModelListingDialog(DialogBuilder options) {
        this(options, new CompletableFuture<DialogResult>());
    }

    private ResourcePackModelListingDialog(DialogBuilder options, CompletableFuture<DialogResult> future) {
        this.options = options;
        this.future = future;
    }

    public void open() {
        this.currentListing = this.options.getQuery().isEmpty() ? this.options.listing() : this.options.listing().filter(this.options.getQuery());
        Bukkit.getPluginManager().registerEvents((Listener)this, this.options.plugin());
        this.inventory = Bukkit.createInventory((InventoryHolder)this.player(), (int)54, (String)this.options.getTitle());
        ListedEntry initialEntry = this.currentListing.root().findAtPath(ListedEntry.tokenizePath(this.options.getBrowsedPath())).orElse(this.currentListing.root()).compactIf(this.options.isCompactingEnabled());
        this.navigate(initialEntry, this.options.getBrowsedPage());
        this.player().openInventory(this.inventory);
    }

    public void close() {
        this.cancelDialog(false);
        if (this.player().getOpenInventory() != null && this.player().getOpenInventory().getTopInventory() == this.inventory) {
            this.player().closeInventory();
        }
    }

    public void closeAndShowSearchDialog(String initialQuery) {
        this.futureDisabled = true;
        this.close();
        final DialogBuilder newOptions = this.options.clone();
        final CompletableFuture<DialogResult> future = this.future;
        new InputDialogSubmitText(this, this.options.plugin(), this.options.player()){
            final /* synthetic */ ResourcePackModelListingDialog this$0;
            {
                this.this$0 = this$0;
                super(arg0, arg1);
            }

            public void onAccept(String text) {
                newOptions.query(text);
                newOptions.navigate("", 0);
                newOptions.show().thenAccept(future::complete);
            }

            public void onCancel() {
                newOptions.show().thenAccept(future::complete);
            }
        }.setDescription("Enter search query").setInitialText(newOptions.getQuery()).setAcceptEmptyText(true).open();
    }

    private Player player() {
        return this.options.player();
    }

    private void complete(DialogResult result) {
        if (!this.futureDisabled) {
            this.future.complete(result);
        }
    }

    private ClickAction onItemClicked(ListedItemModel item) {
        if (this.options.isCreativeMenu()) {
            return ClickAction.CREATIVE_CLICK_PICKUP;
        }
        this.complete(new DialogResult(this.options, item));
        return ClickAction.CLOSE_DIALOG;
    }

    private boolean tryNavigateBack(boolean toRoot) {
        ListedEntry e = this.current;
        if (toRoot) {
            while (e.parent() != null) {
                e = e.parent();
            }
            e = e.compactIf(this.options.isCompactingEnabled());
        } else {
            while (e.parent() != null && (e = e.parent()).compact() != e) {
            }
        }
        if (e != this.current) {
            this.navigate(e, 0);
            return true;
        }
        return false;
    }

    private ClickAction handleClick(int clickedSlot, boolean isRightClick, ItemStack cursorItem) {
        if (this.options.isCreativeMenu() && !ItemUtil.isEmpty((ItemStack)cursorItem) && clickedSlot >= 0 && clickedSlot < 54) {
            ItemStack itemInSlot = this.inventory.getItem(clickedSlot);
            if (!isRightClick && itemInSlot != null && ItemUtil.equalsIgnoreAmount((ItemStack)itemInSlot, (ItemStack)cursorItem)) {
                if (cursorItem.getAmount() < cursorItem.getMaxStackSize()) {
                    return ClickAction.CREATIVE_CLICK_INCREASE_COUNT;
                }
                return ClickAction.HANDLED;
            }
            return ClickAction.CREATIVE_CLICK_CONSUME;
        }
        for (UIButton button : this.buttons) {
            if (button.slot != clickedSlot) continue;
            button.click(isRightClick);
            return ClickAction.HANDLED;
        }
        if (isRightClick) {
            if (!this.tryNavigateBack(false) && this.options.isCancelOnRootRightClick()) {
                this.complete(new DialogResult(this.options, true));
                return ClickAction.CLOSE_DIALOG;
            }
            return ClickAction.HANDLED;
        }
        int offset = this.options.getBrowsedPage() * 36;
        int limit = Math.min(36, this.currentItems.size() - offset);
        if (clickedSlot < limit) {
            ListedEntry e = this.currentItems.get(clickedSlot + offset);
            if (e instanceof ListedItemModel) {
                return this.onItemClicked((ListedItemModel)e);
            }
            this.navigate(e, 0);
        }
        return ClickAction.HANDLED;
    }

    private void navigate(ListedEntry current, int page) {
        this.current = current;
        this.currentItems = current.displayedItems(36, this.options.isCompactingEnabled());
        this.options.navigate(current.fullPath(), this.clampPage(page));
        this.updateItems();
    }

    private void incrementPage(int incr) {
        int newPage = this.clampPage(this.options.getBrowsedPage() + incr);
        if (newPage != this.options.getBrowsedPage()) {
            this.options.navigate(this.current.fullPath(), newPage);
            this.updateItems();
        }
    }

    private int clampPage(int newPage) {
        if (newPage < 0) {
            return 0;
        }
        return Math.min(newPage, this.currentItems.size() / 36);
    }

    private void updateItemsNextTick() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.options.plugin(), this::updateItems);
    }

    private void updateItems() {
        int i;
        int page = this.options.getBrowsedPage();
        int offset = page * 36;
        int limit = Math.min(36, this.currentItems.size() - offset);
        this.btnPrevPage.enabled = page > 0;
        this.btnNextPage.enabled = this.currentItems.size() - offset > 36;
        for (i = 0; i < limit; ++i) {
            this.inventory.setItem(i, this.currentItems.get(i + offset).createIconItem(this.options).toBukkit());
        }
        for (i = limit; i < 36; ++i) {
            this.inventory.setItem(i, BG_ITEM1.toBukkit());
        }
        for (i = 36; i < 45; ++i) {
            this.inventory.setItem(i, BG_ITEM2.toBukkit());
        }
        for (UIButton button : this.buttons) {
            this.inventory.setItem(button.slot, button.item().toBukkit());
        }
        for (int i2 = 45; i2 < 54; ++i2) {
            boolean isButtonSlot = false;
            for (UIButton button : this.buttons) {
                if (button.slot != i2) continue;
                isButtonSlot = true;
                break;
            }
            if (isButtonSlot) continue;
            this.inventory.setItem(i2, BG_ITEM3.toBukkit());
        }
    }

    private void cancelDialog(boolean delayEvent) {
        CommonUtil.unregisterListener((Listener)this);
        ResourcePackModelListingDialog dialog = shownTo.remove(this.player());
        if (dialog != null && dialog != this) {
            shownTo.put(this.player(), dialog);
        }
        DialogResult result = new DialogResult(this.options, false);
        if (delayEvent) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.options.plugin(), () -> this.complete(result));
        } else {
            this.complete(result);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer() == this.player()) {
            this.cancelDialog(false);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getPlayer() == this.player()) {
            this.close();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    private void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() == this.player() && event.getInventory() == this.inventory) {
            this.cancelDialog(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    private void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() != this.player() || event.getInventory() != this.inventory) {
            return;
        }
        long numDraggedInDialog = event.getRawSlots().stream().mapToInt(Integer::intValue).filter(i -> i >= 0 && i < 54).count();
        if (this.options.isCreativeMenu() && numDraggedInDialog == (long)event.getRawSlots().size()) {
            ItemStack cursorAfterTmp = event.getCursor();
            if (!ItemUtil.isEmpty((ItemStack)event.getOldCursor()) && event.getType() == DragType.EVEN && numDraggedInDialog == 1L && ItemUtil.equalsIgnoreAmount((ItemStack)ItemUtil.createItem((ItemStack)event.getOldCursor()), (ItemStack)this.inventory.getItem(((Integer)event.getInventorySlots().iterator().next()).intValue())) && (cursorAfterTmp = event.getOldCursor().clone()).getAmount() < cursorAfterTmp.getMaxStackSize()) {
                cursorAfterTmp.setAmount(cursorAfterTmp.getAmount() + 1);
            }
            ItemStack cursorAfter = ItemUtil.cloneItem((ItemStack)cursorAfterTmp);
            ItemStack cursorExpected = ItemUtil.cloneItem((ItemStack)event.getOldCursor());
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.options.plugin(), () -> {
                if (LogicUtil.bothNullOrEqual((Object)this.player().getItemOnCursor(), (Object)cursorExpected)) {
                    this.player().setItemOnCursor(cursorAfter);
                }
            });
            event.setCursor(cursorAfter);
            event.setResult(Event.Result.DENY);
        } else if (numDraggedInDialog > 0L) {
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() != this.player()) {
            return;
        }
        Inventory clickedInventory = ItemUtil.getClickedInventory((InventoryClickEvent)event);
        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && event.getInventory() == this.inventory && clickedInventory != this.inventory) {
            if (this.options.isCreativeMenu()) {
                event.setResult(Event.Result.DENY);
                event.setCurrentItem(null);
            } else {
                event.setResult(Event.Result.DENY);
            }
            return;
        }
        if ((event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) && this.options.isCreativeMenu() && !ItemUtil.isEmpty((ItemStack)event.getCursor()) && clickedInventory == this.inventory && event.getSlot() >= 0 && event.getSlot() < 54) {
            if (event.getCursor().getAmount() > 1) {
                ItemStack updated = event.getCursor().clone();
                updated.setAmount(updated.getAmount() - 1);
                event.setCursor(updated);
                event.setResult(Event.Result.DENY);
            } else {
                event.setCursor(null);
                event.setResult(Event.Result.DENY);
            }
            return;
        }
        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR && clickedInventory != this.inventory && !ItemUtil.isEmpty((ItemStack)event.getCursor())) {
            boolean hasItemThatMatches = false;
            ItemStack match = ItemUtil.createItem((ItemStack)event.getCursor());
            for (int i = 0; i < 54; ++i) {
                ItemStack invItem = this.inventory.getItem(i);
                if (invItem == null || !ItemUtil.equalsIgnoreAmount((ItemStack)invItem, (ItemStack)match)) continue;
                hasItemThatMatches = true;
                this.inventory.setItem(i, null);
            }
            if (hasItemThatMatches) {
                this.updateItemsNextTick();
            }
        }
        if (clickedInventory != this.inventory) {
            return;
        }
        boolean isRightClick = event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT;
        ClickAction action = this.handleClick(event.getSlot(), isRightClick, event.getCursor());
        switch (action.ordinal()) {
            case 3: {
                event.setCursor(null);
                event.setResult(Event.Result.DENY);
                break;
            }
            case 2: {
                if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    event.setResult(Event.Result.ALLOW);
                    this.updateItemsNextTick();
                    break;
                }
                if (event.getCurrentItem() != null) {
                    ItemStack pickedItem = event.getCurrentItem().clone();
                    if (event.getAction() == InventoryAction.CLONE_STACK) {
                        pickedItem.setAmount(pickedItem.getMaxStackSize());
                    }
                    event.setCursor(pickedItem);
                    event.setResult(Event.Result.DENY);
                    break;
                }
                this.updateItemsNextTick();
                event.setCursor(null);
                event.setResult(Event.Result.DENY);
                break;
            }
            case 4: {
                ItemStack incrItem = event.getCursor().clone();
                incrItem.setAmount(incrItem.getAmount() + 1);
                event.setCursor(incrItem);
                event.setResult(Event.Result.DENY);
                break;
            }
            case 1: {
                event.setResult(Event.Result.DENY);
                this.close();
                break;
            }
            case 0: {
                event.setResult(Event.Result.DENY);
                break;
            }
        }
    }

    private CommonItemStack applyPageInfo(CommonItemStack item, boolean isMiddleCountItem) {
        if (item.isEmpty()) {
            return CommonItemStack.empty();
        }
        int pageCount = 1 + this.currentItems.size() / 36;
        if (pageCount == 1) {
            return item;
        }
        item = item.clone();
        int currPage = this.options.getBrowsedPage() + 1;
        if (isMiddleCountItem) {
            item.setCustomNameMessage(ChatColor.DARK_GRAY + "Currently on");
            if (currPage <= 64) {
                item.setAmount(currPage);
            }
        } else {
            item.addLoreLine().addLoreMessage(ChatColor.DARK_GRAY + "Currently on");
        }
        item.addLoreMessage(ChatColor.DARK_GRAY + "page " + ChatColor.GRAY + currPage + ChatColor.DARK_GRAY + " of " + ChatColor.GRAY + pageCount);
        return item;
    }

    private static CommonItemStack createItem(String ... materialNames) {
        return CommonItemStack.create((Material)MaterialUtil.getFirst((String[])materialNames), (int)1);
    }

    private static CommonItemStack createGlassPaneItem(DyeColor color) {
        try {
            CommonItemStack item = CommonCapabilities.MATERIAL_ENUM_CHANGES ? CommonItemStack.create((Material)MaterialUtil.getMaterial((String)(color.name() + "_STAINED_GLASS_PANE")), (int)1) : BlockData.fromMaterialData((Material)MaterialUtil.getMaterial((String)"LEGACY_STAINED_GLASS_PANE"), (int)color.getWoolData()).createCommonItem(1);
            return item.setEmptyCustomName();
        }
        catch (Throwable t) {
            return null;
        }
    }

    private class PrevPageButton
    extends UIButton {
        private final CommonItemStack enabledIconItem;
        private final CommonItemStack disabledIconItem;

        public PrevPageButton() {
            super(3);
            this.enabledIconItem = ResourcePackModelListingDialog.createItem(new String[]{"DIAMOND_BLOCK", "LEGACY_DIAMOND_BLOCK"}).setCustomNameMessage(ChatColor.GREEN + "Previous Page");
            this.disabledIconItem = ResourcePackModelListingDialog.createItem(new String[]{"CLAY", "LEGACY_CLAY"}).setCustomNameMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "Previous Page");
        }

        @Override
        public CommonItemStack item() {
            return ResourcePackModelListingDialog.this.applyPageInfo(this.enabled ? this.enabledIconItem : this.disabledIconItem, false);
        }

        @Override
        public void click(boolean isRightClick) {
            ResourcePackModelListingDialog.this.incrementPage(-1);
        }
    }

    private static abstract class UIButton {
        public final int slot;
        public boolean enabled = true;

        public UIButton(int slot) {
            this.slot = 45 + slot;
        }

        public abstract CommonItemStack item();

        public abstract void click(boolean var1);
    }

    private class BetweenPageButton
    extends UIButton {
        public BetweenPageButton() {
            super(4);
        }

        @Override
        public CommonItemStack item() {
            return ResourcePackModelListingDialog.this.applyPageInfo(BG_ITEM3, true);
        }

        @Override
        public void click(boolean isRightClick) {
        }
    }

    private class NextPageButton
    extends UIButton {
        private final CommonItemStack enabledIconItem;
        private final CommonItemStack disabledIconItem;

        public NextPageButton() {
            super(5);
            this.enabledIconItem = ResourcePackModelListingDialog.createItem(new String[]{"DIAMOND_BLOCK", "LEGACY_DIAMOND_BLOCK"}).setCustomNameMessage(ChatColor.GREEN + "Next Page");
            this.disabledIconItem = ResourcePackModelListingDialog.createItem(new String[]{"CLAY", "LEGACY_CLAY"}).setCustomNameMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "Next Page");
        }

        @Override
        public CommonItemStack item() {
            return ResourcePackModelListingDialog.this.applyPageInfo(this.enabled ? this.enabledIconItem : this.disabledIconItem, false);
        }

        @Override
        public void click(boolean isRightClick) {
            ResourcePackModelListingDialog.this.incrementPage(1);
        }
    }

    private class BackButton
    extends UIButton {
        private final CommonItemStack backIconItem;

        public BackButton() {
            super(0);
            this.backIconItem = ResourcePackModelListingDialog.createItem(new String[]{"BOOK", "LEGACY_BOOK"}).setCustomNameMessage(ChatColor.YELLOW + "Back").addLoreLine().addLoreMessage(ChatColor.BLUE.toString() + ChatColor.ITALIC + "Right-click to go").addLoreMessage(ChatColor.BLUE.toString() + ChatColor.ITALIC + "all the way back");
        }

        @Override
        public CommonItemStack item() {
            return this.backIconItem;
        }

        @Override
        public void click(boolean isRightClick) {
            if (!ResourcePackModelListingDialog.this.tryNavigateBack(isRightClick) && !isRightClick && ResourcePackModelListingDialog.this.options.isCancelOnRootRightClick()) {
                ResourcePackModelListingDialog.this.complete(new DialogResult(ResourcePackModelListingDialog.this.options, true));
                ResourcePackModelListingDialog.this.close();
            }
        }
    }

    private class SearchButton
    extends UIButton {
        private final CommonItemStack searchIconItem;

        public SearchButton() {
            super(8);
            this.searchIconItem = ResourcePackModelListingDialog.createItem(new String[]{"COMPASS", "LEGACY_COMPASS"}).setCustomNameMessage(ChatColor.YELLOW + "Enter search query");
        }

        @Override
        public CommonItemStack item() {
            CommonItemStack item = this.searchIconItem.clone();
            if (!ResourcePackModelListingDialog.this.options.getQuery().isEmpty()) {
                item.addLoreLine().addLoreMessage(ChatColor.DARK_GRAY + "Current: " + ChatColor.GRAY + ChatColor.ITALIC + "\"" + ResourcePackModelListingDialog.this.options.getQuery() + "\"").addLoreMessage(ChatColor.BLUE.toString() + ChatColor.ITALIC + "Right-click to clear");
            }
            return item;
        }

        @Override
        public void click(boolean isRightClick) {
            if (isRightClick) {
                ResourcePackModelListingDialog.this.options.query("");
                ResourcePackModelListingDialog.this.navigate(ResourcePackModelListingDialog.this.options.listing().root().compactIf(ResourcePackModelListingDialog.this.options.isCompactingEnabled()), 0);
            } else {
                ResourcePackModelListingDialog.this.closeAndShowSearchDialog(ResourcePackModelListingDialog.this.options.getQuery());
            }
        }
    }

    private static enum ClickAction {
        HANDLED,
        CLOSE_DIALOG,
        CREATIVE_CLICK_PICKUP,
        CREATIVE_CLICK_CONSUME,
        CREATIVE_CLICK_INCREASE_COUNT;

    }
}

