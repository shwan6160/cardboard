/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  org.bukkit.entity.EntityType
 */
package com.bergerkiller.bukkit.tc.attachments.ui.entity;

import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentEntity;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSelectionBox;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.entity.EntityType;

public class MapWidgetEntityTypeList
extends MapWidget {
    private final MapWidgetSelectionBox selector = new MapWidgetSelectionBox(){

        @Override
        public void onSelectedItemChanged() {
            MapWidgetEntityTypeList.this.onEntityTypeChanged();
        }
    };

    public void onAttached() {
        this.selector.clearItems();
        ArrayList<String> items = new ArrayList<String>();
        for (EntityType type : EntityType.values()) {
            if (!CartAttachmentEntity.isEntityTypeSupported(type)) continue;
            items.add(type.toString());
        }
        Collections.sort(items);
        for (String item : items) {
            this.selector.addItem(item);
        }
        this.addWidget(this.selector);
    }

    public void onBoundsChanged() {
        this.selector.setBounds(0, 0, this.getWidth(), this.getHeight());
    }

    public EntityType getEntityType() {
        return (EntityType)ParseUtil.parseEnum((String)this.selector.getSelectedItem(), (Object)EntityType.MINECART);
    }

    public void setEntityType(EntityType entityType) {
        this.selector.setSelectedItem(entityType.toString());
    }

    public void onEntityTypeChanged() {
    }
}

