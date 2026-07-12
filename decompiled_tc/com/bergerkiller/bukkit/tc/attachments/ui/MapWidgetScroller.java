/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;

public class MapWidgetScroller
extends MapWidget {
    private final MapWidget container = new MapWidget();
    private double scrollSpeed = 0.35;
    private int scrollPadding = 0;

    public MapWidgetScroller() {
        this.container.setClipParent(true);
        this.addWidget(this.container);
        this.setRetainChildWidgets(true);
    }

    public MapWidgetScroller setScrollSpeed(double speed) {
        this.scrollSpeed = speed;
        return this;
    }

    public MapWidgetScroller setScrollPadding(int padding) {
        this.scrollPadding = padding;
        return this;
    }

    public MapWidget getContainer() {
        return this.container;
    }

    public <T extends MapWidget> T addContainerWidget(T widget) {
        if (this.getDisplay() != null) {
            this.adjustContainerSize(widget);
        }
        this.setClipParentRecursive(widget);
        this.container.addWidget(widget);
        return widget;
    }

    public int getHScrollMaximum() {
        return Math.min(0, this.container.getWidth() - this.getWidth());
    }

    public int getHScroll() {
        return -this.container.getX();
    }

    public MapWidgetScroller setHScroll(int scroll) {
        this.container.setPosition(-scroll, this.container.getY());
        return this;
    }

    public int getVScrollMaximum() {
        return Math.min(0, this.container.getHeight() - this.getHeight());
    }

    public int getVScroll() {
        return -this.container.getY();
    }

    public MapWidgetScroller setVScroll(int scroll) {
        this.container.setPosition(this.container.getX(), -scroll);
        return this;
    }

    public void scrollIntoView() {
        this.scrollIntoView(true);
    }

    public void scrollIntoView(boolean instant) {
        if (this.display == null) {
            return;
        }
        MapWidget focused = this.display.getFocusedWidget();
        if (focused == null || !this.isContained(focused)) {
            return;
        }
        int cMinX = this.container.getAbsoluteX();
        int cMinY = this.container.getAbsoluteY();
        int cMaxX = cMinX + this.container.getWidth();
        int cMaxY = cMinY + this.container.getHeight();
        int fMinX = focused.getAbsoluteX();
        int fMinY = focused.getAbsoluteY();
        int fMaxX = fMinX + focused.getWidth();
        int fMaxY = fMinY + focused.getHeight();
        if (this.container.getWidth() > this.getWidth()) {
            fMinX -= this.scrollPadding;
            fMaxX += this.scrollPadding;
        }
        if (this.container.getHeight() > this.getHeight()) {
            fMinY -= this.scrollPadding;
            fMaxY += this.scrollPadding;
        }
        fMinX = Math.max(fMinX, cMinX);
        fMinY = Math.max(fMinY, cMinY);
        fMaxX = Math.min(fMaxX, cMaxX);
        fMaxY = Math.min(fMaxY, cMaxY);
        int selfAbsX = this.getAbsoluteX();
        int selfAbsY = this.getAbsoluteY();
        int minEdgeX = selfAbsX - fMinX;
        int minEdgeY = selfAbsY - fMinY;
        int maxEdgeX = this.getWidth() - (fMaxX - selfAbsX);
        int maxEdgeY = this.getHeight() - (fMaxY - selfAbsY);
        int dx = 0;
        if (minEdgeX > 0 && maxEdgeX < 0) {
            dx = minEdgeX + maxEdgeX;
            if (dx == 1 || dx == -1) {
                dx = 0;
            }
        } else if (minEdgeX >= 0) {
            dx = minEdgeX;
        } else if (maxEdgeX < 0) {
            dx = maxEdgeX;
        }
        int dy = 0;
        if (minEdgeY > 0 && maxEdgeY < 0) {
            dy = minEdgeY + maxEdgeY;
            if (dy == 1 || dy == -1) {
                dy = 0;
            }
        } else if (minEdgeY >= 0) {
            dy = minEdgeY;
        } else if (maxEdgeY < 0) {
            dy = maxEdgeY;
        }
        if (dx != 0 || dy != 0) {
            if (!instant) {
                dx = this.smoothenScrollDelta(dx);
                dy = this.smoothenScrollDelta(dy);
            }
            this.container.setPosition(this.container.getX() + dx, this.container.getY() + dy);
            this.onScrolled();
        }
    }

    private int smoothenScrollDelta(int delta) {
        if (delta == 0) {
            return 0;
        }
        boolean negative = delta < 0;
        delta = Math.abs(delta);
        delta = Math.max((int)((double)delta * this.scrollSpeed), 1);
        if (negative) {
            delta = -delta;
        }
        return delta;
    }

    public void onTick() {
        this.scrollIntoView(false);
    }

    public void onScrolled() {
    }

    private boolean isContained(MapWidget w) {
        while (w != null && w != this.root) {
            if (w == this.container) {
                return true;
            }
            w = w.getParent();
        }
        return false;
    }

    private static void clearViewRecursive(MapWidget w) {
        w.clear();
        for (MapWidget c : w.getWidgets()) {
            MapWidgetScroller.clearViewRecursive(c);
        }
    }

    public void onAttached() {
        this.container.getWidgets().forEach(this::adjustContainerSize);
        this.scrollIntoView(true);
    }

    public void onDetached() {
        this.container.setBounds(0, 0, 0, 0);
    }

    public void onKeyPressed(MapKeyEvent event) {
        super.onKeyPressed(event);
        this.scrollIntoView(false);
    }

    private void setClipParentRecursive(MapWidget w) {
        w.setClipParent(true);
        for (MapWidget c : w.getWidgets()) {
            this.setClipParentRecursive(c);
        }
    }

    public void recalculateContainerSize() {
        int w = 0;
        int h = 0;
        for (MapWidget child : this.container.getWidgets()) {
            int cw = child.getX() + child.getWidth();
            int ch = child.getY() + child.getHeight();
            w = Math.max(w, cw);
            h = Math.max(h, ch);
        }
        this.container.setSize(w, h);
    }

    private void adjustContainerSize(MapWidget child) {
        this.adjustContainerSize(child.getX() + child.getWidth(), child.getY() + child.getHeight());
    }

    private void adjustContainerSize(int width, int height) {
        if (width > this.container.getWidth() || height > this.container.getHeight()) {
            this.container.setSize(Math.max(this.container.getWidth(), width), Math.max(this.container.getHeight(), height));
        }
    }
}

