/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.PlayerUtil
 *  org.bukkit.Color
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.debug.particles;

import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.tc.debug.particles.DebugParticles;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

class DebugParticlesLegacy
extends DebugParticles {
    private static final double PARTICLE_SPACING = 0.3;
    private static final int PARTICLE_ITERATIONS = 20;
    private static final int PARTICLE_INTERVAL = 4;
    private final List<Element> elements = new ArrayList<Element>();

    public DebugParticlesLegacy(Player player) {
        super(player);
    }

    @Override
    public void line(Color color, double x1, double y1, double z1, double x2, double y2, double z2) {
        double dist = MathUtil.distance((double)x1, (double)y1, (double)z1, (double)x2, (double)y2, (double)z2);
        if (dist >= 1.0E-8) {
            int n = MathUtil.ceil((double)(dist / 0.3));
            this.elements.add(new Line(color, x1, y1, z1, x2, y2, z2, n));
            this.startUpdating();
        }
    }

    @Override
    public void point(Color color, double x, double y, double z) {
        this.elements.add(new Point(color, x, y, z));
        this.startUpdating();
    }

    @Override
    protected boolean update() {
        this.elements.removeIf(l -> l.update(this.player));
        return this.elements.isEmpty();
    }

    private static class Line
    extends Element {
        public final Color color;
        public final double x;
        public final double y;
        public final double z;
        public final double dx;
        public final double dy;
        public final double dz;
        public final int count;

        public Line(Color color, double x1, double y1, double z1, double x2, double y2, double z2, int count) {
            this.color = color;
            this.x = x1;
            this.y = y1;
            this.z = z1;
            this.dx = x2 - x1;
            this.dy = y2 - y1;
            this.dz = z2 - z1;
            this.count = count;
        }

        @Override
        public void spawn(Player viewer) {
            Vector position = new Vector();
            int n = this.count;
            for (int i = 0; i < n; ++i) {
                double t = (double)i / (double)(n - 1);
                position.setX(this.x + this.dx * t);
                position.setY(this.y + this.dy * t);
                position.setZ(this.z + this.dz * t);
                PlayerUtil.spawnDustParticles((Player)viewer, (Vector)position, (Color)this.color);
            }
        }
    }

    private static class Point
    extends Element {
        public final Color color;
        public final Vector pos;

        public Point(Color color, double x, double y, double z) {
            this.color = color;
            this.pos = new Vector(x, y, z);
        }

        @Override
        public void spawn(Player viewer) {
            PlayerUtil.spawnDustParticles((Player)viewer, (Vector)this.pos, (Color)this.color);
        }
    }

    private static abstract class Element {
        public int age = 0;
        public int skip = 4;

        public abstract void spawn(Player var1);

        public boolean update(Player viewer) {
            if (++this.skip < 4) {
                return false;
            }
            this.skip = 0;
            this.spawn(viewer);
            return ++this.age >= 20;
        }
    }
}

