/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView$Tab
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetTabView;
import com.bergerkiller.bukkit.common.resources.SoundEffect;

public class MapWidgetVerticalNavigableList
extends MapWidgetTabView {
    public void onKeyPressed(MapKeyEvent event) {
        if (!this.shouldInterceptInput(event)) {
            super.onKeyPressed(event);
        } else if (event.getKey() == MapPlayerInput.Key.UP && this.getSelectedIndex() > 0) {
            this.display.playSound(SoundEffect.PISTON_EXTEND);
            this.setSelectedIndex(this.getSelectedIndex() - 1);
            this.getSelectedTab().activate();
            this.onNavigated(event, this.getSelectedTab());
        } else if (event.getKey() == MapPlayerInput.Key.DOWN && this.getSelectedIndex() < this.getTabCount() - 1 && this.getTab(this.getSelectedIndex() + 1).getWidgetCount() > 0) {
            this.display.playSound(SoundEffect.PISTON_EXTEND);
            this.setSelectedIndex(this.getSelectedIndex() + 1);
            this.getSelectedTab().activate();
            this.onNavigated(event, this.getSelectedTab());
        } else if (event.getKey() == MapPlayerInput.Key.DOWN) {
            this.onLastItemDown(event);
        } else if (event.getKey() == MapPlayerInput.Key.UP) {
            this.onFirstItemUp(event);
        } else {
            super.onKeyPressed(event);
        }
    }

    public void onNavigated(MapKeyEvent event, MapWidgetTabView.Tab tab) {
    }

    public void onFirstItemUp(MapKeyEvent event) {
        super.onKeyPressed(event);
    }

    public void onLastItemDown(MapKeyEvent event) {
        super.onKeyPressed(event);
    }

    public boolean shouldInterceptInput(MapKeyEvent event) {
        return true;
    }
}

