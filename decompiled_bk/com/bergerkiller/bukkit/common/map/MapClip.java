/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map;

public class MapClip {
    public int dirty_x1 = 0;
    public int dirty_y1 = 0;
    public int dirty_x2 = 0;
    public int dirty_y2 = 0;
    private boolean dirty = false;
    private boolean everything = false;

    public final int getX() {
        return this.dirty_x1;
    }

    public final int getY() {
        return this.dirty_y1;
    }

    public final int getWidth() {
        return this.dirty_x2 - this.dirty_x1 + 1;
    }

    public final int getHeight() {
        return this.dirty_y2 - this.dirty_y1 + 1;
    }

    public final boolean isDirty() {
        return this.dirty;
    }

    public final boolean isEverythingDirty() {
        return this.everything;
    }

    public final boolean isFullyEnclosedBy(int x, int y, int w, int h) {
        return !this.dirty || !this.everything && x <= this.dirty_x1 && y <= this.dirty_y1 && x + w >= this.dirty_x2 && y + h >= this.dirty_y2;
    }

    public final void markDirty(int x, int y) {
        if (!this.dirty) {
            this.dirty = true;
            this.dirty_x1 = this.dirty_x2 = x;
            this.dirty_y1 = this.dirty_y2 = y;
        } else if (!this.everything) {
            if (x < this.dirty_x1) {
                this.dirty_x1 = x;
            } else if (x > this.dirty_x2) {
                this.dirty_x2 = x;
            }
            if (y < this.dirty_y1) {
                this.dirty_y1 = y;
            } else if (y > this.dirty_y2) {
                this.dirty_y2 = y;
            }
        }
    }

    public final void markDirty(int x, int y, int w, int h) {
        if (w == 0 || h == 0) {
            return;
        }
        int x2 = x + w - 1;
        int y2 = y + h - 1;
        if (!this.dirty) {
            this.dirty = true;
            this.dirty_x1 = x;
            this.dirty_x2 = x2;
            this.dirty_y1 = y;
            this.dirty_y2 = y2;
        } else if (!this.everything) {
            if (x < this.dirty_x1) {
                this.dirty_x1 = x;
            }
            if (y < this.dirty_y1) {
                this.dirty_y1 = y;
            }
            if (x2 > this.dirty_x2) {
                this.dirty_x2 = x2;
            }
            if (y2 > this.dirty_y2) {
                this.dirty_y2 = y2;
            }
        }
    }

    public final void markDirty(MapClip clip) {
        if (!this.dirty) {
            this.dirty = true;
            this.dirty_x1 = clip.dirty_x1;
            this.dirty_x2 = clip.dirty_x2;
            this.dirty_y1 = clip.dirty_y1;
            this.dirty_y2 = clip.dirty_y2;
        } else if (!this.everything) {
            if (clip.dirty_x1 < this.dirty_x1) {
                this.dirty_x1 = clip.dirty_x1;
            }
            if (clip.dirty_y1 < this.dirty_y1) {
                this.dirty_y1 = clip.dirty_y1;
            }
            if (clip.dirty_x2 > this.dirty_x2) {
                this.dirty_x2 = clip.dirty_x2;
            }
            if (clip.dirty_y2 > this.dirty_y2) {
                this.dirty_y2 = clip.dirty_y2;
            }
        }
    }

    public final void markEverythingDirty() {
        this.dirty = true;
        this.everything = true;
    }

    public final void clearDirty() {
        this.dirty = false;
        this.everything = false;
    }

    public final MapClip getArea(int x, int y, int width, int height) {
        MapClip result = new MapClip();
        if (!this.dirty) {
            result.clearDirty();
        } else if (this.everything) {
            result.markEverythingDirty();
        } else if (this.dirty_x1 <= x && this.dirty_y1 <= y && this.dirty_x2 >= x + width && this.dirty_y2 >= y + height) {
            result.markEverythingDirty();
        } else if (this.dirty_x1 >= x + width || this.dirty_y1 >= y + height) {
            result.clearDirty();
        } else if (this.dirty_x2 < x || this.dirty_y2 < y) {
            result.clearDirty();
        } else {
            result.dirty = true;
            result.everything = false;
            result.dirty_x1 = Math.max(this.dirty_x1, x) - x;
            result.dirty_y1 = Math.max(this.dirty_y1, y) - y;
            result.dirty_x2 = Math.min(this.dirty_x2, x + width - 1) - x;
            result.dirty_y2 = Math.min(this.dirty_y2, y + height - 1) - y;
        }
        return result;
    }
}

