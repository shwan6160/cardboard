/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.map.widgets;

import com.bergerkiller.bukkit.common.events.map.MapClickEvent;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.events.map.MapStatusEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayEvents;
import com.bergerkiller.bukkit.common.map.MapEventPropagation;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.util.MapWidgetNavigator;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetRoot;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MapWidget
implements MapDisplayEvents {
    protected MapWidgetRoot root = null;
    protected MapWidget parent = null;
    protected MapDisplay display = null;
    protected MapDisplay.Layer layer = null;
    protected MapCanvas view;
    private int _z = 0;
    private int _x;
    private int _y;
    private int _width;
    private int _height;
    private MapCanvas _lastView = null;
    private boolean _positionAbsolute = false;
    private boolean _enabled = true;
    private boolean _invalidated = true;
    private boolean _focusable = false;
    private boolean _attached = false;
    private boolean _boundsChanged = true;
    private boolean _wasFocused = false;
    private boolean _retainChildren = false;
    private boolean _clipParent = false;
    private boolean _visible = true;
    private MapWidget[] _children = new MapWidget[0];

    @Override
    public void onAttached() {
    }

    @Override
    public void onDetached() {
    }

    public void onActivate() {
        if (this.root != null) {
            this.root.setActivatedWidget(this);
        }
    }

    public void onDeactivate() {
    }

    public void onFocus() {
    }

    public void onBlur() {
    }

    @Override
    public void onTick() {
    }

    public void onDraw() {
    }

    public void onBoundsChanged() {
    }

    public final MapWidget getParent() {
        return this.parent;
    }

    public final MapWidget getNextInputWidget() {
        MapWidget focused;
        MapWidget tmp = focused = this.getInputWidget();
        while (tmp != null && tmp.parent != this) {
            tmp = tmp.parent;
        }
        return tmp;
    }

    public final void setDepthOffset(int z) {
        this._z = z;
    }

    public final boolean isPositionAbsolute() {
        return this._positionAbsolute;
    }

    public final int getX() {
        return this._x;
    }

    public final int getY() {
        return this._y;
    }

    public final int getWidth() {
        return this._width;
    }

    public final int getHeight() {
        return this._height;
    }

    public final int getAbsoluteX() {
        return this.parent == null || this._positionAbsolute ? this._x : this._x + this.parent.getAbsoluteX();
    }

    public final int getAbsoluteY() {
        return this.parent == null || this._positionAbsolute ? this._y : this._y + this.parent.getAbsoluteY();
    }

    public final MapDisplay getDisplay() {
        return this.display;
    }

    public final boolean isVisible() {
        return this._visible;
    }

    public final boolean isVisibleAndDisplayed() {
        MapWidget w = this;
        while (w != null) {
            if (!w.isVisible()) {
                return false;
            }
            w = w.parent;
        }
        return true;
    }

    public final MapWidget setVisible(boolean visible) {
        if (this._visible != visible) {
            this._visible = visible;
            this.invalidate();
            if (this.parent == null || this.parent.isVisibleAndDisplayed()) {
                this.invalidateRecurseChildrenIfVisible();
            }
        }
        return this;
    }

    public final boolean isFocusable() {
        return this._focusable;
    }

    public final void setFocusable(boolean focusable) {
        this._focusable = focusable;
    }

    public final boolean isClipParent() {
        return this._clipParent;
    }

    public final void setClipParent(boolean clipParent) {
        if (this._clipParent != clipParent) {
            this._clipParent = clipParent;
            this.refreshView(true);
        }
    }

    public final boolean isFocused() {
        return this.root != null && this == this.root.getFocusedWidget();
    }

    public final void focus() {
        if (this.root != null) {
            this.root.setFocusedWidget(this);
        }
    }

    public final boolean isActivated() {
        return this.root != null && this == this.root.getActivatedWidget();
    }

    public final void activate() {
        if (this.isEnabled()) {
            this.onActivate();
        }
    }

    public final void deactivate() {
        if (this.root == null) {
            return;
        }
        boolean isChildOrSelfActivated = false;
        MapWidget activated = this.root.getActivatedWidget();
        while (activated != null) {
            if (activated == this) {
                isChildOrSelfActivated = true;
                break;
            }
            activated = activated.parent;
        }
        if (isChildOrSelfActivated) {
            MapWidget parent = this.parent;
            while (parent != null && !parent.isFocusable()) {
                parent = parent.parent;
            }
            if (parent == null) {
                parent = this.root;
            }
            if (parent != null) {
                parent.activate();
            }
        }
    }

    public final boolean isNavigableFocus() {
        if (!this.isFocusable() || this.root == null) {
            return false;
        }
        MapWidget activated = this.root.getActivatedWidget();
        MapWidget parent = this.parent;
        while (parent != null) {
            if (parent == activated) {
                return true;
            }
            parent = parent.parent;
        }
        return false;
    }

    public final boolean isEnabled() {
        return this._enabled && (this.parent == null || this.parent.isEnabled());
    }

    public final MapWidget setEnabled(boolean enabled) {
        if (this._enabled != enabled) {
            this._enabled = enabled;
            this.invalidate();
        }
        return this;
    }

    public final List<MapWidget> getWidgets() {
        return Arrays.asList(this._children);
    }

    public final MapWidget getWidget(int index) {
        return index >= 0 && index < this._children.length ? this._children[index] : null;
    }

    public final int getWidgetCount() {
        return this._children.length;
    }

    public final void removeWidget() {
        if (this.parent != null) {
            this.parent.removeWidget(this);
        }
    }

    public void clearWidgets() {
        MapWidget[] old_children;
        for (MapWidget old_child : old_children = this._children) {
            old_child.handleDetach();
        }
        if (this._children == old_children) {
            this._children = new MapWidget[0];
        } else {
            ArrayList<MapWidget> remaining = new ArrayList<MapWidget>(Arrays.asList(this._children));
            block1: for (MapWidget old_child : old_children) {
                for (int i = 0; i < remaining.size(); ++i) {
                    if (remaining.get(i) != old_child) continue;
                    remaining.remove(i);
                    continue block1;
                }
            }
            this._children = LogicUtil.toArray(remaining, MapWidget.class);
        }
    }

    public final <T extends MapWidget> T addWidget(T widget) {
        this._children = Arrays.copyOf(this._children, this._children.length + 1);
        this._children[this._children.length - 1] = widget;
        this.handleWidgetAdded(widget);
        return widget;
    }

    public final <T extends MapWidget> T swapWidget(MapWidget oldWidget, T newWidget) {
        int i;
        MapWidget[] old_children = this._children;
        int oldIndex = -1;
        for (i = 0; i < old_children.length; ++i) {
            if (old_children[i] != oldWidget) continue;
            oldIndex = i;
            break;
        }
        if (oldIndex == -1) {
            return this.addWidget(newWidget);
        }
        oldWidget.handleDetach();
        if (old_children == this._children) {
            this._children = (MapWidget[])this._children.clone();
            this._children[oldIndex] = newWidget;
            this.handleWidgetAdded(newWidget);
            return newWidget;
        }
        for (i = 0; i < this._children.length; ++i) {
            if (this._children[i] != oldWidget) continue;
            this._children = (MapWidget[])this._children.clone();
            this._children[i] = newWidget;
            this.handleWidgetAdded(newWidget);
            return newWidget;
        }
        return this.addWidget(newWidget);
    }

    private final void handleWidgetAdded(MapWidget child) {
        child.root = this.root;
        child.parent = this;
        child._attached = false;
        child._invalidated = true;
        child._boundsChanged = true;
        for (MapWidget subChild : child.getWidgets()) {
            child.handleWidgetAdded(subChild);
        }
    }

    public final boolean removeWidget(MapWidget widget) {
        int i;
        int index = -1;
        for (i = 0; i < this._children.length; ++i) {
            if (this._children[i] != widget) continue;
            index = i;
            break;
        }
        if (index == -1) {
            return false;
        }
        widget.handleDetach();
        if (index >= this._children.length || this._children[index] != widget) {
            index = -1;
            for (i = 0; i < this._children.length; ++i) {
                if (this._children[i] != widget) continue;
                index = i;
                break;
            }
        }
        if (index != -1) {
            MapWidget[] new_children = new MapWidget[this._children.length - 1];
            int dest_idx = 0;
            for (int i2 = 0; i2 < this._children.length; ++i2) {
                if (i2 == index) continue;
                new_children[dest_idx++] = this._children[i2];
            }
            this._children = new_children;
        }
        return true;
    }

    public final MapWidget clear() {
        if (this.view != null) {
            this.view.clear();
        }
        return this;
    }

    public final void invalidate() {
        this._invalidated = true;
    }

    private void invalidateRecurseChildrenIfVisible() {
        for (MapWidget child : this._children) {
            if (!child.isVisible()) continue;
            child.invalidate();
            child.invalidateRecurseChildrenIfVisible();
        }
    }

    public final MapWidget setRetainChildWidgets(boolean retain) {
        this._retainChildren = retain;
        return this;
    }

    public final void sendStatusChange(String name) {
        this.sendStatusChange(MapEventPropagation.BROADCAST, name, null);
    }

    public final void sendStatusChange(String name, Object argument) {
        this.sendStatusChange(MapEventPropagation.BROADCAST, name, argument);
    }

    public final void sendStatusChange(MapEventPropagation propagationMode, String name) {
        this.sendStatusChange(propagationMode, name, null);
    }

    public final void sendStatusChange(MapEventPropagation propagationMode, String name, Object argument) {
        switch (propagationMode) {
            case BROADCAST: {
                if (this.display == null) break;
                this.display.sendStatusChange(name, argument);
                break;
            }
            case UPSTREAM: {
                MapWidget.sendStatusChangeUpstream(this, new MapStatusEvent(name, argument));
                break;
            }
            case DOWNSTREAM: {
                MapWidget.sendStatusChangeDownstream(this, new MapStatusEvent(name, argument));
            }
        }
    }

    private static void sendStatusChangeUpstream(MapWidget widget, MapStatusEvent event) {
        widget.onStatusChanged(event);
        for (MapWidget child : widget.getWidgets()) {
            MapWidget.sendStatusChangeUpstream(child, event);
        }
    }

    private static void sendStatusChangeDownstream(MapWidget widget, MapStatusEvent event) {
        widget.onStatusChanged(event);
        if (widget.parent != null) {
            MapWidget.sendStatusChangeDownstream(widget.parent, event);
        } else if (widget.display != null) {
            widget.display.onStatusChanged(event);
        }
    }

    void performTickUpdates() {
        this.handleAttach();
        this.clearInvalidatedAreas(false);
        this.handleTick();
        this.clearInvalidatedAreas(true);
        this.handleDraw();
    }

    public final MapWidget setPosition(int x, int y) {
        return this.setBounds(x, y, this._width, this._height);
    }

    public final MapWidget setPositionAbsolute(boolean absolute) {
        if (this._positionAbsolute != absolute) {
            this._positionAbsolute = absolute;
            this._boundsChanged = true;
            this.refreshView(true);
        }
        return this;
    }

    public final MapWidget setSize(int width, int height) {
        return this.setBounds(this._x, this._y, width, height);
    }

    public final MapWidget setSize(Dimension dimension) {
        return this.setSize(dimension.width, dimension.height);
    }

    public final MapWidget setBounds(int x, int y, int width, int height) {
        this._x = x;
        this._y = y;
        this._width = width;
        this._height = height;
        this._boundsChanged = true;
        this.refreshView(true);
        return this;
    }

    private final void refreshView(boolean refreshChildren) {
        if (this.layer != null) {
            if (refreshChildren) {
                this.refreshViewRecurse(this.getAbsoluteX(), this.getAbsoluteY());
            } else {
                this.refreshViewSelf(this.getAbsoluteX(), this.getAbsoluteY());
            }
        }
    }

    private final void refreshViewRecurse(int absoluteX, int absoluteY) {
        if (this.layer == null) {
            return;
        }
        this.refreshViewSelf(absoluteX, absoluteY);
        for (MapWidget child : this._children) {
            int childX = child.getX();
            int childY = child.getY();
            if (!child.isPositionAbsolute()) {
                childX += absoluteX;
                childY += absoluteY;
            }
            child.refreshViewRecurse(childX, childY);
        }
    }

    private final void refreshViewSelf(int absoluteX, int absoluteY) {
        this.view = this.createParentClip(this.layer).getView(absoluteX, absoluteY, this._width, this._height);
        this.invalidate();
    }

    private final MapCanvas createParentClip(MapDisplay.Layer layer) {
        if (!this._clipParent || this.parent == null) {
            return layer;
        }
        if (!this._positionAbsolute && this._x >= 0 && this._y >= 0 && this._x + this._width < this.parent.getWidth() && this._y + this._height < this.parent.getHeight()) {
            return this.parent.createParentClip(layer);
        }
        return this.parent.createParentClip(layer).getClip(this.parent.getAbsoluteX(), this.parent.getAbsoluteY(), this.parent.getWidth(), this.parent.getHeight());
    }

    private final void clearInvalidatedAreas(boolean checkOverlappingChildren) {
        if (this.parent != null) {
            this.clearInvalidatedAreas(this.parent.getAbsoluteX(), this.parent.getAbsoluteY(), checkOverlappingChildren);
        } else {
            this.clearInvalidatedAreas(0, 0, checkOverlappingChildren);
        }
    }

    private final void clearInvalidatedAreas(int absoluteX, int absoluteY, boolean checkOverlappingChildren) {
        if (this.layer == null) {
            return;
        }
        if (this._positionAbsolute) {
            absoluteX = this._x;
            absoluteY = this._y;
        } else {
            absoluteX += this._x;
            absoluteY += this._y;
        }
        MapCanvas currentView = this.view;
        if (currentView == null || currentView.getViewAbsoluteX() != absoluteX || currentView.getViewAbsoluteY() != absoluteY || currentView.getWidth() != this._width || currentView.getHeight() != this._height) {
            this.refreshViewRecurse(absoluteX, absoluteY);
            currentView = this.view;
        }
        if (this != this.root && this._lastView != null) {
            if (currentView == null || !this._lastView.isSameView(currentView)) {
                this.invalidate();
            }
            if (this._invalidated) {
                this._lastView.clear();
                this._lastView = null;
            }
        }
        if (checkOverlappingChildren) {
            for (MapWidget child : this._children) {
                if (!child._invalidated) continue;
                int childX = child.getX();
                int childY = child.getY();
                if (!child.isPositionAbsolute()) {
                    childX += absoluteX;
                    childY += absoluteY;
                }
                for (MapWidget otherChild : this._children) {
                    if (otherChild == child || otherChild._invalidated) continue;
                    int otherChildX = otherChild.getX();
                    int otherChildY = otherChild.getY();
                    if (!otherChild.isPositionAbsolute()) {
                        otherChildX += absoluteX;
                        otherChildY += absoluteY;
                    }
                    if (otherChildX + otherChild.getWidth() < childX || otherChildX > childX + child.getWidth() || otherChildY + otherChild.getHeight() < childY || otherChildY > childY + child.getHeight()) continue;
                    otherChild.invalidate();
                }
            }
        }
        for (MapWidget child : this._children) {
            child.clearInvalidatedAreas(absoluteX, absoluteY, checkOverlappingChildren);
        }
    }

    private final void handleAttach() {
        if (!this._attached) {
            this._attached = true;
            if (this.parent != null) {
                this.root = this.parent.root;
                this.display = this.parent.display;
                this.layer = this.parent.layer.next();
            } else if (this.display != null) {
                this.layer = this.display.getTopLayer().next();
            }
            if (this.layer != null) {
                int n;
                for (n = this._z; n > 0; --n) {
                    this.layer = this.layer.next();
                }
                while (n < 0) {
                    this.layer = this.layer.previous();
                    ++n;
                }
                this.refreshView(false);
            }
            this.onAttached();
            if (this.root != null && this.root.getFocusedWidget() == null && this.isNavigableFocus()) {
                this.focus();
            }
        }
        if (this._attached) {
            for (MapWidget child : this._children) {
                child.handleAttach();
            }
        }
    }

    private final void handleTick() {
        if (!this._attached) {
            return;
        }
        this.onTick();
        if (this._boundsChanged) {
            this._boundsChanged = false;
            this.onBoundsChanged();
        }
        this.handleRefreshFocus();
        for (MapWidget child : this._children) {
            child.handleTick();
        }
    }

    final void handleRefreshFocus() {
        if (this._wasFocused != this.isFocused()) {
            boolean bl = this._wasFocused = !this._wasFocused;
            if (this._wasFocused) {
                this.onFocus();
            } else {
                this.onBlur();
            }
        }
    }

    private final void handleDraw() {
        if (this.parent != null) {
            boolean parentVisible = true;
            MapWidget tmp = this.parent;
            do {
                parentVisible &= tmp.isVisible();
            } while ((tmp = tmp.parent) != null);
            this.handleDraw(parentVisible);
        } else {
            this.handleDraw(true);
        }
    }

    private final void handleDraw(boolean visible) {
        if (!this._attached) {
            return;
        }
        visible &= this.isVisible();
        if (this._invalidated) {
            this._lastView = this.view;
            if (this != this.root && visible) {
                this.onDraw();
            }
            this._invalidated = false;
        }
        for (MapWidget child : this._children) {
            child.handleDraw(visible);
        }
    }

    void handleDetach() {
        if (this._attached) {
            this._attached = false;
            this.deactivate();
            if (this._retainChildren) {
                for (MapWidget old_child : this._children) {
                    old_child.handleDetach();
                }
            } else {
                this.clearWidgets();
            }
            this.onDetached();
            if (this._lastView != null) {
                this._lastView.clear();
            }
            this.display = null;
            this.view = null;
            this.layer = null;
            this.root = null;
            this._lastView = null;
        }
    }

    public void handleNavigation(MapKeyEvent event) {
        MapWidget focused = this.root.getFocusedWidget();
        if (event.getKey() == MapPlayerInput.Key.ENTER) {
            if (focused != null) {
                focused.activate();
            }
        } else if (event.getKey() == MapPlayerInput.Key.BACK) {
            this.deactivate();
        } else {
            List<MapWidget> widgets = MapWidgetNavigator.getFocusableWidgets(this);
            MapWidget nextFocus = null;
            if (focused != null) {
                nextFocus = focused.navigateNextWidget(widgets, event.getKey());
            } else if (!widgets.isEmpty()) {
                nextFocus = widgets.get(0);
            }
            if (nextFocus != null) {
                nextFocus.focus();
            }
        }
    }

    protected MapWidget navigateNextWidget(List<MapWidget> widgets, MapPlayerInput.Key key) {
        return MapWidgetNavigator.getNextWidget(widgets, this, key);
    }

    @Override
    public void onKey(MapKeyEvent event) {
        MapWidget next = this.getNextInputWidget();
        if (next != null) {
            next.onKey(event);
        }
    }

    @Override
    public void onKeyPressed(MapKeyEvent event) {
        if (this == this.getInputWidget()) {
            this.root.getActivatedWidget().handleNavigation(event);
            return;
        }
        MapWidget next = this.getNextInputWidget();
        if (next != null) {
            next.onKeyPressed(event);
        }
    }

    @Override
    public void onKeyReleased(MapKeyEvent event) {
        MapWidget next = this.getNextInputWidget();
        if (next != null) {
            next.onKeyReleased(event);
        }
    }

    @Override
    public void onLeftClick(MapClickEvent event) {
        MapWidget next = this.getNextInputWidget();
        if (next != null) {
            next.onLeftClick(event);
        }
    }

    @Override
    public void onRightClick(MapClickEvent event) {
        MapWidget next = this.getNextInputWidget();
        if (next != null) {
            next.onRightClick(event);
        }
    }

    @Override
    public void onStatusChanged(MapStatusEvent event) {
    }

    @Override
    public void onMapItemChanged() {
    }

    @Override
    public boolean onItemDrop(Player player, ItemStack item) {
        MapWidget next = this.getNextInputWidget();
        return next != null && next.onItemDrop(player, item);
    }

    @Override
    public void onBlockInteract(PlayerInteractEvent event) {
        MapWidget next = this.getNextInputWidget();
        if (next != null) {
            next.onBlockInteract(event);
        }
    }

    private MapWidget getInputWidget() {
        if (this.root == null) {
            return null;
        }
        MapWidget input = this.root.getFocusedWidget();
        if (input == null) {
            input = this.root.getActivatedWidget();
        }
        return input;
    }
}

