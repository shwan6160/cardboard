/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect;

import com.bergerkiller.bukkit.tc.attachments.control.effect.EffectLoop;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class EffectLoopGroup
implements EffectLoop {
    private final List<EffectLoop> originalGroup;
    private final List<EffectLoop> group;

    public EffectLoopGroup(Collection<EffectLoop> group) {
        this.originalGroup = new ArrayList<EffectLoop>(group);
        this.group = new ArrayList<EffectLoop>(this.originalGroup);
    }

    @Override
    public boolean advance(EffectLoop.Time dt, EffectLoop.Time duration, boolean loop) {
        this.group.removeIf(e -> !e.advance(dt, duration, loop));
        return !this.group.isEmpty();
    }

    @Override
    public void resetToBeginning() {
        this.group.clear();
        this.originalGroup.forEach(EffectLoop::resetToBeginning);
        this.group.addAll(this.originalGroup);
    }
}

