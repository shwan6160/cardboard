/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.collections.BlockMap
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.block.Block
 *  org.bukkit.event.Event
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.collections.BlockMap;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.DirectionStatement;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.actions.GroupActionWaitPathFinding;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.events.MissingPathConnectionEvent;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathConnection;
import com.bergerkiller.bukkit.tc.pathfinding.PathNavigateEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.pathfinding.PathPredictEvent;
import com.bergerkiller.bukkit.tc.pathfinding.SignRoutingEvent;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.statements.Statement;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.block.Block;
import org.bukkit.event.Event;

public class SignActionSwitcher
extends TrainCartsSignAction {
    private BlockMap<CounterState> switchedTimes = new BlockMap();

    private CounterState getSwitchedTimes(Block signblock) {
        CounterState i = (CounterState)this.switchedTimes.get(signblock);
        if (i == null) {
            i = new CounterState();
            this.switchedTimes.put(signblock, (Object)i);
        }
        return i;
    }

    private void cleanupCountersOnLeave(SignActionEvent info) {
        CounterState state;
        if (info.isAction(SignActionType.GROUP_LEAVE) && (state = (CounterState)this.switchedTimes.get(info.getBlock())) != null) {
            for (MinecartMember<?> member : info.getGroup()) {
                state.syncLeave(member);
            }
        }
    }

    private static List<DirectionStatement> parseDirectionStatements(SignActionEvent info) {
        ArrayList<DirectionStatement> statements = new ArrayList<DirectionStatement>();
        if (!info.getLine(2).isEmpty() || !info.getLine(3).isEmpty()) {
            String left_str = Direction.IMPLICIT_LEFT.aliases()[0];
            if (info.getLine(2).isEmpty()) {
                statements.add(new DirectionStatement("default", left_str));
            } else {
                statements.add(new DirectionStatement(info.getLine(2), left_str));
            }
            String right_str = Direction.IMPLICIT_RIGHT.aliases()[0];
            if (info.getLine(3).isEmpty()) {
                statements.add(new DirectionStatement("default", right_str));
            } else {
                statements.add(new DirectionStatement(info.getLine(3), right_str));
            }
        }
        for (String line : info.getExtraLinesBelow()) {
            if (line.isEmpty()) continue;
            DirectionStatement stat = new DirectionStatement(line, "");
            if (stat.direction.isEmpty()) continue;
            statements.add(stat);
        }
        return statements;
    }

    public SignActionSwitcher() {
        super("switcher", "tag");
    }

    @Override
    public void execute(SignActionEvent info) {
        new SwitcherLogic(this, info).run();
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        if (event.isCartSign()) {
            return SignBuildOptions.create().setPermission(Permission.BUILD_SWITCHER).setName("cart switcher").setDescription("switch between tracks based on properties of the cart above").setTraincartsWIKIHelp("TrainCarts/Signs/Switcher").handle(event);
        }
        if (event.isTrainSign()) {
            return SignBuildOptions.create().setPermission(Permission.BUILD_SWITCHER).setName("train switcher").setDescription("switch between tracks based on properties of the train above").setTraincartsWIKIHelp("TrainCarts/Signs/Switcher").handle(event);
        }
        return false;
    }

    @Override
    public boolean isRailSwitcher(SignActionEvent info) {
        return !TCConfig.onlyPoweredSwitchersDoPathFinding || !info.getHeader().isAlwaysOff();
    }

    @Override
    public void predictPathFinding(SignActionEvent info, PathPredictEvent prediction) {
        new SwitcherLogic(this, info).predict(prediction);
    }

    @Override
    public String getDescriptiveOutputName(SignActionEvent event) {
        return "Train activates switcher";
    }

    @Override
    public void route(SignRoutingEvent event) {
        if (TCConfig.onlyPoweredSwitchersDoPathFinding && event.getHeader().isAlwaysOff()) {
            return;
        }
        new SwitcherLogic(this, event).route(event);
    }

    @Override
    public boolean overrideFacing() {
        return true;
    }

    static /* synthetic */ List access$100(SignActionEvent x0) {
        return SignActionSwitcher.parseDirectionStatements(x0);
    }

    private static class CounterState {
        public int counter = 0;
        public int startLength = 0;
        public Set<UUID> uuidsToIgnore = Collections.emptySet();

        private CounterState() {
        }

        public void syncCartSignEnter(MinecartGroup group, RailPiece railPiece) {
            if (!TCConfig.switcherResetCountersOnFirstCart) {
                return;
            }
            boolean isNewGroup = !this.isGroupTracked(group);
            this.addAll(group);
            if (isNewGroup) {
                this.startLength = group.size();
                if (TCConfig.switcherResetCountersOnFirstCart) {
                    this.counter = 0;
                }
                if (this.uuidsToIgnore.size() != group.size()) {
                    this.uuidsToIgnore.clear();
                    this.addAll(group);
                    if (railPiece != RailPiece.NONE) {
                        railPiece.members().stream().map(MinecartMember::getGroup).distinct().forEach(this::addAll);
                    }
                }
            }
        }

        public void syncLeave(MinecartMember<?> member) {
            if (!this.uuidsToIgnore.isEmpty()) {
                this.uuidsToIgnore.remove(((CommonMinecart)member.getEntity()).getUniqueId());
                if (this.uuidsToIgnore.isEmpty()) {
                    this.uuidsToIgnore = Collections.emptySet();
                }
            }
        }

        private void addAll(MinecartGroup group) {
            if (this.uuidsToIgnore.isEmpty()) {
                this.uuidsToIgnore = new HashSet<UUID>();
            }
            for (MinecartMember<?> member : group) {
                this.uuidsToIgnore.add(((CommonMinecart)member.getEntity()).getUniqueId());
            }
        }

        private boolean isGroupTracked(MinecartGroup group) {
            for (MinecartMember<?> member : group) {
                if (!this.uuidsToIgnore.contains(((CommonMinecart)member.getEntity()).getUniqueId())) continue;
                return true;
            }
            return false;
        }
    }

    /*
     * Exception performing whole class analysis.
     */
    private class SwitcherLogic {
        private final SignActionEvent info;
        private final List<DirectionStatement> statements;
        private final boolean hasFromDirections;
        private final boolean doCart;
        private final boolean doTrain;
        private final boolean canToggleRails;
        final /* synthetic */ SignActionSwitcher this$0;

        /*
         * Unable to fully structure code
         */
        public SwitcherLogic(SignActionSwitcher var1_1, SignActionEvent info) {
            this.this$0 = var1_1;
            super();
            this.info = info;
            this.statements = SignActionSwitcher.access$100(info);
            calcHasFromDirections = false;
            for (DirectionStatement statement : this.statements) {
                if (statement.isSwitchedFromSelf() || statement.isDefault()) continue;
                calcHasFromDirections = true;
                break;
            }
            this.hasFromDirections = calcHasFromDirections;
            this.doTrain = info.isTrainSign() != false && info.isAction(new SignActionType[]{SignActionType.GROUP_ENTER, SignActionType.GROUP_UPDATE}) != false;
            v0 = this.doCart = info.isCartSign() != false && info.isAction(new SignActionType[]{SignActionType.MEMBER_ENTER, SignActionType.MEMBER_UPDATE}) != false;
            if (info.isCartSign() == false ? info.isAction(new SignActionType[]{SignActionType.GROUP_ENTER}) != false : info.isAction(new SignActionType[]{SignActionType.MEMBER_ENTER}) != false) ** GOTO lbl-1000
            if (this.hasFromDirections && info.isAction(new SignActionType[]{SignActionType.REDSTONE_CHANGE}) && info.hasRails() && info.isPowered()) lbl-1000:
            // 2 sources

            {
                v1 = true;
            } else {
                v1 = false;
            }
            this.canToggleRails = v1;
        }

        public void route(SignRoutingEvent event) {
            DirectionStatement activeDirection;
            if (!this.statements.isEmpty() && this.info.isEnterActivated() && (activeDirection = this.selectStatement(true, false)) != null) {
                this.predictRails(event, activeDirection);
                return;
            }
            event.setRouteSwitchable(true);
        }

        public void predict(PathPredictEvent prediction) {
            if (!this.canToggleRails) {
                return;
            }
            boolean facing = this.info.isEnterActivated();
            DirectionStatement activeDirection = null;
            if (!this.statements.isEmpty() && facing) {
                activeDirection = this.selectStatement(false, false);
                if (!(activeDirection == null || this.canToggleRails && this.info.isPowered())) {
                    activeDirection = null;
                }
                if (activeDirection != null && !activeDirection.isDefault()) {
                    this.predictRails(prediction, activeDirection);
                    return;
                }
            }
            boolean handlePathfinding = true;
            if (TCConfig.onlyPoweredSwitchersDoPathFinding && !this.info.isPowered()) {
                handlePathfinding = false;
            }
            if (TCConfig.onlyEmptySwitchersDoPathFinding && !this.statements.isEmpty()) {
                handlePathfinding = false;
            }
            if (handlePathfinding && this.predictPathFinding(prediction, facing)) {
                return;
            }
            if (activeDirection != null) {
                this.predictRails(prediction, activeDirection);
            }
        }

        public void run() {
            boolean hasMember;
            this.this$0.cleanupCountersOnLeave(this.info);
            if (!this.doTrain && !this.doCart) {
                if (this.info.isAction(SignActionType.MEMBER_LEAVE) && this.info.isCartSign()) {
                    this.info.setLevers(false);
                    return;
                }
                if (this.info.isAction(SignActionType.GROUP_LEAVE) && this.info.isTrainSign()) {
                    this.info.setLevers(false);
                    return;
                }
                if (!this.canToggleRails) {
                    return;
                }
            }
            boolean facing = !(hasMember = this.info.hasRailedMember()) || this.info.isFacing();
            DirectionStatement activeDirection = null;
            if (facing) {
                if (this.statements.isEmpty()) {
                    if (hasMember) {
                        this.info.setLevers(true);
                    }
                } else {
                    activeDirection = this.selectStatement(false, true);
                    if (hasMember) {
                        this.info.setLevers(activeDirection != null && !activeDirection.isDefault());
                    }
                    if (!(activeDirection == null || this.canToggleRails && this.info.isPowered())) {
                        activeDirection = null;
                    }
                    if (activeDirection != null && !activeDirection.isDefault()) {
                        this.switchRails(activeDirection);
                        return;
                    }
                }
            }
            boolean handlePathfinding = true;
            if (TCConfig.onlyPoweredSwitchersDoPathFinding && !this.info.isPowered()) {
                handlePathfinding = false;
            }
            if (TCConfig.onlyEmptySwitchersDoPathFinding && !this.statements.isEmpty()) {
                handlePathfinding = false;
            }
            if (handlePathfinding && this.handlePathFinding(facing)) {
                return;
            }
            if (activeDirection != null) {
                this.switchRails(activeDirection);
            }
        }

        private void switchRails(DirectionStatement direction) {
            if (direction.isSwitchedFromSelf()) {
                this.info.setRailsTo(direction.direction);
            } else {
                this.info.setRailsFromTo(direction.directionFrom, direction.direction);
            }
        }

        private void predictRails(PathNavigateEvent navigateEvent, DirectionStatement direction) {
            RailJunction b;
            RailJunction a = this.info.findJunction(direction.direction);
            RailJunction railJunction = b = direction.isSwitchedFromSelf() ? null : this.info.findJunction(direction.directionFrom);
            if (b == null) {
                if (a == null) {
                    return;
                }
                navigateEvent.setSwitchedJunction(a);
            } else if (a == null) {
                navigateEvent.setSwitchedJunction(b);
            } else {
                RailPath.Position pos = navigateEvent.railState().position();
                if (a.position().motDot(pos) > b.position().motDot(pos)) {
                    navigateEvent.setSwitchedJunction(a);
                } else {
                    navigateEvent.setSwitchedJunction(b);
                }
            }
        }

        private void predictRailsTo(PathNavigateEvent prediction, String name) {
            RailJunction junction = this.info.findJunction(name);
            if (junction != null) {
                prediction.setSwitchedJunction(junction);
            }
        }

        private boolean handlePathFinding(boolean facing) {
            PathNode node;
            if (this.info.isAction(SignActionType.MEMBER_ENTER, SignActionType.GROUP_ENTER) && (facing || !this.info.isWatchedDirectionsDefined()) && (node = PathNode.getOrCreate(this.info)) != null) {
                String destination = null;
                IProperties prop = null;
                if (this.doCart && this.info.hasMember()) {
                    prop = this.info.getMember().getProperties();
                } else if (this.doTrain && this.info.hasGroup()) {
                    prop = this.info.getGroup().getProperties();
                }
                if (prop != null) {
                    destination = prop.getDestination();
                    prop.setLastPathNode(node.getName());
                }
                if (!LogicUtil.nullOrEmpty(destination) && !node.containsName(destination)) {
                    if (this.info.getTrainCarts().getPathProvider().isProcessing()) {
                        double currentForce = this.info.getGroup().getAverageForce();
                        this.info.getGroup().getActions().addAction(new GroupActionWaitPathFinding(this.info, node, destination));
                        this.info.getMember().getActions().addActionLaunch(this.info.getMember().getDirectionFrom(), 1.0, currentForce);
                        this.info.getGroup().stop();
                    } else {
                        PathConnection conn = node.findConnection(destination);
                        if (conn != null) {
                            if (this.canToggleRails) {
                                this.info.setRailsTo(conn.junctionName);
                            }
                        } else {
                            CommonUtil.callEvent((Event)new MissingPathConnectionEvent(this.info.getRailPiece(), node, this.info.getGroup(), destination));
                            Localization.PATHING_FAILED.broadcast(this.info.getGroup(), destination);
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        private boolean predictPathFinding(PathPredictEvent prediction, boolean facing) {
            PathNode node;
            if ((facing || !this.info.isWatchedDirectionsDefined()) && (node = PathNode.getOrCreate(this.info)) != null) {
                if (this.info.getTrainCarts().getPathProvider().isProcessing()) {
                    prediction.setSpeedLimit(0.0);
                } else {
                    PathConnection conn;
                    String destination = null;
                    if (this.doCart) {
                        destination = this.info.getMember().getProperties().getDestination();
                    } else if (this.doTrain) {
                        destination = this.info.getGroup().getProperties().getDestination();
                    }
                    if (!LogicUtil.nullOrEmpty(destination) && !node.containsName(destination) && (conn = node.findConnection(destination)) != null) {
                        this.predictRailsTo(prediction, conn.junctionName);
                    }
                }
            }
            return false;
        }

        private DirectionStatement selectStatement(boolean isPathRouting, boolean incrementCounters) {
            boolean hasMember = this.info.hasRailedMember();
            if (this.statements.isEmpty()) {
                if (hasMember) {
                    this.info.setLevers(true);
                }
                return null;
            }
            int maxcount = 0;
            CounterState signcounter = null;
            for (DirectionStatement stat : this.statements) {
                if (!stat.hasCounter()) continue;
                if (signcounter == null) {
                    signcounter = this.this$0.getSwitchedTimes(this.info.getBlock());
                    if (this.info.isCartSign() && incrementCounters && this.info.hasGroup()) {
                        signcounter.syncCartSignEnter(this.info.getGroup(), this.info.getRailPiece());
                    }
                }
                maxcount += stat.counter.get(signcounter.startLength);
            }
            int counter = 0;
            if (signcounter != null && incrementCounters) {
                if (this.info.isAction(SignActionType.MEMBER_ENTER, SignActionType.GROUP_ENTER)) {
                    ++signcounter.counter;
                } else if (this.info.isAction(SignActionType.REDSTONE_ON) && this.hasFromDirections) {
                    ++signcounter.counter;
                }
                if (signcounter.counter > maxcount) {
                    signcounter.counter = 1;
                }
                counter = 1;
            }
            DirectionStatement dir = null;
            for (DirectionStatement stat : this.statements) {
                if (stat.isDefault()) continue;
                if (stat.hasCounter()) {
                    if (isPathRouting) {
                        return null;
                    }
                    if ((counter += stat.counter.get(signcounter.startLength)) > signcounter.counter) {
                        dir = stat;
                        break;
                    }
                }
                if (isPathRouting) {
                    Statement.MatchResult result = Statement.Matcher.of(stat.text).withSignEvent(this.info).match();
                    if (!result.isConstant()) {
                        return null;
                    }
                    if (result.has()) {
                        dir = stat;
                        break;
                    }
                }
                if (this.doCart && stat.has(this.info, this.info.getMember()) || this.doTrain && stat.has(this.info, this.info.getGroup())) {
                    dir = stat;
                    break;
                }
                if (stat.isSwitchedFromSelf() || !stat.has(this.info, (MinecartMember)null)) continue;
                dir = stat;
                break;
            }
            if (dir == null && !isPathRouting) {
                for (DirectionStatement stat : this.statements) {
                    if (!stat.isDefault() || !hasMember && stat.isSwitchedFromSelf()) continue;
                    dir = stat;
                    break;
                }
            }
            if (dir != null && dir.direction.isEmpty()) {
                dir = null;
            }
            return dir;
        }
    }
}

