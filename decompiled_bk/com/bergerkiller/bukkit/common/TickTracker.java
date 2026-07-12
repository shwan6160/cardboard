/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.utils.CommonUtil;

public class TickTracker {
    private int _tick = Integer.MIN_VALUE;
    private Runnable _runnable = null;

    public void setRunnable(Runnable runnable) {
        this._runnable = runnable;
    }

    public boolean update() {
        if (!this.check()) {
            return false;
        }
        if (this._runnable != null) {
            this._runnable.run();
        }
        return true;
    }

    public boolean check() {
        int current_tick = CommonUtil.getServerTicks();
        if (current_tick != this._tick) {
            this._tick = current_tick;
            return true;
        }
        return false;
    }
}

