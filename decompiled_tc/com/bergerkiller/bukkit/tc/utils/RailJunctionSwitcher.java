/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.components.RailTracker;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RailJunctionSwitcher {
    private final RailPiece rail;
    private final Predicate<MinecartMember<?>> memberFilter;

    public RailJunctionSwitcher(RailPiece rail) {
        this(rail, LogicUtil.alwaysTruePredicate());
    }

    public RailJunctionSwitcher(RailPiece rail, Predicate<MinecartMember<?>> memberFilter) {
        this.rail = rail;
        this.memberFilter = memberFilter;
    }

    public void switchJunction(RailJunction from, RailJunction to) {
        List members = this.rail.members().stream().filter(m -> !m.isUnloaded()).filter(this.memberFilter).map(m -> m.getRailTracker().getRail()).filter(rail -> rail.state.railPiece().equals(this.rail)).map(MemberOnRail::new).collect(Collectors.toList());
        MinecartGroupStore.notifyPhysicsChange();
        this.rail.type().switchJunction(this.rail.block(), from, to);
        for (MemberOnRail member : members) {
            RailPath path = member.state.loadRailLogic().getPath();
            path.move(member.state, member.distanceTraveled);
            member.member.snapToPosition(member.state.position());
        }
    }

    private static class MemberOnRail {
        public final MinecartMember<?> member;
        public final RailState state;
        public final double distanceTraveled;

        public MemberOnRail(RailTracker.TrackedRail rail) {
            this.member = rail.member;
            this.state = rail.state.cloneAndInvertMotion();
            this.distanceTraveled = rail.getPath().move(this.state, Double.MAX_VALUE);
            this.state.position().invertMotion();
        }
    }
}

