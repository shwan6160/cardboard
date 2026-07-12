/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.events.map.MapStatusEvent
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapDisplay
 *  com.bergerkiller.bukkit.common.map.MapDisplayProperties
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapSessionMode
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetWindow
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil$ItemSynchronizer
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayProperties;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapSessionMode;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetWindow;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.config.SavedAttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.helper.HelperMethods;
import com.bergerkiller.bukkit.tc.attachments.ui.ItemDropTarget;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentTree;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.global.TrainCartsPlayer;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AttachmentEditor
extends MapDisplay {
    private static final int SNEAK_DEBOUNCE_TICKS = 5;
    public CartProperties editedCart;
    public AttachmentModel model;
    private boolean _hasPermission;
    private int blinkCounter = 0;
    private int sneakCounter = 0;
    private List<Attachment> _lastSelectedAttachments = new ArrayList<Attachment>();
    private MapWidgetWindow window = new MapWidgetWindow();
    private MapWidgetAttachmentTree tree = new MapWidgetAttachmentTree(){

        @Override
        public void onKeyPressed(MapKeyEvent event) {
            if (!AttachmentEditor.this.updateSneakWalking(event)) {
                super.onKeyPressed(event);
            }
        }

        @Override
        public void onMenuOpen(MapWidgetAttachmentNode node, MapWidgetAttachmentNode.MenuItem menu) {
            if (node.checkModifyPermissions()) {
                AttachmentEditor.this.addWidget((MapWidget)menu.createMenu(node));
            }
        }
    };

    public MapDisplayProperties getProperties() {
        return this.properties;
    }

    public void onTick() {
        Player player = (Player)this.getViewers().get(0);
        if (this.sneakCounter > 0) {
            if (player.isSneaking()) {
                this.sneakCounter = 5;
            } else if (--this.sneakCounter == 0) {
                this.setReceiveInputWhenHolding(true);
            }
        }
        if (this._hasPermission != Permission.COMMAND_GIVE_EDITOR.has((CommandSender)this.getOwners().get(0))) {
            this.setRunning(false);
            this.setRunning(true);
            return;
        }
        if (this.editedCart != null && this.editedCart.isRemoved()) {
            this.setRunning(false);
            this.setRunning(true);
            return;
        }
        this.syncSelectedLiveAttachments();
        if (!this._lastSelectedAttachments.isEmpty()) {
            ++this.blinkCounter;
            FocusMode nextMode = FocusMode.fromPhase(this.blinkCounter);
            if (nextMode != null) {
                this.updateFocus(nextMode);
                if (nextMode == FocusMode.NONE) {
                    this.blinkCounter = 0;
                }
            }
        }
    }

    public boolean updateSneakWalking(MapKeyEvent event) {
        if (event.getKey() == MapPlayerInput.Key.BACK) {
            MapWidget activated = this.getActivatedWidget();
            if (activated instanceof MapWidgetAttachmentNode && ((MapWidgetAttachmentNode)activated).isChangingOrder()) {
                return false;
            }
            if (activated == this.getRootWidget() || activated == this.tree || activated instanceof MapWidgetAttachmentNode) {
                if (TCConfig.enableSneakingInAttachmentEditor) {
                    this.sneakCounter = 5;
                    this.setReceiveInputWhenHolding(false);
                }
                return true;
            }
        }
        return false;
    }

    public void onKeyPressed(MapKeyEvent event) {
        this.updateSneakWalking(event);
    }

    public void onStatusChanged(MapStatusEvent event) {
        if (this.tree.getDisplay() == null) {
            return;
        }
        if (event.isName("changed") || event.isName("sync")) {
            this.tree.sync();
            this.pauseBlinking(FocusMode.SELECTED, 5);
        } else if (event.isName("reset")) {
            this.tree.updateView();
            this.tree.sync();
            this.pauseBlinking(FocusMode.NONE, 30);
        }
    }

    private void syncSelectedLiveAttachments() {
        LogicUtil.synchronizeList(this._lastSelectedAttachments, this.tree.getSelectedNode().getAttachments(), (LogicUtil.ItemSynchronizer)new LogicUtil.ItemSynchronizer<Attachment, Attachment>(){

            public boolean isItem(Attachment o, Attachment o2) {
                return o == o2;
            }

            public Attachment onAdded(Attachment added) {
                FocusMode.fromCounter(AttachmentEditor.this.blinkCounter).applyTo(added);
                return added;
            }

            public void onRemoved(Attachment removed) {
                removed.setFocused(false);
                AttachmentEditor.setChildrenFocused(removed, false);
            }
        });
    }

    public void onSelectedNodeChanged() {
        if (this.getFocusedWidget() instanceof MapWidgetAttachmentNode) {
            this.pauseBlinking(FocusMode.SELECTED, 2);
        } else {
            this.pauseBlinking(FocusMode.NONE, 30);
        }
    }

    public void onAttached() {
        this.setGlobal(false);
        this.setUpdateWithoutViewers(false);
        this.setSessionMode(MapSessionMode.HOLDING);
        this.setMasterVolume(0.3f);
        this.reload();
    }

    public void onDetached() {
        this.getRootWidget().deactivate();
        this.updateFocus(FocusMode.NONE);
        this._lastSelectedAttachments.clear();
    }

    public CartProperties getEditedCartProperties() {
        return this.editedCart;
    }

    public MinecartMember<?> getEditedCart() {
        return this.editedCart == null ? null : this.editedCart.getHolder();
    }

    public boolean isEditingSavedModel() {
        return this.model instanceof SavedAttachmentModel;
    }

    public static void reloadAttachmentEditorFor(UUID playerUUID) {
        AttachmentEditor editor;
        Player player = Bukkit.getPlayer((UUID)playerUUID);
        if (player != null && (editor = (AttachmentEditor)MapDisplay.getHeldDisplay((Player)player, AttachmentEditor.class)) != null) {
            editor.reload();
        }
    }

    public void reload() {
        this.clearWidgets();
        this.window = new MapWidgetWindow();
        this.window.setBounds(0, 0, this.getWidth(), this.getHeight());
        this.window.getTitle().setText("Attachment Editor");
        this.addWidget((MapWidget)this.window);
        this._hasPermission = Permission.COMMAND_GIVE_EDITOR.has((CommandSender)this.getOwners().get(0));
        if (!this._hasPermission) {
            this.setReceiveInputWhenHolding(false);
            this.editedCart = null;
            this.model = AttachmentModel.getDefaultModel(EntityType.MINECART);
            ((MapWidgetText)this.window.addWidget((MapWidget)new MapWidgetText())).setText("You do not have\npermission!").setColor((byte)18).setShadowColor(MapColorPalette.getSpecular((byte)18, (float)0.5f)).setPosition(20, 60);
        } else {
            TrainCarts traincarts = TrainCarts.plugin;
            Player owner = (Player)this.getOwners().get(0);
            TrainCartsPlayer tcOwner = traincarts.getPlayer(owner);
            SavedAttachmentModel editedModel = tcOwner.getEditedModelInit();
            if (editedModel != null) {
                this.editedCart = null;
                this.sneakCounter = owner.isSneaking() ? 5 : 0;
                this.setReceiveInputWhenHolding(this.sneakCounter == 0);
                this.model = editedModel;
                this.tree.setModel(this.model);
                this.tree.setBounds(5, 13, 119, 102);
                this.window.getTitle().setText("Attachment Model Editor");
                this.window.setBackgroundColor(MapColorPalette.getColor((int)54, (int)168, (int)176));
                this.window.addWidget((MapWidget)this.tree);
            } else {
                this.editedCart = tcOwner.getEditedCart();
                if (this.editedCart != null) {
                    this.sneakCounter = owner.isSneaking() ? 5 : 0;
                    this.setReceiveInputWhenHolding(this.sneakCounter == 0);
                    this.model = this.editedCart.getModel();
                    this.tree.setModel(this.model);
                    this.tree.setBounds(5, 13, 119, 102);
                    this.window.addWidget((MapWidget)this.tree);
                } else {
                    this.setReceiveInputWhenHolding(false);
                    this.model = AttachmentModel.getDefaultModel(EntityType.MINECART);
                    ((MapWidgetText)this.window.addWidget((MapWidget)new MapWidgetText())).setText("Please select the\nMinecart to edit!").setColor((byte)18).setShadowColor(MapColorPalette.getSpecular((byte)18, (float)0.5f)).setPosition(20, 60);
                }
            }
        }
    }

    private static void setChildrenFocused(Attachment attachment, boolean focused) {
        if (attachment == null) {
            return;
        }
        for (Attachment child : attachment.getChildren()) {
            child.setFocused(focused);
            AttachmentEditor.setChildrenFocused(child, focused);
        }
    }

    public boolean onItemDrop(Player player, ItemStack item) {
        if (item == null) {
            return false;
        }
        MapWidget activated = this.getActivatedWidget();
        return activated instanceof ItemDropTarget ? ((ItemDropTarget)activated).acceptItem(item) : false;
    }

    private void pauseBlinking(FocusMode mode, int time) {
        this.updateFocus(mode);
        this.blinkCounter = -time;
    }

    private void updateFocus(FocusMode mode) {
        this._lastSelectedAttachments.forEach(mode::applyTo);
    }

    private static enum FocusMode {
        SELECTED(10),
        SELECTED_AND_CHILDREN(12),
        NONE(20);

        public final int phase;

        private FocusMode(int phase) {
            this.phase = phase;
        }

        public void applyTo(Attachment attachment) {
            switch (this.ordinal()) {
                case 2: {
                    HelperMethods.setFocusedRecursive(attachment, false);
                    break;
                }
                case 0: {
                    attachment.setFocused(true);
                    break;
                }
                case 1: {
                    HelperMethods.setFocusedRecursive(attachment, true);
                }
            }
        }

        public static FocusMode fromPhase(int phase) {
            for (FocusMode mode : FocusMode.values()) {
                if (mode.phase != phase) continue;
                return mode;
            }
            return null;
        }

        public static FocusMode fromCounter(int counter) {
            FocusMode result = SELECTED;
            for (FocusMode mode : FocusMode.values()) {
                if (mode.phase > counter) break;
                result = mode;
            }
            return result;
        }
    }
}

