/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.map;

import com.bergerkiller.bukkit.common.map.binding.ItemFrameInfo;

class ItemFrameUpdateList {
    private ItemFrameInfo.UpdateEntry first = null;
    private ItemFrameInfo.UpdateEntry last = null;

    ItemFrameUpdateList() {
    }

    public ItemFrameInfo.UpdateEntry first() {
        return this.first;
    }

    public void add(ItemFrameInfo.UpdateEntry entry) {
        entry.added = true;
        entry.prioritized = true;
        if (this.first == null) {
            this.first = this.last = entry;
        } else {
            this.first.prev = entry;
            entry.next = this.first;
            this.first = entry;
        }
    }

    public void prioritize(ItemFrameInfo.UpdateEntry entry) {
        if (!entry.prioritized && entry.added) {
            entry.prioritized = true;
            if (entry != this.first) {
                if (entry == this.last) {
                    this.last = entry.prev;
                    this.last.next = null;
                } else {
                    ItemFrameUpdateList.detachMiddle(entry);
                }
                this.first.prev = entry;
                entry.next = this.first;
                entry.prev = null;
                this.first = entry;
            }
        }
    }

    public void moveRangeToEnd(ItemFrameInfo.UpdateEntry chainStart, ItemFrameInfo.UpdateEntry chainEnd) {
        if (chainEnd != this.last) {
            if (chainStart == this.first) {
                this.first = chainEnd.next;
                this.first.prev = null;
            } else {
                chainStart.prev.next = chainEnd.next;
                chainEnd.next.prev = chainStart.prev;
            }
            this.last.next = chainStart;
            chainStart.prev = this.last;
            this.last = chainEnd;
            chainEnd.next = null;
        }
    }

    public void remove(ItemFrameInfo.UpdateEntry entry) {
        if (entry.added) {
            entry.added = false;
            if (entry == this.first) {
                this.first = entry.next;
                if (this.first != null) {
                    this.first.prev = null;
                    entry.next = null;
                } else {
                    this.last = null;
                }
            } else if (entry == this.last) {
                this.last = entry.prev;
                if (this.last != null) {
                    this.last.next = null;
                    entry.prev = null;
                }
            } else {
                ItemFrameUpdateList.detachMiddle(entry);
            }
        }
    }

    private static void detachMiddle(ItemFrameInfo.UpdateEntry entry) {
        entry.prev.next = entry.next;
        entry.next.prev = entry.prev;
    }
}

