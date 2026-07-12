/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.offline.OfflineWorld
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  org.bukkit.Location
 */
package com.bergerkiller.bukkit.tc.actions;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.actions.MemberActionLaunchDirection;
import com.bergerkiller.bukkit.tc.actions.MovementAction;
import com.bergerkiller.bukkit.tc.controller.components.ActionTracker;
import com.bergerkiller.bukkit.tc.offline.train.format.OfflineDataBlock;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.Location;

public class MemberActionLaunchLocation
extends MemberActionLaunchDirection
implements MovementAction {
    private final Location target;

    public MemberActionLaunchLocation(double targetvelocity, Location target) {
        this.initDistance(0.0, targetvelocity);
        this.target = target.clone();
    }

    public Location getTargetLocation() {
        return this.target;
    }

    @Override
    public void bind() {
        super.bind();
        this.setTargetDistance(((CommonMinecart)this.getMember().getEntity()).loc.distance(this.target));
        this.setDirection(this.getMember().getDirection());
    }

    @Override
    public void start() {
        super.setDirection(FaceUtil.getDirection((Location)this.getEntity().getLocation(), (Location)this.target, (boolean)false));
        double d = this.getEntity().loc.xz.distance(this.target);
        super.setTargetDistance(d += (double)Math.abs(this.target.getBlockY() - this.getEntity().loc.y.block()));
        super.start();
    }

    public static class Serializer
    extends MemberActionLaunchDirection.BaseSerializer<MemberActionLaunchLocation> {
        @Override
        public boolean save(MemberActionLaunchLocation action, OfflineDataBlock data, ActionTracker tracker) throws IOException {
            super.save(action, data, tracker);
            data.addChild("launch-location", stream -> {
                Location loc = action.getTargetLocation();
                StreamUtil.writeUUID((DataOutputStream)stream, (UUID)loc.getWorld().getUID());
                stream.writeDouble(loc.getX());
                stream.writeDouble(loc.getY());
                stream.writeDouble(loc.getZ());
            });
            return true;
        }

        @Override
        public MemberActionLaunchLocation create(OfflineDataBlock data) throws IOException {
            Location target;
            try (DataInputStream stream = data.findChildOrThrow("launch-location").readData();){
                OfflineWorld world = OfflineWorld.of((UUID)StreamUtil.readUUID((DataInputStream)stream));
                if (!world.isLoaded()) {
                    throw new IllegalStateException("Launch target world is not loaded");
                }
                double x = stream.readDouble();
                double y = stream.readDouble();
                double z = stream.readDouble();
                target = new Location(world.getLoadedWorld(), x, y, z);
            }
            return new MemberActionLaunchLocation(0.0, target);
        }
    }
}

