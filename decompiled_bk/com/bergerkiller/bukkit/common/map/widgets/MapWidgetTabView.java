/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.widgets;

import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import java.util.ArrayList;
import java.util.List;

public class MapWidgetTabView
extends MapWidget {
    private final List<Tab> tabs = new ArrayList<Tab>();

    public <T extends Tab> T addTab(T tab) {
        T t = tab;
        if (((Tab)t).tabView != null) {
            ((Tab)t).tabView.removeTab(t);
            ((Tab)t).tabView = null;
        }
        ((Tab)t).tabView = this;
        this.tabs.add(tab);
        if (this.getWidgetCount() == 0) {
            this.addWidget(tab);
            this.updateBounds();
        }
        return tab;
    }

    public Tab addTab() {
        return this.addTab(new Tab());
    }

    public int getTabCount() {
        return this.tabs.size();
    }

    public Tab getTab(int index) {
        return this.tabs.get(index);
    }

    public int getSelectedIndex() {
        return this.getWidgetCount() == 0 ? -1 : this.tabs.indexOf(this.getWidget(0));
    }

    public MapWidgetTabView setSelectedIndex(int index) {
        if (index >= 0) {
            return this.setSelectedTab(this.tabs.get(index));
        }
        return this.setSelectedTab(null);
    }

    public Tab getSelectedTab() {
        return this.getWidgetCount() == 0 ? null : (Tab)this.getWidget(0);
    }

    public MapWidgetTabView setSelectedTab(Tab tab) {
        if (!this.tabs.contains(tab)) {
            throw new IllegalArgumentException("Tab is not part of this tab view");
        }
        if (this.getWidgetCount() == 0 || this.getWidget(0) != tab) {
            this.clearWidgets();
            this.addWidget(tab);
            this.updateBounds();
        }
        return this;
    }

    public void removeTab(Tab tab) {
        int index = this.tabs.indexOf(tab);
        if (index == -1) {
            return;
        }
        this.tabs.remove(index);
        if (this.getWidgetCount() > 0 && this.getWidget(0) == tab) {
            this.clearWidgets();
            if (this.tabs.size() > 0) {
                this.addWidget(this.tabs.get(Math.min(index, this.tabs.size() - 1)));
                this.updateBounds();
            }
        }
    }

    public void clearTabs() {
        this.tabs.clear();
        this.clearWidgets();
    }

    @Override
    public void onBoundsChanged() {
        this.updateBounds();
    }

    @Override
    public void onAttached() {
        if (this.getWidgetCount() == 0 && !this.tabs.isEmpty()) {
            this.addWidget(this.tabs.get(0));
            this.updateBounds();
        }
    }

    private void updateBounds() {
        if (this.getWidgetCount() > 0) {
            this.getWidget(0).setBounds(0, 0, this.getWidth(), this.getHeight());
        }
    }

    public static class Tab
    extends MapWidget {
        private MapWidgetTabView tabView = null;

        public Tab() {
            this.setRetainChildWidgets(true);
        }

        public int getIndex() {
            return this.tabView == null ? -1 : this.tabView.tabs.indexOf(this);
        }

        public boolean isSelected() {
            return this.tabView != null && this.tabView.getSelectedTab() == this;
        }

        public void select() {
            if (this.tabView != null) {
                this.tabView.setSelectedTab(this);
            }
        }

        public void remove() {
            if (this.tabView != null) {
                this.tabView.removeTab(this);
            }
        }
    }
}

