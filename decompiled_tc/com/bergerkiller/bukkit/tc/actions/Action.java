/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.ToggledState
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.common.ToggledState;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatus;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatusProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Action
implements TrainStatusProvider,
TrainCarts.Provider {
    private final ToggledState started = new ToggledState();
    private int _timeTicks = 0;
    private int _subTicks = 1;
    private long _startTimeMillis = 0L;
    private final HashSet<String> tags = new HashSet();

    public boolean doTick() {
        if (this.started.set()) {
            this._startTimeMillis = System.currentTimeMillis();
            this.start();
        }
        boolean result = this.update();
        if (this.isFullTick()) {
            this._subTicks = 1;
            ++this._timeTicks;
        } else {
            ++this._subTicks;
        }
        return result;
    }

    public MinecartGroup getGroup() {
        return null;
    }

    public boolean hasActionStarted() {
        return this.started.get();
    }

    public final int elapsedTicks() {
        return this._timeTicks;
    }

    public final boolean isFullTick() {
        MinecartGroup group = this.getGroup();
        return group == null || this._subTicks >= group.getUpdateStepCount();
    }

    public final long elapsedTimeMillis() {
        return System.currentTimeMillis() - this._startTimeMillis;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(this.tags);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public boolean hasTag(String tag) {
        return this.tags.contains(tag);
    }

    public boolean update() {
        return true;
    }

    public void cancel() {
    }

    public void bind() {
    }

    public void start() {
    }

    @Override
    public List<TrainStatus> getStatusInfo() {
        return Collections.emptyList();
    }

    public static void loadElapsedTime(Action action, int elapsedTicks, long elapsedMillis) {
        if (elapsedTicks > 0) {
            action.started.set();
            action._timeTicks = elapsedTicks;
            action._startTimeMillis = System.currentTimeMillis() - elapsedMillis;
        } else {
            action.started.clear();
            action._timeTicks = 0;
            action._startTimeMillis = 0L;
        }
        action._subTicks = 1;
    }
}

