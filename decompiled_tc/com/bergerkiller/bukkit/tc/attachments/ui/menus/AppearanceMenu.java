/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapEventPropagation
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.tc.attachments.ui.menus;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentType;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.ui.ItemDropTarget;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSelectionBox;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AppearanceMenu
extends MapWidgetMenu
implements ItemDropTarget {
    private final MapWidgetTabView tabView = new MapWidgetTabView();
    private AttachmentTypeRegistry typeRegistry;
    private List<TypePage> pages;

    public AppearanceMenu() {
        this.setBounds(5, 15, 118, 104);
        this.setBackgroundColor((byte)50);
    }

    @Override
    public void onAttached() {
        super.onAttached();
        this.typeRegistry = AttachmentTypeRegistry.instance();
        List<AttachmentType> types = this.typeRegistry.all();
        this.pages = new ArrayList<TypePage>(types.size());
        for (AttachmentType type : types) {
            boolean listed = true;
            for (Player player : this.display.getOwners()) {
                if (type.isListed(player)) continue;
                listed = false;
                break;
            }
            if (!listed) continue;
            this.pages.add(new TypePage(type, this.tabView.addTab()));
        }
        this.tabView.setPosition(9, 16);
        this.addWidget((MapWidget)this.tabView);
        MapWidgetSelectionBox typeSelectionBox = (MapWidgetSelectionBox)this.addWidget(new MapWidgetSelectionBox(){

            @Override
            public void onSelectedItemChanged() {
                int index = this.getSelectedIndex();
                if (index >= 0 && index < AppearanceMenu.this.pages.size()) {
                    AppearanceMenu.this.setPage((TypePage)AppearanceMenu.this.pages.get(index));
                }
            }
        });
        AttachmentType selected = this.typeRegistry.fromConfig(this.getAttachment().getConfig());
        for (TypePage page : this.pages) {
            typeSelectionBox.addItem(page.type.getName());
            if (selected == null || !selected.getID().equalsIgnoreCase(page.type.getID())) continue;
            typeSelectionBox.setSelectedIndex(typeSelectionBox.getItemCount() - 1);
        }
        typeSelectionBox.setBounds(9, 3, 100, 11);
        this.setType(selected);
        typeSelectionBox.focus();
    }

    public void setType(AttachmentType type) {
        for (TypePage page : this.pages) {
            if (page.type != type) continue;
            this.setPage(page);
            return;
        }
        this.setPage(this.pages.get(0));
    }

    private void setPage(TypePage page) {
        if (this.typeRegistry.fromConfig(this.getAttachment().getConfig()) != page.type) {
            this.typeRegistry.toConfig(this.getAttachment().getConfig(), page.type);
            this.sendStatusChange(MapEventPropagation.DOWNSTREAM, "changed");
            this.getAttachment().resetIcon();
        }
        if (!page.appearanceCreated) {
            page.appearanceCreated = true;
            try {
                page.type.migrateConfiguration(this.attachment.getConfig());
            }
            catch (Throwable t) {
                TrainCarts.plugin.getLogger().log(Level.SEVERE, "Failed to migrate attachment configuration of " + page.type.getName(), t);
            }
            try {
                page.type.createAppearanceTab(page.tab, this.attachment);
            }
            catch (Throwable t) {
                TrainCarts.plugin.getLogger().log(Level.SEVERE, "Failed to display appearance tab for " + page.type.getName(), t);
                page.tab.clear();
                ((MapWidgetText)page.tab.addWidget((MapWidget)new MapWidgetText())).setText("An error occurred!").setColor((byte)18).setPosition(5, 5);
            }
        }
        page.tab.select();
    }

    @Override
    public boolean acceptItem(ItemStack item) {
        for (MapWidget widget : this.tabView.getSelectedTab().getWidgets()) {
            if (!(widget instanceof ItemDropTarget) || !((ItemDropTarget)widget).acceptItem(item)) continue;
            return true;
        }
        return false;
    }

    public ConfigurationNode getConfig() {
        return this.attachment.getConfig();
    }

    public MapWidgetAttachmentNode getAttachment() {
        return this.attachment;
    }

    private static class TypePage {
        public final AttachmentType type;
        public final MapWidgetTabView.Tab tab;
        public boolean appearanceCreated;

        public TypePage(AttachmentType type, MapWidgetTabView.Tab tab) {
            this.type = type;
            this.tab = tab;
            this.appearanceCreated = false;
        }
    }
}

