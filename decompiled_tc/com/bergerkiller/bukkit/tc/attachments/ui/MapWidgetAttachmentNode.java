/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  org.bukkit.ChatColor
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event$Result
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentConfig;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentBlock;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentItem;
import com.bergerkiller.bukkit.tc.attachments.ui.AttachmentEditor;
import com.bergerkiller.bukkit.tc.attachments.ui.ItemDropTarget;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentTree;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetBlinkyButton;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.AnimationMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.AppearanceMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.GeneralMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.PhysicalMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.menus.PositionMenu;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.AttachmentControllerMember;
import com.bergerkiller.bukkit.tc.utils.SetCallbackCollector;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MapWidgetAttachmentNode
extends MapWidget
implements ItemDropTarget {
    private static final int COL_WIDTH = 17;
    private final MapWidgetAttachmentTree tree;
    private static MapTexture expanded_icon = null;
    private static MapTexture collapsed_icon = null;
    private AttachmentConfig config;
    private final List<MapWidgetAttachmentNode> attachments = new ArrayList<MapWidgetAttachmentNode>();
    private MapWidgetAttachmentNode parentAttachment = null;
    private int col;
    private int row;
    private MapTexture icon = null;
    private boolean changingOrder = false;
    private boolean expanded = true;
    private MapWidgetMenuButton appearanceMenuButton;
    private final MapWidgetNameBox topNameBox = new MapWidgetNameBox();

    public static MapWidgetAttachmentNode createNewRoot(MapWidgetAttachmentTree tree, AttachmentConfig config) {
        return new MapWidgetAttachmentNode(null, tree, config);
    }

    public MapWidgetAttachmentNode(MapWidgetAttachmentNode parentAttachment, MapWidgetAttachmentTree tree, AttachmentConfig config) {
        this.parentAttachment = parentAttachment;
        this.tree = tree;
        this.config = config;
        this.loadFromConfig();
        this.setFocusable(true);
    }

    public void loadFromConfig() {
        this.attachments.clear();
        for (AttachmentConfig childConfig : this.config.children()) {
            this.attachments.add(new MapWidgetAttachmentNode(this, this.tree, childConfig));
        }
        boolean bl = this.expanded = this.parentAttachment == null || this.getEditorOption("expanded", true) != false;
        if (!this.expanded && this.attachments.isEmpty()) {
            this.expanded = true;
            this.setEditorOption("expanded", true, true);
        }
    }

    public boolean sync(AttachmentConfig config) {
        this.config = config;
        this.resetIcon();
        if (this.isActivated() && this.appearanceMenuButton != null) {
            this.appearanceMenuButton.setIcon(this.getIcon());
        }
        boolean changed = false;
        List<AttachmentConfig> childConfigs = config.children();
        for (int i = 0; i < childConfigs.size(); ++i) {
            AttachmentConfig childConfig = childConfigs.get(i);
            if (i < this.attachments.size()) {
                MapWidgetAttachmentNode node = this.attachments.get(i);
                if (node.getConfig() == childConfig.config()) {
                    changed |= node.sync(childConfig);
                    continue;
                }
                boolean found = false;
                for (int j = i + 1; j < this.attachments.size(); ++j) {
                    node = this.attachments.get(j);
                    if (node.getConfig() != childConfig.config()) continue;
                    this.attachments.remove(j);
                    this.attachments.add(i, node);
                    changed = true;
                    found = true;
                    node.sync(childConfig);
                    break;
                }
                if (found) continue;
            }
            this.attachments.add(i, new MapWidgetAttachmentNode(this, this.tree, childConfig));
            changed = true;
        }
        while (this.attachments.size() > childConfigs.size()) {
            this.attachments.remove(childConfigs.size());
            changed = true;
        }
        return changed;
    }

    public MapWidgetAttachmentTree getTree() {
        return this.tree;
    }

    public MapWidgetAttachmentNode getParentAttachment() {
        return this.parentAttachment;
    }

    public void setParentAttachment(MapWidgetAttachmentNode newParent) {
        this.parentAttachment = newParent;
    }

    public void openMenu(MenuItem item) {
        this.getTree().onMenuOpen(this, item);
    }

    public List<MapWidgetAttachmentNode> getChildAttachmentNodes() {
        return this.attachments;
    }

    public ConfigurationNode getConfig() {
        return this.config.config();
    }

    public AttachmentConfig getAttachmentConfig() {
        return this.config;
    }

    public <T> T getEditorOption(String name, T defaultValue) {
        ConfigurationNode config = this.getConfig();
        if (config.contains("editor." + name)) {
            return (T)config.get("editor." + name, defaultValue);
        }
        return defaultValue;
    }

    public <T> void setEditorOption(String name, T defaultValue, T value) {
        ConfigurationNode config = this.getConfig();
        if (!config.contains("editor." + name) && LogicUtil.bothNullOrEqual(defaultValue, value)) {
            return;
        }
        config.set("editor." + name, value);
    }

    public void update() {
        this.getTree().sync();
    }

    public MapWidgetAttachmentNode addAttachment(ConfigurationNode config) {
        return this.addAttachment(this.attachments.size(), config);
    }

    public MapWidgetAttachmentNode addAttachment(int index, ConfigurationNode config) {
        MapWidgetAttachmentNode attachment = new MapWidgetAttachmentNode(this, this.tree, this.config.addChild(index, config));
        this.attachments.add(index, attachment);
        return attachment;
    }

    public void remove() {
        if (this.parentAttachment != null && this.parentAttachment.attachments.remove(this)) {
            this.config.remove();
            this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "reset");
        }
    }

    public void setCell(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getCellColumn() {
        return this.col;
    }

    public int getCellRow() {
        return this.row;
    }

    public AttachmentType getType() {
        return AttachmentTypeRegistry.instance().fromConfig(this.getConfig());
    }

    public void setType(AttachmentType type) {
        AttachmentTypeRegistry.instance().toConfig(this.getConfig(), type);
    }

    public int[] getTargetPath() {
        return this.config.childPath();
    }

    @Deprecated
    public Attachment getAttachment() {
        List<Attachment> attachments = this.config.liveAttachments();
        return attachments.isEmpty() ? null : attachments.get(0);
    }

    public List<Attachment> getAttachments() {
        return this.config.liveAttachments();
    }

    public <T extends Attachment> List<T> getAttachmentsOfType(Class<T> type) {
        return this.config.liveAttachmentsOfType(type);
    }

    public Set<MinecartMember<?>> getMembersUsingAttachment() {
        SetCallbackCollector collector = new SetCallbackCollector();
        this.config.runAction(attachment -> {
            MinecartMember<?> member;
            AttachmentManager manager = attachment.getManager();
            if (manager instanceof AttachmentControllerMember && !(member = ((AttachmentControllerMember)manager).getMember()).isUnloaded()) {
                collector.accept(member);
            }
        });
        return collector.result();
    }

    public AttachmentEditor getEditor() {
        if (this.display == null && this.root != null) {
            return (AttachmentEditor)this.root.getDisplay();
        }
        return (AttachmentEditor)this.getDisplay();
    }

    public boolean checkModifyPermissions() {
        AttachmentType type;
        if (this.display != null && (type = this.getType()) != null) {
            for (Player player : this.display.getOwners()) {
                if (type.hasPermission(player)) continue;
                for (Player notifPlayer : this.display.getOwners()) {
                    notifPlayer.sendMessage(ChatColor.RED + "You do not have permission to modify this type of attachment");
                }
                return false;
            }
        }
        return true;
    }

    public void onAttached() {
        this.setSize(this.parent.getWidth(), 18);
    }

    public void onActivate() {
        List names;
        super.onActivate();
        if (this.display == null) {
            return;
        }
        this.display.playSound(SoundEffect.PISTON_EXTEND);
        int px = this.col * 17 + 1;
        this.appearanceMenuButton = (MapWidgetMenuButton)this.addWidget(new MapWidgetMenuButton(MenuItem.APPEARANCE));
        this.appearanceMenuButton.setIcon(this.getIcon()).setPosition(px, 1);
        px += 17;
        if (this.parentAttachment == null && this.getEditor().getEditedCartProperties() != null) {
            this.addWidget(new MapWidgetMenuButton(MenuItem.PHYSICAL).setPosition(px, 1));
            px += 17;
        }
        this.addWidget(new MapWidgetMenuButton(MenuItem.POSITION).setPosition(px, 1));
        this.addWidget(new MapWidgetMenuButton(MenuItem.ANIMATION).setPosition(px += 17, 1));
        this.addWidget(new MapWidgetMenuButton(MenuItem.GENERAL).setPosition(px += 17, 1));
        px += 17;
        if (this.isChangingOrder()) {
            for (MapWidget child : this.getWidgets()) {
                child.setEnabled(false);
            }
        }
        if (!(names = this.getConfig().getList("names", String.class)).isEmpty()) {
            int xoff = this.col * 17;
            this.topNameBox.setText(String.join((CharSequence)" - ", names));
            this.topNameBox.setBounds(xoff + 1, -this.topNameBox.getHeight(), this.getWidth() - xoff - 2, this.topNameBox.getHeight());
            this.addWidget(this.topNameBox);
        }
    }

    public void onDeactivate() {
        this.clearWidgets();
        this.display.playSound(SoundEffect.PISTON_CONTRACT);
    }

    public void onFocus() {
        this.activate();
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (event.getKey() == MapPlayerInput.Key.LEFT && this.parentAttachment != null && this.getWidgetCount() > 0 && this.getWidget(0).isFocused() && !this.attachments.isEmpty()) {
            this.setExpanded(!this.isExpanded());
        } else {
            super.onKeyPressed(event);
        }
    }

    @Override
    public boolean acceptItem(ItemStack item) {
        if (this.getType() == CartAttachmentItem.TYPE) {
            this.getConfig().set("item", (Object)item.clone());
            this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
            this.resetIcon();
            ((MapWidgetMenuButton)this.getWidget(0)).setIcon(this.getIcon());
            return true;
        }
        return false;
    }

    public void onBlockInteract(PlayerInteractEvent event) {
        Block block;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.getType() == CartAttachmentBlock.TYPE && (block = event.getClickedBlock()) != null) {
            BlockData blockData = WorldUtil.getBlockData((Block)block);
            this.getConfig().set("blockData", (Object)blockData.serializeToString());
            this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
            this.resetIcon();
            ((MapWidgetMenuButton)this.getWidget(0)).setIcon(this.getIcon());
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

    public void onDraw() {
        int px = this.col * 17;
        if (this.isActivated() || this.isFocused()) {
            byte bgColor = this.getEditor().isEditingSavedModel() ? MapColorPalette.getColor((int)77, (int)238, (int)250) : MapColorPalette.getColor((int)220, (int)255, (int)220);
            this.view.fillRectangle(px, 0, this.getWidth() - px, this.getHeight(), bgColor);
        }
        if (this.parentAttachment != null) {
            int n;
            int dotOffset = (this.row - this.parentAttachment.row & 1) == 1 ? 1 : 0;
            byte dotColor = MapColorPalette.getColor((int)64, (int)64, (int)64);
            for (n = 0; n < 5; ++n) {
                this.view.drawPixel(px - 17 + 8, n * 2 + dotOffset, dotColor);
            }
            for (n = 1; n < 5; ++n) {
                this.view.drawPixel(px - 17 + 8 + n * 2, 8 + dotOffset, dotColor);
            }
            int childIdx = this.parentAttachment.attachments.indexOf(this);
            if (childIdx != this.parentAttachment.attachments.size() - 1) {
                for (int n2 = 5; n2 < 9; ++n2) {
                    this.view.drawPixel(px - 17 + 8, n2 * 2 + dotOffset, dotColor);
                }
            }
            int tmpX = px - 26;
            MapWidgetAttachmentNode tmpNode = this.parentAttachment;
            while (tmpNode != null) {
                MapWidgetAttachmentNode tmpNodeParent = tmpNode.parentAttachment;
                if (tmpNodeParent != null && tmpNode != tmpNodeParent.attachments.get(tmpNodeParent.attachments.size() - 1)) {
                    int childDotOffset = (this.row - tmpNodeParent.row & 1) == 1 ? 1 : 0;
                    for (int n3 = 0; n3 < 9; ++n3) {
                        this.view.drawPixel(tmpX, n3 * 2 + childDotOffset, dotColor);
                    }
                }
                tmpNode = tmpNodeParent;
                tmpX -= 17;
            }
            if (!this.attachments.isEmpty()) {
                if (this.expanded) {
                    if (expanded_icon == null) {
                        expanded_icon = this.getDisplay().loadTexture("com/bergerkiller/bukkit/tc/textures/attachments/expanded.png");
                    }
                    this.view.draw((MapCanvas)expanded_icon, px - 9 - expanded_icon.getWidth() / 2, (this.view.getHeight() - expanded_icon.getHeight()) / 2 + dotOffset);
                } else {
                    if (collapsed_icon == null) {
                        collapsed_icon = this.getDisplay().loadTexture("com/bergerkiller/bukkit/tc/textures/attachments/collapsed.png");
                    }
                    this.view.draw((MapCanvas)collapsed_icon, px - 9 - collapsed_icon.getWidth() / 2, (this.view.getHeight() - collapsed_icon.getHeight()) / 2 + dotOffset);
                }
            }
        }
        if (!this.isActivated()) {
            this.view.draw((MapCanvas)this.getIcon(), px + 1, 1);
        }
        if (this.isChangingOrder()) {
            this.view.drawRectangle(px, 0, this.getWidth() - px, this.getHeight(), (byte)18);
        } else if (this.isFocused()) {
            this.view.drawRectangle(px, 0, this.getWidth() - px, this.getHeight(), (byte)119);
        } else if (this.isActivated()) {
            this.view.drawRectangle(px, 0, this.getWidth() - px, this.getHeight(), (byte)30);
        }
    }

    public void resetIcon() {
        this.icon = null;
    }

    public void setChangingOrder(boolean changing) {
        if (this.changingOrder != changing) {
            this.changingOrder = changing;
            this.topNameBox.invalidate();
            this.invalidate();
            for (MapWidget child : this.getWidgets()) {
                child.setEnabled(!changing);
            }
        }
    }

    public boolean isChangingOrder() {
        return this.changingOrder;
    }

    public void setExpanded(boolean expanded) {
        if (this.expanded != expanded) {
            this.expanded = expanded;
            this.setEditorOption("expanded", true, this.expanded);
            this.getTree().updateView();
            this.invalidate();
        }
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    private MapTexture getIcon() {
        if (this.icon == null) {
            AttachmentType type = this.getType();
            this.icon = type == null ? MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/missing.png") : this.getType().getIcon(this.getConfig());
        }
        return this.icon;
    }

    public String toString() {
        AttachmentType type = this.getType();
        String name = type == null ? "MISSING_TYPE" : type.toString();
        for (int p : this.getTargetPath()) {
            name = name + "." + p;
        }
        return name;
    }

    private class MapWidgetNameBox
    extends MapWidget {
        private static final int SCROLL_DELAY = 40;
        private static final int SCROLL_HOLD = 20;
        private static final int SCROLL_STEP = 3;
        private String text = "";
        private int textWidth = -1;
        private int textScroll = 0;
        private int delayCtr = 0;
        private int holdCtr = 0;

        public MapWidgetNameBox() {
            this.setDepthOffset(1);
            this.setSize(64, 6);
        }

        public void setText(String text) {
            this.text = text;
            this.textWidth = -1;
            this.textScroll = 0;
            this.delayCtr = 0;
            this.holdCtr = 0;
            this.invalidate();
        }

        private int getTextWidth() {
            int w = this.textWidth;
            if (w == -1) {
                this.textWidth = w = this.view.calcFontSize((MapFont)MapFont.TINY, (CharSequence)this.text).width;
            }
            return w;
        }

        public void onTick() {
            int overflow = this.getTextWidth() - this.getWidth() + 1;
            if (overflow > 0 && ++this.delayCtr > 40) {
                int newScroll = Math.min(overflow, this.textScroll + 3);
                if (newScroll != this.textScroll) {
                    this.textScroll = newScroll;
                    this.invalidate();
                } else if (++this.holdCtr > 20) {
                    this.delayCtr = 0;
                    this.holdCtr = 0;
                    this.textScroll = 0;
                    this.invalidate();
                }
            }
        }

        public void onDraw() {
            if (this.text.isEmpty() || MapWidgetAttachmentNode.this.isChangingOrder()) {
                return;
            }
            int width = Math.min(this.getWidth(), this.getTextWidth() + 1);
            int x = (this.getWidth() - width) / 2;
            this.view.fillRectangle(x, 0, width, this.getHeight(), (byte)30);
            this.view.getView(x + 1, 1, width - 2, this.getHeight() - 1).draw(MapFont.TINY, -this.textScroll, 0, MapColorPalette.getColor((int)255, (int)255, (int)255), (CharSequence)this.text);
        }
    }

    private class MapWidgetMenuButton
    extends MapWidgetBlinkyButton {
        private final MenuItem _menu;

        public MapWidgetMenuButton(MenuItem menu) {
            this._menu = menu;
            this.setTooltip(Character.toUpperCase(menu.name().charAt(0)) + menu.name().substring(1).toLowerCase(Locale.ENGLISH));
            if (menu.getIcon() != null) {
                this.setIcon(menu.getIcon());
            }
        }

        @Override
        public void onClick() {
            MapWidgetAttachmentNode.this.openMenu(this._menu);
        }
    }

    public static enum MenuItem {
        APPEARANCE(AppearanceMenu::new, null),
        POSITION(PositionMenu::new, "attachments/move.png"),
        ANIMATION(AnimationMenu::new, "attachments/animation.png"),
        GENERAL(GeneralMenu::new, "attachments/general_menu.png"),
        PHYSICAL(PhysicalMenu::new, "attachments/physical.png");

        private final Supplier<? extends MapWidgetMenu> _menuConstructor;
        private final String _icon;

        private MenuItem(Supplier<? extends MapWidgetMenu> menuConstructor, String icon) {
            this._menuConstructor = menuConstructor;
            this._icon = icon;
        }

        public String getIcon() {
            return this._icon;
        }

        public MapWidgetMenu createMenu(MapWidgetAttachmentNode attachmentNode) {
            MapWidgetMenu menu = this._menuConstructor.get();
            menu.setAttachment(attachmentNode);
            return menu;
        }
    }
}

