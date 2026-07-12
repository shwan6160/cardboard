/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.pathfinding;

import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.pathfinding.PathNavigateEventBaseImpl;
import org.bukkit.World;
import org.bukkit.block.Block;

public interface PathNavigateEvent {
    public static PathNavigateEvent createNew() {
        return new PathNavigateEventBaseImpl();
    }

    public void resetToInitialState(RailState var1, RailPath var2, double var3);

    public double currentDistance();

    public boolean isNavigationAborted();

    public void abortNavigation();

    default public void setBlocked() {
        this.abortNavigation();
    }

    default public boolean isBlocked() {
        return this.isNavigationAborted();
    }

    public RailState railState();

    public RailPath railPath();

    default public RailPiece railPiece() {
        return this.railState().railPiece();
    }

    default public Block railBlock() {
        return this.railState().railBlock();
    }

    default public World railWorld() {
        return this.railState().railWorld();
    }

    public RailPath.Position getSwitchedPosition();

    public boolean hasSwitchedPosition();

    public void setSwitchedPosition(RailPath.Position var1);

    default public void setSwitchedJunction(RailJunction junction) {
        this.setSwitchedPosition(junction.position());
    }
}

