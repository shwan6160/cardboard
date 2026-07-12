/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.portals.PortalDestination;
import com.bergerkiller.bukkit.tc.portals.TCPortalManager;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.BlockTimeoutMap;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.bergerkiller.bukkit.tc.utils.TrackIterator;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class SignActionTeleport
extends SignAction {
    private BlockTimeoutMap teleportTimes = new BlockTimeoutMap();

    @Override
    public boolean canSupportRC() {
        return true;
    }

    @Override
    public boolean verify(SignActionEvent info) {
        return this.matchMyWorlds(info) || super.verify(info);
    }

    @Override
    public boolean match(SignActionEvent info) {
        return this.matchMyWorlds(info) || info.isType("teleport");
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void execute(SignActionEvent info) {
        BlockFace[] railDirections;
        if (!info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) || !info.isPowered()) {
            return;
        }
        MinecartGroup group = null;
        if (!info.isRCSign()) {
            if (!info.hasGroup()) {
                return;
            }
            group = info.getGroup();
        } else {
            Collection<MinecartGroup> groups = info.getRCTrainGroups();
            if (groups.isEmpty()) {
                return;
            }
            group = groups.iterator().next();
        }
        String destName = TCPortalManager.getPreferredDestination(info);
        if (destName == null) {
            return;
        }
        PortalDestination dest = TCPortalManager.getPortalDestination(group.getWorld(), destName, group);
        if (dest == null || dest.getRailsBlock() == null) return;
        if (info.hasRails() && this.teleportTimes.isMarked(info.getRails(), 2000L)) {
            return;
        }
        this.teleportTimes.mark(dest.getRailsBlock());
        ArrayList<BlockFace> possibleDirs = new ArrayList<BlockFace>();
        ArrayList<TrackIterator> possibleIters = new ArrayList<TrackIterator>();
        for (BlockFace dir : railDirections = RailType.getType(dest.getRailsBlock()).getPossibleDirections(dest.getRailsBlock())) {
            if (dest.hasDirections() && !LogicUtil.contains((Object)dir, (Object[])dest.getDirections())) continue;
            possibleDirs.add(dir);
            possibleIters.add(new TrackIterator(dest.getRailsBlock(), dir));
        }
        BlockFace spawnDirection = null;
        if (possibleIters.isEmpty()) {
            if (railDirections.length > 0) {
                spawnDirection = railDirections[0];
            } else {
                if (!dest.hasDirections()) return;
                spawnDirection = dest.getDirections()[0];
            }
        } else {
            spawnDirection = (BlockFace)possibleDirs.get(0);
            if (possibleDirs.size() > 1) {
                for (int n = 0; n < 30; ++n) {
                    int num_succ = 0;
                    for (int i = 0; i < possibleIters.size(); ++i) {
                        TrackIterator iter = (TrackIterator)possibleIters.get(i);
                        if (!iter.hasNext()) continue;
                        iter.next();
                        ++num_succ;
                        spawnDirection = (BlockFace)possibleDirs.get(i);
                    }
                    if (num_succ <= 1) break;
                }
            }
        }
        if (dest.getRailsBlock().getWorld() == group.getWorld()) {
            group.teleportAndGo(dest.getRailsBlock(), spawnDirection);
            return;
        } else {
            final MinecartGroup targetGroup = group;
            final Block destRail = dest.getRailsBlock();
            final BlockFace destDirection = spawnDirection;
            CommonUtil.nextTick((Runnable)new Runnable(){
                final /* synthetic */ SignActionTeleport this$0;
                {
                    this.this$0 = this$0;
                }

                @Override
                public void run() {
                    targetGroup.teleportAndGo(destRail, destDirection);
                }
            });
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        if (this.matchMyWorlds(event) && !TCPortalManager.isAvailable("My_Worlds")) {
            event.getPlayer().sendMessage(ChatColor.RED + "MyWorlds" + ChatColor.YELLOW + " is not enabled on this server. Teleporter signs will not function as a result.");
            return false;
        }
        return SignBuildOptions.create().setPermission(Permission.BUILD_TELEPORTER).setName("train teleporter").setDescription("teleport trains large distances to another teleporter sign").setTraincartsWIKIHelp("TrainCarts/Signs/Teleporter").setShowBuildMessage(event.hasRails()).handle(event);
    }

    private boolean matchMyWorlds(SignActionEvent info) {
        return info.getHeader().getModeText().equals("portal");
    }
}

