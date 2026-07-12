/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.actions.Action;
import com.bergerkiller.bukkit.tc.actions.BlockActionSetLevers;
import com.bergerkiller.bukkit.tc.actions.MemberAction;
import com.bergerkiller.bukkit.tc.actions.MovementAction;
import com.bergerkiller.bukkit.tc.actions.TrackedSignActionSetOutput;
import com.bergerkiller.bukkit.tc.actions.WaitAction;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatus;
import com.bergerkiller.bukkit.tc.controller.status.TrainStatusProvider;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.bukkit.block.Block;

public abstract class ActionTracker
implements TrainStatusProvider {
    private final Queue<Action> actions = new LinkedList<Action>();

    public boolean hasAction() {
        return !this.actions.isEmpty();
    }

    public Collection<Action> getScheduledActions() {
        return Collections.unmodifiableCollection(this.actions);
    }

    public abstract TrainCarts.Provider getOwner();

    public abstract MinecartGroup getGroupOwner();

    public void clear() {
        for (Action a : this.actions) {
            a.cancel();
        }
        this.actions.clear();
    }

    public void removeActions(MinecartMember<?> forMember) {
        Iterator iter = this.actions.iterator();
        while (iter.hasNext()) {
            Action action = (Action)iter.next();
            if (!(action instanceof MemberAction) || ((MemberAction)action).getMember() != forMember) continue;
            action.cancel();
            iter.remove();
        }
    }

    public Action removeAction() {
        Action a = this.actions.remove();
        if (a != null) {
            a.cancel();
        }
        return a;
    }

    public <T extends Action> T addAction(T action) {
        this.actions.offer(action);
        action.bind();
        return action;
    }

    public BlockActionSetLevers addActionSetLevers(Block block, boolean down) {
        return this.addAction(new BlockActionSetLevers(this.getOwner().getTrainCarts(), block, down));
    }

    public TrackedSignActionSetOutput addActionSetSignOutput(RailLookup.TrackedSign sign, boolean output) {
        return this.addAction(new TrackedSignActionSetOutput(this.getOwner().getTrainCarts(), sign, output));
    }

    public boolean isMovementControlled() {
        Action a = this.getCurrentAction();
        return a instanceof MovementAction && ((MovementAction)((Object)a)).isMovementSuppressed();
    }

    public boolean isWaitAction() {
        return this.getCurrentAction() instanceof WaitAction;
    }

    public boolean isCurrentActionTag(String tag) {
        return !this.actions.isEmpty() && this.actions.peek().hasTag(tag);
    }

    public Action getCurrentAction() {
        return this.actions.peek();
    }

    public void doTick() {
        Action action;
        while ((action = this.actions.peek()) != null && action.doTick()) {
            if (action != this.actions.peek()) continue;
            this.actions.remove();
        }
    }

    @Override
    public List<TrainStatus> getStatusInfo() {
        Action currentAction = this.getCurrentAction();
        if (currentAction == null) {
            return Collections.emptyList();
        }
        return currentAction.getStatusInfo();
    }
}

