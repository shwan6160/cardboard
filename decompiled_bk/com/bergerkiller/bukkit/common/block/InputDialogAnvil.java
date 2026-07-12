/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryCloseEvent
 *  org.bukkit.event.inventory.InventoryEvent
 *  org.bukkit.event.inventory.PrepareAnvilEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.inventory.AnvilInventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.block;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.hooks.LegacyContainerAnvilHook;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.PacketUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.AbstractContainerMenuHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.AnvilMenuHandle;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class InputDialogAnvil {
    private final Plugin plugin;
    private final Player player;
    public final Button LEFT_BUTTON = new Button(this, 0);
    public final Button MIDDLE_BUTTON = new Button(this, 1);
    public final Button RIGHT_BUTTON = new Button(this, 2);
    private final Set<InventoryView> _openInventories;
    private final Listener _listener;
    private boolean _isWindowOpen = false;
    private String _initialText = "";
    private String _text = "";

    public InputDialogAnvil(Plugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this._openInventories = Collections.newSetFromMap(new WeakHashMap());
        this._listener = CommonCapabilities.HAS_PREPARE_ANVIL_EVENT ? new EventListenerFull() : new EventListenerBase();
    }

    public final Plugin getPlugin() {
        return this.plugin;
    }

    public final Player getPlayer() {
        return this.player;
    }

    public String getText() {
        return this._text;
    }

    public InputDialogAnvil setInitialText(String text) {
        this._initialText = text;
        return this;
    }

    public ChatText getTitle() {
        return null;
    }

    public InputDialogAnvil setMiddleButtonTitle(String title) {
        this.MIDDLE_BUTTON.setTitle(title);
        return this;
    }

    public void onClick(Button button) {
    }

    public void onTextChanged() {
    }

    public void onOpen() {
    }

    public void onClose() {
    }

    public void open() {
        this.setWindowOpen(true, false);
    }

    public void close() {
        this.setWindowOpen(false, false);
    }

    private void handleTextChange(InventoryView view) {
        String new_text = AnvilMenuHandle.fromBukkit(view).getRenameText();
        new_text = LogicUtil.fixNull(new_text, "").replace("\u0000", "");
        this.LEFT_BUTTON._title = ChatColor.BLACK + new_text;
        while (new_text.startsWith(" ")) {
            new_text = new_text.substring(1);
        }
        if (!this._text.equals(new_text)) {
            this._text = new_text;
            this.onTextChanged();
        }
        this.RIGHT_BUTTON.refresh(view);
    }

    private void refreshButtons(InventoryView view) {
        this.LEFT_BUTTON.refresh(view);
        this.MIDDLE_BUTTON.refresh(view);
        this.RIGHT_BUTTON.refresh(view);
    }

    private void setWindowOpen(boolean enabled, boolean delayCallback) {
        if (this._isWindowOpen == enabled) {
            return;
        }
        this._isWindowOpen = enabled;
        for (InventoryView view : this._openInventories) {
            ItemUtil.setViewItem(view, this.LEFT_BUTTON.getIndex(), null);
            ItemUtil.setViewItem(view, this.MIDDLE_BUTTON.getIndex(), null);
            ItemUtil.setViewItem(view, this.RIGHT_BUTTON.getIndex(), null);
            ItemUtil.closeView(view);
        }
        this._openInventories.clear();
        if (enabled) {
            this.onOpen();
            Bukkit.getPluginManager().registerEvents(this._listener, this.plugin);
            this._text = this._initialText;
            this.LEFT_BUTTON._title = ChatColor.BLACK + this._initialText;
            InventoryView view = ServerPlayerHandle.fromBukkit(this.player).openAnvilWindow(this.getTitle());
            AnvilMenuHandle container = AnvilMenuHandle.fromBukkit(view);
            container.setRenameText(this._initialText);
            if (!CommonCapabilities.HAS_PREPARE_ANVIL_EVENT) {
                LegacyContainerAnvilHook hook = ClassHook.get(container.getRaw(), LegacyContainerAnvilHook.class);
                hook.textChangeCallback = () -> this.handleTextChange(view);
            }
            this._openInventories.add(view);
            this.refreshButtons(view);
            if (this._openInventories.isEmpty()) {
                this.setWindowOpen(false, false);
            }
        } else {
            CommonUtil.unregisterListener(this._listener);
            if (delayCallback) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, this::onClose);
            } else {
                this.onClose();
            }
        }
    }

    public static class Button {
        private final InputDialogAnvil _owner;
        private final int _index;
        private String _title;
        private String _description;
        private Material _material;

        public Button(InputDialogAnvil owner, int index) {
            this._owner = owner;
            this._index = index;
            this._title = "";
            this._description = "";
            this._material = Material.AIR;
        }

        public int getIndex() {
            return this._index;
        }

        public String getTitle() {
            return this._title;
        }

        public void setTitle(String title) {
            if (!LogicUtil.bothNullOrEqual(this._title, title)) {
                this._title = title;
                this.refresh();
            }
        }

        public String getDescription() {
            return this._description;
        }

        public void setDescription(String description) {
            if (!LogicUtil.bothNullOrEqual(this._description, description)) {
                this._description = description;
                this.refresh();
            }
        }

        public Material getMaterial() {
            return this._material;
        }

        public void setMaterial(Material material) {
            if (this._material != material) {
                this._material = material;
                this.refresh();
            }
        }

        public void setItemMaterial(ItemStack material) {
            this.setMaterial(material == null ? null : material.getType());
        }

        protected ItemStack createItem() {
            if (this.getMaterial() == null || BlockData.AIR.isType(this.getMaterial())) {
                return null;
            }
            CommonItemStack item = CommonItemStack.create(this.getMaterial(), 1);
            if (this.getTitle() != null && !this.getTitle().isEmpty()) {
                item.setCustomNameMessage(this.getTitle());
            } else if (CommonCapabilities.EMPTY_ITEM_NAME) {
                item.setCustomNameMessage("");
            } else {
                item.setCustomNameMessage("\u0000");
            }
            item.setRepairCost(0);
            if (this.getDescription() != null && !this.getDescription().isEmpty()) {
                for (String line : this.getDescription().split("\n")) {
                    item.addLoreMessage(ChatColor.RESET.toString() + line);
                }
            }
            return item.toBukkit();
        }

        protected void refresh() {
            for (InventoryView view : this._owner._openInventories) {
                this.refresh(view);
            }
        }

        protected void refresh(InventoryView view) {
            if (!this._owner._isWindowOpen) {
                return;
            }
            ItemStack item = this.createItem();
            if (!LogicUtil.bothNullOrEqual(item, ItemUtil.getViewItem(view, this.getIndex()))) {
                ItemUtil.setViewItem(view, this.getIndex(), item);
            }
            int windowId = AbstractContainerMenuHandle.fromBukkit(view).getWindowId();
            CommonPacket set_output_packet = PacketType.OUT_WINDOW_SET_SLOT.newInstance();
            set_output_packet.write(PacketType.OUT_WINDOW_SET_SLOT.windowId, windowId);
            set_output_packet.write(PacketType.OUT_WINDOW_SET_SLOT.slot, this.getIndex());
            set_output_packet.write(PacketType.OUT_WINDOW_SET_SLOT.item, this.createItem());
            PacketUtil.sendPacket(ItemUtil.getViewPlayer(view), set_output_packet);
        }

        public String toString() {
            if (this.getDescription() == null || this.getDescription().isEmpty()) {
                return "Button[" + this.getIndex() + "]";
            }
            return this.getDescription();
        }
    }

    private class EventListenerFull
    extends EventListenerBase {
        private EventListenerFull() {
        }

        @EventHandler(priority=EventPriority.LOWEST)
        public void onPrepareAnvil(PrepareAnvilEvent event) {
            if (!this.mustHandle((InventoryEvent)event)) {
                return;
            }
            InputDialogAnvil.this.handleTextChange(event.getView());
            event.setResult(InputDialogAnvil.this.RIGHT_BUTTON.createItem());
        }
    }

    private class EventListenerBase
    implements Listener {
        private EventListenerBase() {
        }

        protected boolean mustHandle(InventoryEvent event) {
            if (InputDialogAnvil.this._openInventories.isEmpty()) {
                InputDialogAnvil.this.setWindowOpen(false, false);
                return false;
            }
            return event.getInventory() instanceof AnvilInventory && InputDialogAnvil.this._openInventories.contains(event.getView());
        }

        @EventHandler(priority=EventPriority.LOWEST)
        public void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin() == InputDialogAnvil.this.plugin) {
                InputDialogAnvil.this.setWindowOpen(false, false);
            }
        }

        @EventHandler(priority=EventPriority.MONITOR)
        public void onPlayerQuit(PlayerQuitEvent event) {
            if (event.getPlayer() == InputDialogAnvil.this.player) {
                InputDialogAnvil.this.setWindowOpen(false, false);
            }
        }

        @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            if (event.getPlayer() == InputDialogAnvil.this.player) {
                InputDialogAnvil.this.setWindowOpen(false, false);
            }
        }

        @EventHandler(priority=EventPriority.MONITOR)
        public void onInventoryClose(InventoryCloseEvent event) {
            if (!this.mustHandle((InventoryEvent)event)) {
                return;
            }
            InputDialogAnvil.this.setWindowOpen(false, true);
        }

        @EventHandler(priority=EventPriority.LOWEST)
        public void onInventoryClick(InventoryClickEvent event) {
            Button button;
            if (!this.mustHandle((InventoryEvent)event)) {
                return;
            }
            InventoryView view = event.getView();
            if (event.getRawSlot() == 0) {
                button = InputDialogAnvil.this.LEFT_BUTTON;
            } else if (event.getRawSlot() == 1) {
                button = InputDialogAnvil.this.MIDDLE_BUTTON;
            } else if (event.getRawSlot() == 2) {
                button = InputDialogAnvil.this.RIGHT_BUTTON;
            } else {
                CommonUtil.nextTick(() -> InputDialogAnvil.this.refreshButtons(view));
                return;
            }
            ItemStack itemOnCursor = event.getCursor();
            event.setCursor(null);
            event.setResult(Event.Result.DENY);
            if (!ItemUtil.isEmpty(itemOnCursor)) {
                ItemUtil.getBottomInventory(view).addItem(new ItemStack[]{itemOnCursor});
            }
            InputDialogAnvil.this.onClick(button);
            if (InputDialogAnvil.this._isWindowOpen) {
                CommonUtil.nextTick(() -> InputDialogAnvil.this.refreshButtons(view));
            }
        }
    }
}

