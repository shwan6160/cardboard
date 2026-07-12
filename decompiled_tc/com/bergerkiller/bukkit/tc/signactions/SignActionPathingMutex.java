/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathPredictEvent;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneCache;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZonePath;
import com.bergerkiller.bukkit.tc.signactions.mutex.MutexZoneSlotType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;

public class SignActionPathingMutex
extends TrainCartsSignAction {
    public SignActionPathingMutex() {
        super("pmutex", "spmutex", "psmutex", "pathmutex", "pathingmutex");
    }

    @Override
    public void predictPathFinding(SignActionEvent info, PathPredictEvent prediction) {
        if (!info.isEnterActivated() || !info.isPowered()) {
            return;
        }
        MutexZonePath path = MutexZoneCache.getOrCreatePathingMutex(info.getTrackedSign(), prediction.group(), prediction.railState().positionOfflineBlock().getPosition(), opt -> this.loadOptions(info, (MutexZonePath.OptionsBuilder)opt));
        path.onUsed(prediction.group());
        prediction.trackBlock((p, d) -> {
            p.railPath().forAllBlocks(p.railPiece().blockPosition(), path::addBlock);
            return true;
        }, path, path.getMaxDistance());
    }

    private MutexZonePath.OptionsBuilder loadOptions(SignActionEvent info, MutexZonePath.OptionsBuilder opt) {
        String name;
        opt.type(info.isType("spmutex", "psmutex") ? MutexZoneSlotType.SMART : MutexZoneSlotType.NORMAL);
        String options = info.getLine(1);
        int firstSpace = options.indexOf(32);
        if (firstSpace != -1) {
            boolean hasDistance = false;
            for (String part : options.substring(firstSpace + 1).split(" ")) {
                if (part.isEmpty()) continue;
                if (!hasDistance) {
                    opt.maxDistance(ParseUtil.parseDouble((String)part, (double)opt.maxDistance()));
                    hasDistance = true;
                    continue;
                }
                opt.spacing(ParseUtil.parseDouble((String)part, (double)opt.spacing()));
            }
        }
        if (!(name = info.getLine(2).trim()).isEmpty()) {
            name = info.getWorld().getUID().toString() + "_" + name;
        }
        opt.name(name);
        opt.statement(info.getLine(3).trim());
        return opt;
    }

    @Override
    public void execute(SignActionEvent info) {
    }

    @Override
    public String getDescriptiveOutputName(SignActionEvent event) {
        return "Train is activated pathing mutex";
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        return SignBuildOptions.create().setPermission(Permission.BUILD_MUTEX).setName("pathing mutex zone").setDescription("prevent more than one train entering a stretch of track ahead").setTraincartsWIKIHelp("TrainCarts/Signs/Mutex").handle(event);
    }
}

