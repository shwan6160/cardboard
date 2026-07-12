/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Task
 *  com.bergerkiller.bukkit.common.collections.BlockMap
 *  com.bergerkiller.bukkit.common.config.FileConfiguration
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.sl.API.Variables
 *  org.bukkit.ChatColor
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 *  org.bukkit.entity.Minecart
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.Task;
import com.bergerkiller.bukkit.common.collections.BlockMap;
import com.bergerkiller.bukkit.common.config.FileConfiguration;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.sl.API.Variables;
import com.bergerkiller.bukkit.tc.SignActionHeader;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.utils.TimeDurationFormat;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ArrivalSigns {
    private static HashMap<String, TimeSign> timerSigns = new HashMap();
    private static BlockMap<TimeCalculation> timeCalculations = new BlockMap();
    private static TimeDurationFormat timeFormat = new TimeDurationFormat("HH:mm:ss");
    private static Task updateTask;

    public static TimeSign getTimer(String name) {
        return timerSigns.computeIfAbsent(name, new_timesign_name -> new TimeSign((String)new_timesign_name));
    }

    public static boolean isTrigger(Sign sign) {
        SignActionHeader header = SignActionHeader.parseFromSign(sign);
        return header.isValid() && Util.getCleanLine(sign, 1).equalsIgnoreCase("trigger");
    }

    public static void trigger(Sign sign, MinecartMember<?> mm) {
        if (!TrainCarts.plugin.isSignLinkEnabled()) {
            return;
        }
        String name = Util.getCleanLine(sign, 2);
        String duration = Util.getCleanLine(sign, 3);
        if (name.isEmpty()) {
            return;
        }
        if (mm != null) {
            Variables.get((String)(name + 'N')).set(mm.getGroup().getProperties().getDisplayName());
            if (mm.getProperties().hasDestination()) {
                Variables.get((String)(name + 'D')).set(mm.getProperties().getDestination());
            } else {
                Variables.get((String)(name + 'D')).set("Unknown");
            }
            double speed = MathUtil.round((double)mm.getRealSpeed(), (int)2);
            speed = Math.min(speed, mm.getGroup().getProperties().getSpeedLimit());
            Variables.get((String)(name + 'V')).set(Double.toString(speed));
        }
        TimeSign t = ArrivalSigns.getTimer(name);
        t.duration = ParseUtil.parseTime((String)duration);
        if (t.duration == 0L) {
            ArrivalSigns.timeCalcStart(sign.getBlock(), mm);
        } else {
            t.trigger();
            t.update();
        }
    }

    public static void setTimeDurationFormat(String format) {
        try {
            timeFormat = new TimeDurationFormat(format);
        }
        catch (IllegalArgumentException ex) {
            TrainCarts.plugin.log(Level.WARNING, "Time duration format is invalid: " + format);
        }
    }

    public static void updateAll() {
        for (TimeSign t : timerSigns.values()) {
            if (t.update()) continue;
            return;
        }
    }

    public static void init(String filename) {
        FileConfiguration config = new FileConfiguration(filename);
        config.load();
        for (String key : config.getKeys()) {
            String dur = (String)config.get(key, String.class, null);
            if (dur == null) continue;
            TimeSign t = ArrivalSigns.getTimer(key);
            t.duration = ParseUtil.parseTime((String)dur);
            t.startTime = System.currentTimeMillis();
        }
    }

    public static void save(String filename) {
        FileConfiguration config = new FileConfiguration(filename);
        for (TimeSign sign : timerSigns.values()) {
            config.set(sign.name, (Object)sign.getDuration());
        }
        config.save();
    }

    public static void deinit() {
        timerSigns.clear();
        timerSigns = null;
        timeCalculations.clear();
        timeCalculations = null;
        if (updateTask != null && updateTask.isRunning()) {
            updateTask.stop();
        }
        updateTask = null;
    }

    public static void timeCalcStart(Block signblock, MinecartMember<?> member) {
        TimeCalculation calc = new TimeCalculation();
        calc.startTime = System.currentTimeMillis();
        calc.signblock = signblock;
        calc.member = member;
        for (Player player : calc.signblock.getWorld().getPlayers()) {
            if (!player.hasPermission("train.build.trigger")) continue;
            if (member == null) {
                player.sendMessage(ChatColor.YELLOW + "[Train Carts] Remove the power source to stop recording");
                continue;
            }
            player.sendMessage(ChatColor.YELLOW + "[Train Carts] Stop or destroy the minecart to stop recording");
        }
        timeCalculations.put(calc.signblock, (Object)calc);
        if (updateTask == null) {
            updateTask = new Task((JavaPlugin)TrainCarts.plugin){

                public void run() {
                    if (timeCalculations.isEmpty()) {
                        this.stop();
                        updateTask = null;
                    }
                    for (TimeCalculation calc : timeCalculations.values()) {
                        if (calc.member == null || !calc.member.isUnloaded() && !((Minecart)((CommonMinecart)calc.member.getEntity()).getEntity()).isDead() && ((CommonMinecart)calc.member.getEntity()).isMoving()) continue;
                        calc.setTime();
                        timeCalculations.remove(calc.signblock);
                        return;
                    }
                }
            }.start(0L, 1L);
        }
    }

    public static void timeCalcStop(Block signblock) {
        TimeCalculation calc = (TimeCalculation)timeCalculations.get(signblock);
        if (calc != null && calc.member == null) {
            calc.setTime();
            timeCalculations.remove(signblock);
        }
    }

    public static class TimeSign {
        public long startTime = -1L;
        public long duration;
        private String name;

        public TimeSign(String name) {
            this.name = name;
        }

        public void trigger() {
            this.startTime = System.currentTimeMillis();
        }

        public String getName() {
            return this.name;
        }

        public String getDuration() {
            long elapsed = System.currentTimeMillis() - this.startTime;
            long remaining = this.duration - elapsed;
            if (remaining < 0L) {
                remaining = 0L;
            }
            return timeFormat.format(remaining);
        }

        public boolean update() {
            if (!TrainCarts.plugin.isSignLinkEnabled()) {
                return false;
            }
            String dur = this.getDuration();
            Variables.get((String)this.name).set(dur);
            Variables.get((String)(this.name + 'T')).set(dur);
            if (dur.equals("00:00:00")) {
                timerSigns.remove(this.name);
                return false;
            }
            return true;
        }
    }

    private static class TimeCalculation {
        public long startTime;
        public Block signblock;
        public MinecartMember<?> member = null;

        private TimeCalculation() {
        }

        public void setTime() {
            long duration = System.currentTimeMillis() - this.startTime;
            if (((Boolean)MaterialUtil.ISSIGN.get(this.signblock)).booleanValue()) {
                Sign sign = (Sign)this.signblock.getState();
                String dur = timeFormat.format(duration);
                sign.setLine(3, dur);
                sign.update(true);
                for (Player player : sign.getWorld().getPlayers()) {
                    if (!player.hasPermission("train.build.trigger")) continue;
                    player.sendMessage(ChatColor.YELLOW + "[Train Carts] Trigger time of '" + sign.getLine(2) + "' set to " + dur);
                }
            }
        }
    }
}

