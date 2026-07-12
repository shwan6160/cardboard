/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.nbt.CommonTagCompound
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.debug.types;

import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.debug.DebugTool;
import com.bergerkiller.bukkit.tc.debug.DebugToolUtil;
import com.bergerkiller.bukkit.tc.debug.types.DebugToolTrackWalkerType;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.text.NumberFormat;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class DebugToolTypeTrackDistance
extends DebugToolTrackWalkerType {
    @Override
    public String getIdentifier() {
        return "TrackDistance";
    }

    @Override
    public String getTitle() {
        return "Track distance calculator";
    }

    @Override
    public String getDescription() {
        return "Calculates and displays the track distance between two points";
    }

    @Override
    public String getInstructions() {
        return "Left-click on one point and right-click another to display the track distance between the two points";
    }

    @Override
    public boolean handlesLeftClick() {
        return true;
    }

    @Override
    public void onBlockInteract(TrainCarts plugin, Player player, TrackWalkingPoint walker, CommonItemStack item, boolean isRightClick) {
        TrackWalkingPoint measure;
        block14: {
            RailState goal;
            RailState start;
            item = item.clone();
            if (isRightClick) {
                DebugToolTypeTrackDistance.saveRailState(item, "pos2", walker.state);
                start = DebugToolTypeTrackDistance.loadRailState(player, item, "pos1");
                goal = walker.state;
            } else {
                DebugToolTypeTrackDistance.saveRailState(item, "pos1", walker.state);
                start = walker.state;
                goal = DebugToolTypeTrackDistance.loadRailState(player, item, "pos2");
            }
            DebugTool.updateToolItem(player, item);
            if (start == null || goal == null || player.isSneaking()) {
                DebugToolUtil.showParticle(walker.state.positionLocation());
                if (isRightClick) {
                    player.sendMessage(ChatColor.YELLOW + "End" + ChatColor.GREEN + " position set");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Start" + ChatColor.GREEN + " position set");
                }
                return;
            }
            if (start.railWorld() != goal.railWorld()) {
                player.sendMessage(ChatColor.RED + "The two positions are on different worlds!");
                return;
            }
            double distance = start.position().distance(goal.position());
            if (distance > 2000.0) {
                player.sendMessage(ChatColor.RED + "Distance between the two positions is too large!");
                return;
            }
            measure = new TrackWalkingPoint(start);
            double PARTICLE_STEP = 0.5;
            int cycleCtr = 10000;
            double bestRemaining = Double.MAX_VALUE;
            double bestTotal = 0.0;
            boolean foundGoalRailBlock = false;
            while (true) {
                double d;
                double remaining = measure.state.position().distance(goal.position());
                if (!(d > 1.0E-4)) break block14;
                if (!measure.move(Math.min(0.5, remaining))) {
                    DebugToolUtil.showEndOfTheRail(player, measure, 0.0);
                    return;
                }
                if (measure.movedTotal > 2000.0 || --cycleCtr <= 0) {
                    player.sendMessage(ChatColor.RED + "Distance between the two positions is too large!");
                    return;
                }
                DebugToolUtil.showParticle(measure.state.positionLocation());
                if (measure.state.railPiece().equals(goal.railPiece())) {
                    if (remaining < bestRemaining) {
                        bestRemaining = remaining;
                        bestTotal = measure.movedTotal;
                    }
                    foundGoalRailBlock = true;
                    continue;
                }
                if (foundGoalRailBlock) break;
            }
            measure.movedTotal = bestTotal;
        }
        double totalDistance = measure.movedTotal;
        NumberFormat df = NumberFormat.getNumberInstance(Locale.ENGLISH);
        df.setGroupingUsed(false);
        df.setMinimumFractionDigits(2);
        if (isRightClick) {
            player.sendMessage(ChatColor.GREEN + "Distance from start to " + ChatColor.YELLOW + "end" + ChatColor.GREEN + " is " + ChatColor.WHITE + df.format(totalDistance) + ChatColor.GREEN + " blocks");
        } else {
            player.sendMessage(ChatColor.GREEN + "Distance from " + ChatColor.YELLOW + "start" + ChatColor.GREEN + " to end is " + ChatColor.WHITE + df.format(totalDistance) + ChatColor.GREEN + " blocks");
        }
    }

    private static void saveRailState(CommonItemStack item, String prefix, RailState state) {
        state.position().assertAbsolute();
        item.updateCustomData(tag -> {
            CommonTagCompound meta = tag.createCompound((Object)prefix);
            meta.putValue("world", (Object)state.railWorld().getName());
            meta.putValue("posX", (Object)state.position().posX);
            meta.putValue("posY", (Object)state.position().posY);
            meta.putValue("posZ", (Object)state.position().posZ);
            meta.putValue("motX", (Object)state.position().motX);
            meta.putValue("motY", (Object)state.position().motY);
            meta.putValue("motZ", (Object)state.position().motZ);
        });
    }

    private static RailState loadRailState(Player player, CommonItemStack item, String prefix) {
        CommonTagCompound meta = (CommonTagCompound)item.getCustomData().get((Object)prefix, CommonTagCompound.class);
        if (meta == null) {
            return null;
        }
        String worldName = (String)meta.getValue("world", (Object)"");
        World world = Bukkit.getWorld((String)worldName);
        if (world == null) {
            player.sendMessage("Other position is on a world that is not loaded: " + worldName);
            return null;
        }
        RailPath.Position position = new RailPath.Position();
        position.relative = false;
        position.posX = (Double)meta.getValue("posX", (Object)0.0);
        position.posY = (Double)meta.getValue("posY", (Object)0.0);
        position.posZ = (Double)meta.getValue("posZ", (Object)0.0);
        position.motX = (Double)meta.getValue("motX", (Object)0.0);
        position.motY = (Double)meta.getValue("motY", (Object)0.0);
        position.motZ = (Double)meta.getValue("motZ", (Object)0.0);
        RailState state = new RailState();
        state.setRailPiece(RailPiece.createWorldPlaceholder(world));
        state.setPosition(position);
        if (!RailType.loadRailInformation(state)) {
            player.sendMessage("Rails at the other position doesn't exist anymore!");
            return null;
        }
        return state;
    }
}

