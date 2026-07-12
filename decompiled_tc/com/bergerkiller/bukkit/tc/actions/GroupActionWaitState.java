/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.tc.actions.GroupActionWaitForever;

public class GroupActionWaitState
extends GroupActionWaitForever {
    private boolean stop = false;

    @Override
    public boolean update() {
        return this.stop || super.update();
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public boolean isMovementSuppressed() {
        return false;
    }
}

