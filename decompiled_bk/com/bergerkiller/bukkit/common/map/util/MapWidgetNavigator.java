/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import java.util.ArrayList;
import java.util.List;

public class MapWidgetNavigator {
    private static boolean isWidgetVisibleRecurse(MapWidget widget) {
        while (widget != null) {
            if (!widget.isVisible()) {
                return false;
            }
            widget = widget.getParent();
        }
        return true;
    }

    public static List<MapWidget> getFocusableWidgets(MapWidget widget) {
        ArrayList<MapWidget> result = new ArrayList<MapWidget>();
        if (MapWidgetNavigator.isWidgetVisibleRecurse(widget)) {
            MapWidgetNavigator.addFocusableWidgets(result, widget);
        }
        return result;
    }

    public static void addFocusableWidgets(List<MapWidget> result, MapWidget widget) {
        for (MapWidget child : widget.getWidgets()) {
            if (!child.isVisible()) continue;
            if (child.isFocusable() && child.isEnabled() && child.getWidth() > 0 && child.getHeight() > 0) {
                result.add(child);
                continue;
            }
            MapWidgetNavigator.addFocusableWidgets(result, child);
        }
    }

    public static MapWidget getNextWidget(List<MapWidget> widgets, MapWidget current, MapPlayerInput.Key key) {
        if (widgets.isEmpty()) {
            return null;
        }
        int minWeight = Integer.MAX_VALUE;
        MapWidget result = current;
        for (MapWidget widget : widgets) {
            if (!MapWidgetNavigator.isWidgetVisibleRecurse(widget)) continue;
            int weight = Integer.MAX_VALUE;
            if (key == MapPlayerInput.Key.LEFT) {
                weight = MapWidgetNavigator.getWeightLeft(current, widget, key);
            } else if (key == MapPlayerInput.Key.RIGHT) {
                weight = MapWidgetNavigator.getWeightRight(current, widget, key);
            } else if (key == MapPlayerInput.Key.UP) {
                weight = MapWidgetNavigator.getWeightUp(current, widget, key);
            } else if (key == MapPlayerInput.Key.DOWN) {
                weight = MapWidgetNavigator.getWeightDown(current, widget, key);
            }
            if (weight >= minWeight) continue;
            minWeight = weight;
            result = widget;
        }
        return result;
    }

    private static int getWeightLeft(MapWidget current, MapWidget widget, MapPlayerInput.Key key) {
        int x = current.getAbsoluteX();
        int y1 = current.getAbsoluteY();
        int y2 = current.getAbsoluteY() + current.getHeight();
        int weight = x - widget.getAbsoluteX();
        int weight_a = widget.getAbsoluteY() - y2;
        int weight_b = y1 - (widget.getAbsoluteY() + widget.getHeight());
        if (weight > 0) {
            return MapWidgetNavigator.wab(weight, weight_a, weight_b);
        }
        return Integer.MAX_VALUE;
    }

    private static int getWeightRight(MapWidget current, MapWidget widget, MapPlayerInput.Key key) {
        int x = current.getAbsoluteX() + current.getWidth();
        int y1 = current.getAbsoluteY();
        int y2 = current.getAbsoluteY() + current.getHeight();
        int weight = widget.getAbsoluteX() + widget.getWidth() - x;
        int weight_a = widget.getAbsoluteY() - y2;
        int weight_b = y1 - (widget.getAbsoluteY() + widget.getHeight());
        if (weight > 0) {
            return MapWidgetNavigator.wab(weight, weight_a, weight_b);
        }
        return Integer.MAX_VALUE;
    }

    private static int getWeightUp(MapWidget current, MapWidget widget, MapPlayerInput.Key key) {
        int y = current.getAbsoluteY();
        int x1 = current.getAbsoluteX();
        int x2 = current.getAbsoluteX() + current.getWidth();
        int weight = y - widget.getAbsoluteY();
        int weight_a = widget.getAbsoluteX() - x2;
        int weight_b = x1 - (widget.getAbsoluteX() + widget.getWidth());
        if (weight > 0) {
            return MapWidgetNavigator.wab(weight, weight_a, weight_b);
        }
        return Integer.MAX_VALUE;
    }

    private static int getWeightDown(MapWidget current, MapWidget widget, MapPlayerInput.Key key) {
        int y = current.getAbsoluteY() + current.getHeight();
        int x1 = current.getAbsoluteX();
        int x2 = current.getAbsoluteX() + current.getWidth();
        int weight = widget.getAbsoluteY() + widget.getHeight() - y;
        int weight_a = widget.getAbsoluteX() - x2;
        int weight_b = x1 - (widget.getAbsoluteX() + widget.getWidth());
        if (weight > 0) {
            return MapWidgetNavigator.wab(weight, weight_a, weight_b);
        }
        return Integer.MAX_VALUE;
    }

    private static int wab(int weight, int weight_a, int weight_b) {
        if (weight_a > 0) {
            return MapWidgetNavigator.w(1, weight_a * weight);
        }
        if (weight_b > 0) {
            return MapWidgetNavigator.w(1, weight_b * weight);
        }
        return MapWidgetNavigator.w(0, weight);
    }

    private static int w(int n, int weight) {
        return n * 100000 + weight;
    }
}

