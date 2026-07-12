/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.protocol.PlayerGameInfo
 *  org.bukkit.Color
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.debug.particles;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.protocol.PlayerGameInfo;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.debug.particles.DebugParticlesDisplay;
import com.bergerkiller.bukkit.tc.debug.particles.DebugParticlesLegacy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public abstract class DebugParticles {
    private static final Map<Player, List<DebugParticles>> byPlayer = new HashMap<Player, List<DebugParticles>>();
    protected final Player player;
    private boolean started = false;

    public static DebugParticles of(Player player) {
        List<DebugParticles> existing = byPlayer.get(player);
        if (existing != null) {
            return existing.get(0);
        }
        DebugParticles result = DebugParticles.createFor(player);
        result.startUpdating();
        return result;
    }

    private static DebugParticles createFor(Player player) {
        if (CommonCapabilities.HAS_DISPLAY_ENTITY && PlayerGameInfo.of((Player)player).evaluateVersion(">=", "1.20")) {
            return new DebugParticlesDisplay(player);
        }
        return new DebugParticlesLegacy(player);
    }

    protected DebugParticles(Player player) {
        this.player = player;
    }

    public void cube(Color color, double x1, double y1, double z1, double x2, double y2, double z2) {
        this.face(color, x1, y1, z1, x2, y1, z2);
        this.face(color, x1, y2, z1, x2, y2, z2);
        this.line(color, x1, y1, z1, x1, y2, z1);
        this.line(color, x2, y1, z1, x2, y2, z1);
        this.line(color, x1, y1, z2, x1, y2, z2);
        this.line(color, x2, y1, z2, x2, y2, z2);
    }

    public void face(Color color, double x1, double y1, double z1, double x2, double y2, double z2) {
        this.line(color, x1, y1, z1, x2, y1, z1);
        this.line(color, x1, y1, z1, x1, y2, z1);
        this.line(color, x1, y1, z1, x1, y1, z2);
        this.line(color, x1, y2, z2, x2, y2, z2);
        this.line(color, x2, y1, z2, x2, y2, z2);
        this.line(color, x2, y2, z1, x2, y2, z2);
    }

    public final void line(Color color, Vector p1, Vector p2) {
        this.line(color, p1.getX(), p1.getY(), p1.getZ(), p2.getX(), p2.getY(), p2.getZ());
    }

    public abstract void line(Color var1, double var2, double var4, double var6, double var8, double var10, double var12);

    public final void point(Color color, Vector pos) {
        this.point(color, pos.getX(), pos.getY(), pos.getZ());
    }

    public abstract void point(Color var1, double var2, double var4, double var6);

    protected abstract boolean update();

    private boolean doUpdate() {
        if (this.update()) {
            this.started = false;
            return true;
        }
        return false;
    }

    protected void startUpdating() {
        if (this.started) {
            return;
        }
        if (byPlayer.isEmpty()) {
            byPlayer.put(this.player, Collections.singletonList(this));
            new Task((JavaPlugin)TrainCarts.plugin){

                public void run() {
                    byPlayer.values().removeIf(particles -> {
                        if (particles.size() == 1) {
                            return ((DebugParticles)particles.get(0)).doUpdate();
                        }
                        particles.removeIf(rec$ -> ((DebugParticles)rec$).doUpdate());
                        return particles.isEmpty();
                    });
                    if (byPlayer.isEmpty()) {
                        this.stop();
                    }
                }
            }.start(1L, 1L);
        } else {
            byPlayer.compute(this.player, (p, current) -> {
                if (current == null) {
                    return Collections.singletonList(this);
                }
                if (current.size() == 1) {
                    ArrayList<DebugParticles> multi = new ArrayList<DebugParticles>((Collection<DebugParticles>)current);
                    multi.add(this);
                    return multi;
                }
                current.add(this);
                return current;
            });
        }
        this.started = true;
    }
}

