/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.widgets;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.util.MapWidgetNavigator;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapWidgetRoot
extends MapWidget {
    private MapWidget _focusedWidget = null;
    private MapWidget _activatedWidget = this;
    private MapWidget _focusChangeFrom = this;
    private List<MapWidget> _focusHistory = new ArrayList<MapWidget>();

    public MapWidgetRoot(MapDisplay display) {
        this.parent = null;
        this.display = display;
        this.root = this;
    }

    public MapWidget getFocusedWidget() {
        return this._focusedWidget;
    }

    private static boolean isWidgetVisibleRecurse(MapWidget widget) {
        return widget == null || widget.isVisibleAndDisplayed();
    }

    private static MapWidgetDepthPair findFocusableWidget(MapWidget widget, int depth) {
        return MapWidgetRoot.isWidgetVisibleRecurse(widget.parent) ? MapWidgetRoot.findFocusableWidgetRecurse(widget, depth) : null;
    }

    private static MapWidgetDepthPair findFocusableWidgetRecurse(MapWidget widget, int depth) {
        if (!widget.isVisible()) {
            return null;
        }
        if (widget.isFocusable()) {
            return new MapWidgetDepthPair(widget, depth);
        }
        ++depth;
        MapWidgetDepthPair result = null;
        for (MapWidget child : widget.getWidgets()) {
            MapWidgetDepthPair pair = MapWidgetRoot.findFocusableWidget(child, depth);
            if (pair == null || result != null && pair.depth >= result.depth) continue;
            result = pair;
        }
        return result;
    }

    public void setFocusedWidget(MapWidget widget) {
        if (widget != null && !widget.isVisibleAndDisplayed()) {
            return;
        }
        if (widget != null && !widget.isFocusable()) {
            MapWidgetDepthPair pair = MapWidgetRoot.findFocusableWidget(widget, 0);
            if (pair == null) {
                return;
            }
            widget = pair.widget;
        }
        MapWidget prevFocus = this._focusedWidget;
        if (widget == null) {
            this._focusedWidget = null;
            this._focusChangeFrom = null;
            if (prevFocus != null) {
                prevFocus.handleRefreshFocus();
                prevFocus.invalidate();
            }
            return;
        }
        if (this._focusedWidget == widget) {
            this._focusChangeFrom = null;
            return;
        }
        MapWidget tmpParent = widget.parent;
        while (tmpParent != null && !tmpParent.isFocusable()) {
            tmpParent = tmpParent.parent;
        }
        if (tmpParent == null) {
            return;
        }
        if (tmpParent != this._activatedWidget) {
            this.setActivatedWidget(tmpParent);
        }
        this._focusedWidget = widget;
        if (prevFocus != null) {
            prevFocus.handleRefreshFocus();
            prevFocus.invalidate();
        }
        this.pushFocus(widget);
        if (this._focusedWidget != null) {
            this._focusedWidget.invalidate();
        }
        this._focusChangeFrom = null;
    }

    private void pushFocus(MapWidget widget) {
        if (widget != null) {
            this._focusHistory.remove(widget);
            this._focusHistory.add(widget);
            this.cleanupFocusHistory();
        }
    }

    private void cleanupFocusHistory() {
        Iterator<MapWidget> iter = this._focusHistory.iterator();
        while (iter.hasNext()) {
            if (iter.next().root == this) continue;
            iter.remove();
        }
    }

    @Override
    public void clearWidgets() {
        super.clearWidgets();
        this._focusedWidget = null;
        this._activatedWidget = this;
        this._focusChangeFrom = this;
        this._focusHistory.clear();
    }

    @Override
    public void handleDetach() {
        MapDisplay display = this.display;
        super.handleDetach();
        this.parent = null;
        this.display = display;
        this.root = this;
    }

    @Override
    public void performTickUpdates() {
        super.performTickUpdates();
    }

    public MapWidget getActivatedWidget() {
        return this._activatedWidget;
    }

    public void setActivatedWidget(MapWidget widget) {
        if (!MapWidgetRoot.isWidgetVisibleRecurse(widget)) {
            return;
        }
        if (this._activatedWidget == widget) {
            return;
        }
        this.pushFocus(this._activatedWidget);
        if (this._activatedWidget != null) {
            this._activatedWidget.invalidate();
            this._activatedWidget.onDeactivate();
        }
        if (widget == null) {
            MapWidget tmp = this._activatedWidget.parent;
            while (tmp != null && !tmp.isFocusable()) {
                tmp = tmp.parent;
            }
            if (tmp == null) {
                tmp = this;
            }
            this._activatedWidget = tmp;
            this._focusChangeFrom = tmp;
        } else {
            MapWidget tmp = widget;
            while (!(tmp == null || tmp.isFocusable() && tmp.isVisible())) {
                tmp = tmp.parent;
            }
            if (tmp == null) {
                tmp = this;
            }
            this._activatedWidget = tmp;
            this._focusChangeFrom = widget;
        }
        this._activatedWidget.invalidate();
    }

    @Override
    public void onTick() {
        if (this._focusChangeFrom != null) {
            List<MapWidget> widgets = MapWidgetNavigator.getFocusableWidgets(this._focusChangeFrom);
            if (!widgets.isEmpty()) {
                MapWidget result = widgets.get(0);
                for (int i = this._focusHistory.size() - 1; i >= 0; --i) {
                    if (!widgets.contains(this._focusHistory.get(i))) continue;
                    result = this._focusHistory.get(i);
                    break;
                }
                this.setFocusedWidget(result);
            } else {
                this.setFocusedWidget(null);
            }
        }
    }

    @Override
    public void onAttached() {
        this.setFocusable(true);
        this.activate();
    }

    private static class MapWidgetDepthPair {
        public final MapWidget widget;
        public final int depth;

        public MapWidgetDepthPair(MapWidget widget, int depth) {
            this.widget = widget;
            this.depth = depth;
        }
    }
}

