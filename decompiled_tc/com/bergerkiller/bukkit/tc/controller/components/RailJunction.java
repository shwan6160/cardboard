/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import java.util.Optional;
import org.bukkit.util.Vector;

public final class RailJunction {
    private final String _name;
    private final RailPath.Position _position;

    public RailJunction(String name, RailPath.Position position) {
        this._name = name;
        this._position = position;
    }

    public String name() {
        return this._name;
    }

    public RailPath.Position position() {
        return this._position;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof RailJunction) {
            RailJunction other = (RailJunction)o;
            return this.name().equals(other.name()) && this.position().equals(other.position());
        }
        return false;
    }

    public String toString() {
        return "{" + this._name + ": " + this._position.toString() + "}";
    }

    public static Optional<RailJunction> findBest(Iterable<RailJunction> junctions, Vector direction) {
        double bestDot = 0.0;
        RailJunction best = null;
        for (RailJunction junction : junctions) {
            double dot = junction.position().motDot(direction);
            if (!(dot > bestDot)) continue;
            bestDot = dot;
            best = junction;
        }
        return Optional.ofNullable(best);
    }
}

