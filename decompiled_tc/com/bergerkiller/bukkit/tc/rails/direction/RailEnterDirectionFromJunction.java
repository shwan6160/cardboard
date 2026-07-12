/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.rails.direction;

import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.direction.RailEnterDirection;
import org.bukkit.util.Vector;

public final class RailEnterDirectionFromJunction
implements RailEnterDirection {
    private final RailJunction junction;

    RailEnterDirectionFromJunction(RailJunction junction) {
        this.junction = junction;
    }

    public RailJunction getJunction() {
        return this.junction;
    }

    @Override
    public String name() {
        return this.junction.name();
    }

    @Override
    public double motionDot(Vector motion) {
        return -this.junction.position().motDot(motion);
    }

    @Override
    public boolean match(RailState state) {
        RailPath path = state.loadRailLogic().getPath();
        RailPath.Position pos = state.position().clone();
        pos.makeRelative(state.railBlock());
        pos.invertMotion();
        path.moveRelative(pos, Double.MAX_VALUE);
        if (pos.motDot(this.junction.position()) <= 0.0) {
            return false;
        }
        return pos.distanceSquaredAtRail(state.railBlock(), this.junction.position()) < 1.0E-10;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RailEnterDirectionFromJunction) {
            return this.junction.equals(((RailEnterDirectionFromJunction)o).getJunction());
        }
        return false;
    }

    public String toString() {
        return "EnterFrom{junction=" + this.junction.name() + "}";
    }
}

