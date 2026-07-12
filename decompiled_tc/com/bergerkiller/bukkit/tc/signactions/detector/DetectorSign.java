/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 */
package com.bergerkiller.bukkit.tc.signactions.detector;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.detector.DetectorListener;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSign;
import com.bergerkiller.bukkit.tc.offline.sign.OfflineSignStore;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignActionDetector;
import com.bergerkiller.bukkit.tc.statements.Statement;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class DetectorSign
implements DetectorListener {
    private final OfflineSignStore store;
    private final OfflineSign sign;
    private Metadata metadata;

    public DetectorSign(OfflineSignStore store, OfflineSign sign, Metadata metadata) {
        this.store = store;
        this.sign = sign;
        this.metadata = metadata;
    }

    public IntVector3 getLocation() {
        return this.sign.getPosition();
    }

    public boolean isRemoved() {
        return this.metadata.owner != this;
    }

    public void remove() {
        this.store.remove(this.sign, Metadata.class);
    }

    public void loadChunks(World world) {
        int cx = MathUtil.toChunk((int)this.sign.getPosition().x);
        int cz = MathUtil.toChunk((int)this.sign.getPosition().z);
        WorldUtil.loadChunks((World)world, (int)cx, (int)cz, (int)3);
    }

    public boolean validate(SignActionEvent event) {
        return SignActionDetector.INSTANCE.match(event);
    }

    public boolean isLoaded(World world) {
        return world != null;
    }

    public SignActionEvent initSignEvent() {
        Block signBlock = this.sign.getLoadedBlock();
        if (signBlock != null) {
            SignActionEvent event;
            this.loadChunks(signBlock.getWorld());
            Sign sign = BlockUtil.getSign((Block)signBlock);
            if (sign != null && this.validate(event = new SignActionEvent(RailLookup.TrackedSign.forRealSign(sign, this.sign.isFrontText(), null)))) {
                return event;
            }
            this.remove();
            return null;
        }
        return null;
    }

    @Override
    public void onLeave(MinecartGroup group) {
        SignActionEvent event;
        if (this.metadata.isLeverDown && (event = this.initSignEvent()) != null && event.isTrainSign() && this.isLeverUpCheckNeeded(event, group)) {
            this.updateGroups(event);
        }
    }

    @Override
    public void onEnter(MinecartGroup group) {
        SignActionEvent event;
        if (!this.metadata.isLeverDown && !this.isRemoved() && (event = this.initSignEvent()) != null && event.isTrainSign() && this.isDown(event, null, group)) {
            this.metadata = this.metadata.setLeverDown(true);
            this.store.putIfPresent(this.sign, this.metadata);
            event.setLevers(true);
        }
    }

    @Override
    public void onLeave(MinecartMember<?> member) {
        SignActionEvent event;
        if (this.metadata.isLeverDown && (event = this.initSignEvent()) != null && event.isCartSign() && this.isLeverUpCheckNeeded(event, member)) {
            this.updateMembers(event);
        }
    }

    @Override
    public void onEnter(MinecartMember<?> member) {
        SignActionEvent event;
        if (!this.metadata.isLeverDown && !this.isRemoved() && (event = this.initSignEvent()) != null && event.isCartSign() && this.isDown(event, member, null)) {
            this.metadata = this.metadata.setLeverDown(true);
            this.store.putIfPresent(this.sign, this.metadata);
            event.setLevers(true);
        }
    }

    public boolean updateMembers(SignActionEvent event) {
        if (this.isRemoved()) {
            event.setLevers(false);
            return false;
        }
        for (MinecartMember<?> mm : this.metadata.region.getMembers()) {
            if (!this.isDown(event, mm, null)) continue;
            this.metadata = this.metadata.setLeverDown(true);
            this.store.putIfPresent(this.sign, this.metadata);
            event.setLevers(true);
            return true;
        }
        this.metadata = this.metadata.setLeverDown(false);
        this.store.putIfPresent(this.sign, this.metadata);
        event.setLevers(false);
        return false;
    }

    public boolean updateGroups(SignActionEvent event) {
        if (this.isRemoved()) {
            event.setLevers(false);
            return false;
        }
        for (MinecartGroup g : this.metadata.region.getGroups()) {
            if (!this.isDown(event, null, g)) continue;
            this.metadata = this.metadata.setLeverDown(true);
            this.store.putIfPresent(this.sign, this.metadata);
            event.setLevers(true);
            return true;
        }
        this.metadata = this.metadata.setLeverDown(false);
        this.store.putIfPresent(this.sign, this.metadata);
        event.setLevers(false);
        return false;
    }

    @Override
    public void onUpdate(MinecartMember<?> member) {
        SignActionEvent event = this.initSignEvent();
        if (event != null) {
            this.updateMembers(event);
        }
    }

    @Override
    public void onUpdate(MinecartGroup group) {
        SignActionEvent event = this.initSignEvent();
        if (event != null) {
            this.updateGroups(event);
        }
    }

    public boolean isLeverUpCheckNeeded(SignActionEvent event, MinecartMember<?> member) {
        return !this.metadata.region.hasMembers() || this.isDown(event, member, null);
    }

    public boolean isLeverUpCheckNeeded(SignActionEvent event, MinecartGroup group) {
        return !this.metadata.region.hasGroups() || this.isDown(event, null, group);
    }

    public boolean isDown(SignActionEvent event, MinecartMember<?> member, MinecartGroup group) {
        if (member != null) {
            event.setMember(member);
        } else if (group != null) {
            event.setGroup(group);
        } else {
            event.setGroup(null);
        }
        boolean firstEmpty = false;
        if (event.getLine(2).isEmpty()) {
            firstEmpty = true;
        } else if (Statement.has(member, group, event.getLine(2), event)) {
            return true;
        }
        if (event.getLine(3).isEmpty()) {
            return firstEmpty;
        }
        return Statement.has(member, group, event.getLine(3), event);
    }

    @Override
    public void onRegister(DetectorRegion region) {
    }

    @Override
    public void onUnregister(DetectorRegion region) {
    }

    @Override
    public void onUnload(MinecartGroup group) {
        this.onLeave(group);
    }

    public static class Metadata {
        public final OfflineBlock otherSign;
        public final boolean otherSignFront;
        public final DetectorRegion region;
        public final boolean isLeverDown;
        public DetectorSign owner;

        public Metadata(RailLookup.TrackedSign otherSign, DetectorRegion region, boolean isLeverDown) {
            this(OfflineBlock.of((Block)otherSign.signBlock), ((RailLookup.TrackedRealSign)otherSign).isFrontText(), region, isLeverDown);
        }

        public Metadata(OfflineBlock otherSign, boolean otherSignFront, DetectorRegion region, boolean isLeverDown) {
            this(otherSign, otherSignFront, region, isLeverDown, null);
        }

        private Metadata(OfflineBlock otherSign, boolean otherSignFront, DetectorRegion region, boolean isLeverDown, DetectorSign owner) {
            this.otherSign = otherSign;
            this.otherSignFront = otherSignFront;
            this.region = region;
            this.isLeverDown = isLeverDown;
            this.owner = owner;
        }

        public Metadata setLeverDown(boolean down) {
            if (down == this.isLeverDown) {
                return this;
            }
            return new Metadata(this.otherSign, this.otherSignFront, this.region, down, this.owner);
        }
    }
}

