/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.component.LibraryComponent
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.locator;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.locator.TrainLocatorEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TrainLocator
implements LibraryComponent {
    private final TrainCarts plugin;
    private final Map<Key, TrainLocatorEntry> locators = new HashMap<Key, TrainLocatorEntry>();
    private Task updateTask;

    public TrainLocator(TrainCarts plugin) {
        this.plugin = plugin;
    }

    public void enable() {
        this.updateTask = new Task((JavaPlugin)this.plugin){

            public void run() {
                int currentTime = CommonUtil.getServerTicks();
                Iterator iter = TrainLocator.this.locators.values().iterator();
                while (iter.hasNext()) {
                    TrainLocatorEntry locator = (TrainLocatorEntry)iter.next();
                    if (!TrainLocator.this.canLocate(locator.player, locator.member) || currentTime > locator.timeoutTickTime) {
                        locator.despawn();
                        iter.remove();
                        continue;
                    }
                    locator.update();
                }
                if (TrainLocator.this.locators.isEmpty()) {
                    this.stop();
                }
            }
        };
    }

    public void disable() {
        Task.stop((Task)this.updateTask);
        this.updateTask = null;
    }

    public boolean canLocate(Player player, MinecartMember<?> member) {
        return player.isValid() && !member.isUnloaded() && player.getWorld() == member.getWorld();
    }

    public boolean isLocating(Player player, MinecartGroup group) {
        for (MinecartMember<?> member : group) {
            if (!this.isLocating(player, member)) continue;
            return true;
        }
        return false;
    }

    public boolean isLocating(Player player, MinecartMember<?> member) {
        return this.locators.containsKey(new Key(player, member));
    }

    public boolean start(Player player, MinecartGroup group) {
        return this.start(player, group, -1);
    }

    public boolean start(Player player, MinecartGroup group, int timeoutTicks) {
        boolean started = false;
        for (MinecartMember<?> member : group) {
            started |= this.start(player, member, timeoutTicks);
        }
        return started;
    }

    public boolean start(Player player, MinecartMember<?> member) {
        return this.start(player, member, -1);
    }

    public boolean start(Player player, MinecartMember<?> member, int timeoutTicks) {
        if (!this.canLocate(player, member)) {
            return false;
        }
        if (this.locators.isEmpty()) {
            this.updateTask.start(1L, 1L);
        }
        TrainLocatorEntry locator = this.locators.computeIfAbsent(new Key(player, member), k -> TrainLocatorEntry.create(((Key)k).player, ((Key)k).member));
        locator.timeoutTickTime = timeoutTicks >= 0 ? CommonUtil.getServerTicks() + timeoutTicks : Integer.MAX_VALUE;
        return true;
    }

    public boolean stopAll(Player player) {
        boolean found = false;
        Iterator<TrainLocatorEntry> iter = this.locators.values().iterator();
        while (iter.hasNext()) {
            TrainLocatorEntry locator = iter.next();
            if (locator.player != player) continue;
            iter.remove();
            locator.despawn();
            found = true;
        }
        if (this.locators.isEmpty()) {
            this.updateTask.stop();
        }
        return found;
    }

    public boolean stop(Player player, MinecartGroup group) {
        boolean stopped = false;
        for (MinecartMember<?> member : group) {
            stopped |= this.stop(player, member);
        }
        return stopped;
    }

    public boolean stop(Player player, MinecartMember<?> member) {
        TrainLocatorEntry locator = this.locators.remove(new Key(player, member));
        if (locator != null) {
            locator.despawn();
            if (this.locators.isEmpty()) {
                this.updateTask.stop();
            }
            return true;
        }
        return false;
    }

    private static final class Key {
        private final Player player;
        private final MinecartMember<?> member;

        public Key(Player player, MinecartMember<?> member) {
            this.player = player;
            this.member = member;
        }

        public int hashCode() {
            return 31 * this.player.hashCode() + this.member.hashCode();
        }

        public boolean equals(Object o) {
            Key k = (Key)o;
            return this.player == k.player && this.member == k.member;
        }
    }
}

