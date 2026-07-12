/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.permissions.PermissionEnum
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.generated.org.bukkit.block.SignHandle
 *  org.bukkit.ChatColor
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.Sign
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.block.SignChangeEvent
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.BlockLocation;
import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.permissions.PermissionEnum;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignBuildEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.pathfinding.PathNode;
import com.bergerkiller.bukkit.tc.pathfinding.PathPredictEvent;
import com.bergerkiller.bukkit.tc.pathfinding.SignRoutingEvent;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.signactions.SignActionAnimate;
import com.bergerkiller.bukkit.tc.signactions.SignActionAnnounce;
import com.bergerkiller.bukkit.tc.signactions.SignActionBlockChanger;
import com.bergerkiller.bukkit.tc.signactions.SignActionBlocker;
import com.bergerkiller.bukkit.tc.signactions.SignActionBukkitEffect;
import com.bergerkiller.bukkit.tc.signactions.SignActionCraft;
import com.bergerkiller.bukkit.tc.signactions.SignActionDestination;
import com.bergerkiller.bukkit.tc.signactions.SignActionDestroy;
import com.bergerkiller.bukkit.tc.signactions.SignActionDetector;
import com.bergerkiller.bukkit.tc.signactions.SignActionEffect;
import com.bergerkiller.bukkit.tc.signactions.SignActionEject;
import com.bergerkiller.bukkit.tc.signactions.SignActionElevator;
import com.bergerkiller.bukkit.tc.signactions.SignActionEnter;
import com.bergerkiller.bukkit.tc.signactions.SignActionFlip;
import com.bergerkiller.bukkit.tc.signactions.SignActionFuel;
import com.bergerkiller.bukkit.tc.signactions.SignActionJumper;
import com.bergerkiller.bukkit.tc.signactions.SignActionLauncher;
import com.bergerkiller.bukkit.tc.signactions.SignActionMode;
import com.bergerkiller.bukkit.tc.signactions.SignActionMutex;
import com.bergerkiller.bukkit.tc.signactions.SignActionPathingMutex;
import com.bergerkiller.bukkit.tc.signactions.SignActionProperties;
import com.bergerkiller.bukkit.tc.signactions.SignActionSkip;
import com.bergerkiller.bukkit.tc.signactions.SignActionSound;
import com.bergerkiller.bukkit.tc.signactions.SignActionSpawn;
import com.bergerkiller.bukkit.tc.signactions.SignActionStation;
import com.bergerkiller.bukkit.tc.signactions.SignActionSwitcher;
import com.bergerkiller.bukkit.tc.signactions.SignActionTeleport;
import com.bergerkiller.bukkit.tc.signactions.SignActionTicket;
import com.bergerkiller.bukkit.tc.signactions.SignActionTitle;
import com.bergerkiller.bukkit.tc.signactions.SignActionTransfer;
import com.bergerkiller.bukkit.tc.signactions.SignActionTrigger;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.SignActionWait;
import com.bergerkiller.bukkit.tc.signactions.util.SignActionLookupMap;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.bergerkiller.generated.org.bukkit.block.SignHandle;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.SignChangeEvent;

public abstract class SignAction {
    private static SignActionLookupMap lookup = SignActionLookupMap.DISABLED;
    private final boolean _hasPathPrediction = CommonUtil.isMethodOverrided(SignAction.class, this.getClass(), (String)"predictPathFinding", (Class[])new Class[]{SignActionEvent.class, PathPredictEvent.class});

    public static void init() {
        lookup = SignActionLookupMap.create();
        SignAction.register(new SignActionStation());
        SignAction.register(new SignActionLauncher());
        SignAction.register(new SignActionSwitcher());
        SignAction.register(new SignActionSpawn());
        SignAction.register(new SignActionBlockChanger());
        SignAction.register(new SignActionProperties());
        SignAction.register(new SignActionTrigger());
        SignAction.register(new SignActionTeleport());
        SignAction.register(new SignActionJumper());
        SignAction.register(new SignActionEject());
        SignAction.register(new SignActionEnter());
        SignAction.register(new SignActionDestroy());
        SignAction.register(new SignActionTransfer());
        SignAction.register(new SignActionFuel());
        SignAction.register(new SignActionCraft());
        SignAction.register(SignActionDetector.INSTANCE);
        SignAction.register(new SignActionDestination());
        SignAction.register(new SignActionBlocker());
        SignAction.register(new SignActionWait());
        SignAction.register(SignActionElevator.INSTANCE);
        SignAction.register(new SignActionTicket());
        SignAction.register(new SignActionAnnounce());
        SignAction.register(new SignActionEffect());
        SignAction.register(new SignActionBukkitEffect());
        SignAction.register(new SignActionSound());
        SignAction.register(new SignActionSkip());
        SignAction.register(new SignActionMutex());
        SignAction.register(new SignActionPathingMutex());
        SignAction.register(new SignActionFlip());
        SignAction.register(new SignActionAnimate());
        if (Common.evaluateMCVersion((String)">=", (String)"1.11")) {
            SignAction.register(new SignActionTitle());
        }
    }

    public static void deinit() {
        lookup = SignActionLookupMap.DISABLED;
    }

    public static SignActionLookupMap getLookup() {
        return lookup;
    }

    public static SignAction getSignAction(SignActionEvent event) {
        return lookup.lookup(event).map(SignActionLookupMap.Entry::action).orElse(null);
    }

    public static <T extends SignAction> T register(T action) {
        return SignAction.register(action, false);
    }

    public static <T extends SignAction> T register(T action, boolean priority) {
        return lookup.register(action, priority);
    }

    public static void unregister(SignAction action) {
        lookup.unregister(action);
    }

    public static void handleLoadChange(Sign sign, boolean frontText, boolean loaded) {
        RailLookup.TrackedSign trackedSign = RailLookup.TrackedSign.forRealSign(sign, frontText, RailPiece.NONE);
        trackedSign.rail = null;
        SignAction.handleLoadChange(trackedSign, loaded);
    }

    public static void handleLoadChange(RailLookup.TrackedSign trackedSign, boolean loaded) {
        SignActionEvent info = new SignActionEvent(trackedSign);
        lookup.lookup(info, SignActionLookupMap.LookupMode.WITH_LOADED_CHANGED_HANDLER).map(SignActionLookupMap.Entry::action).ifPresent(e -> e.loadedChanged(info, loaded));
    }

    public static boolean handleClick(Block clickedSign, Player player) {
        Sign bsign = BlockUtil.getSign((Block)clickedSign);
        if (bsign == null) {
            return false;
        }
        SignHandle bsignhandle = SignHandle.createHandle((Object)bsign);
        if (!bsignhandle.getFrontLine(0).isEmpty() && SignAction.handleClick(RailLookup.TrackedSign.forRealSign(bsign, clickedSign, true, null), player)) {
            return true;
        }
        return !bsignhandle.getBackLine(0).isEmpty() && SignAction.handleClick(RailLookup.TrackedSign.forRealSign(bsign, clickedSign, false, null), player);
    }

    private static boolean handleClick(RailLookup.TrackedSign clickedSign, Player player) {
        SignActionEvent info = new SignActionEvent(clickedSign);
        SignAction action = SignAction.getSignAction(info);
        return action != null && action.click(info, player);
    }

    @Deprecated
    public static boolean handleBuild(SignChangeActionEvent event, PermissionEnum permission, String signname) {
        return SignAction.handleBuild(event, permission, signname, null);
    }

    @Deprecated
    public static boolean handleBuild(SignChangeActionEvent event, PermissionEnum permission, String signname, String signdescription) {
        return SignBuildOptions.create().setPermission(permission).setName(signname).setDescription(signdescription).handle(event.getPlayer());
    }

    @Deprecated
    public static void handleBuild(SignChangeActionEvent info) {
        SignAction.handleBuild(new SignBuildEvent(info));
    }

    @Deprecated
    public static void handleBuild(SignChangeEvent event) {
        SignAction.handleBuild(new SignBuildEvent(event, true));
    }

    public static void handleBuild(SignBuildEvent info) {
        BlockFace newFacing;
        BlockFace oldFacing;
        BlockData data;
        CommonUtil.callEvent((Event)info);
        if (info.isCancelled()) {
            return;
        }
        if (info.hasRegisteredAction()) {
            Object node;
            String destinationName;
            SignAction action = info.getRegisteredAction();
            if (!info.getTrackedSign().isRealSign() && !action.canSupportFakeSign(info)) {
                info.getPlayer().sendMessage(ChatColor.RED + "A real sign is required for this type of action");
                info.setCancelled(true);
                return;
            }
            if (!action.build(info)) {
                info.setCancelled(true);
                return;
            }
            if (action.canSupportRC()) {
                if (info.isRCSign() && !Permission.BUILD_REMOTE_CONTROL.has((CommandSender)info.getPlayer())) {
                    Localization.SIGN_NO_RC_PERMISSION.message((CommandSender)info.getPlayer(), new String[0]);
                    info.getHeader().setMode(SignActionMode.TRAIN);
                    info.setLine(0, info.getHeader().toString());
                }
            } else if (info.isRCSign()) {
                info.getPlayer().sendMessage(ChatColor.RED + "This sign does not support remote control!");
                info.getHeader().setMode(SignActionMode.TRAIN);
                info.setLine(0, info.getHeader().toString());
            }
            if ((destinationName = action.getRailDestinationName(info)) != null && (node = info.getTrainCarts().getPathProvider().getWorld(info.getWorld()).getNodeByName(destinationName)) != null) {
                Player player = info.getPlayer();
                player.sendMessage(ChatColor.RED + "Another destination with the same name already exists!");
                player.sendMessage(ChatColor.RED + "Please remove either sign and use /train reroute to fix");
                BlockLocation loc = ((PathNode)node).location;
                StringBuilder locMsg = new StringBuilder(100);
                locMsg.append(ChatColor.RED).append("Other destination '" + destinationName + "' is ");
                if (loc.getWorld() != info.getPlayer().getWorld()) {
                    locMsg.append("on world ").append(ChatColor.WHITE).append(((PathNode)node).location.world);
                    locMsg.append(' ').append(ChatColor.RED);
                }
                locMsg.append("at ").append(ChatColor.WHITE);
                locMsg.append('[').append(loc.x).append('/').append(loc.y);
                locMsg.append('/').append(loc.z).append(']');
                player.sendMessage(locMsg.toString());
            }
            if (info.hasRails()) {
                for (MinecartMember minecartMember : info.getRailPiece().members()) {
                    if (minecartMember.isUnloaded() || ((CommonMinecart)minecartMember.getEntity()).isRemoved()) continue;
                    minecartMember.getGroup().getSignTracker().updatePosition();
                }
            }
            action.loadedChanged(info, true);
        }
        if (info.getMode() != SignActionMode.NONE && info.getTrackedSign().isRealSign() && MaterialUtil.ISSIGN.get(data = WorldUtil.getBlockData((Block)info.getBlock())).booleanValue() && FaceUtil.isVertical((BlockFace)data.getAttachedFace()) && (oldFacing = data.getFacingDirection()) != (newFacing = Util.snapFace(oldFacing))) {
            BlockUtil.setFacing((Block)info.getBlock(), (BlockFace)newFacing);
        }
    }

    public static void handleDestroy(SignActionEvent info) {
        if (info == null || info.getSign() == null) {
            return;
        }
        SignAction action = SignAction.getSignAction(info);
        if (action != null) {
            PathNode node;
            Block rails;
            PathNode node2;
            for (MinecartGroup group : MinecartGroup.getGroups().cloneAsIterable()) {
                group.getSignTracker().removeSign(info.getTrackedSign());
            }
            boolean switchable = action.isRailSwitcher(info);
            String destinationName = action.getRailDestinationName(info);
            action.destroy(info);
            if (destinationName != null && (node2 = info.getTrainCarts().getPathProvider().getWorld(info.getWorld()).getNodeByName(destinationName)) != null) {
                node2.removeName(destinationName);
            }
            if (switchable && (rails = info.getRails()) != null && (node = PathNode.get(rails)) != null) {
                node.remove();
            }
            action.loadedChanged(info, false);
        }
    }

    public static void executeAll(SignActionEvent info, SignActionType actiontype) {
        info.setAction(actiontype);
        SignAction.executeAll(info);
    }

    public static void executeAll(SignActionEvent info) {
        if (info == null || info.getSign() == null) {
            return;
        }
        info.setCancelled(false);
        if (((SignActionEvent)CommonUtil.callEvent((Event)info)).isCancelled()) {
            return;
        }
        SignAction.executeOneImpl(SignAction.getSignAction(info), info);
    }

    public static void executeOne(SignAction action, SignActionEvent info) {
        if (info == null || info.getSign() == null) {
            return;
        }
        info.setCancelled(false);
        if (((SignActionEvent)CommonUtil.callEvent((Event)info)).isCancelled()) {
            return;
        }
        SignAction.executeOneImpl(action, info);
    }

    private static void executeOneImpl(SignAction action, SignActionEvent info) {
        if (action == null) {
            return;
        }
        if (info.isAction(SignActionType.MEMBER_MOVE) && !action.isMemberMoveHandled(info)) {
            return;
        }
        if (!info.getTrackedSign().isRealSign() && !action.canSupportFakeSign(info)) {
            return;
        }
        if (!action.overrideFacing() && info.getAction().isMovement() && !info.isFacing()) {
            return;
        }
        try {
            action.execute(info);
        }
        catch (Throwable t) {
            String signInfo;
            if (info.getTrackedSign().isRealSign()) {
                Block signBlock = info.getBlock();
                signInfo = signBlock.getWorld().getName() + " x=" + signBlock.getX() + " y=" + signBlock.getY() + " z=" + signBlock.getZ();
            } else if (info.hasRails()) {
                Block railBlock = info.getRails();
                signInfo = info.getTrackedSign().getClass().getSimpleName() + " rail " + railBlock.getWorld().getName() + " x=" + railBlock.getX() + " y=" + railBlock.getY() + " z=" + railBlock.getZ();
            } else {
                signInfo = info.getTrackedSign().getClass().getSimpleName() + " key " + info.getTrackedSign().getUniqueKey();
            }
            info.getTrainCarts().getLogger().log(Level.SEVERE, "Failed to execute " + info.getAction().toString() + " for " + action.getClass().getSimpleName() + " at {" + signInfo + "}:", CommonUtil.filterStackTrace((Throwable)t));
        }
    }

    public boolean verify(SignActionEvent info) {
        if (!info.getHeader().isValid()) {
            return false;
        }
        return !info.getHeader().isActionFiltered(info.getAction());
    }

    public abstract boolean match(SignActionEvent var1);

    public abstract void execute(SignActionEvent var1);

    public abstract boolean build(SignChangeActionEvent var1);

    public void destroy(SignActionEvent info) {
    }

    public void loadedChanged(SignActionEvent info, boolean loaded) {
    }

    public boolean canSupportRC() {
        return false;
    }

    public boolean canSupportFakeSign(SignActionEvent info) {
        return true;
    }

    public boolean overrideFacing() {
        return false;
    }

    public boolean isMemberMoveHandled(SignActionEvent info) {
        return false;
    }

    public void route(SignRoutingEvent event) {
        String destinationName;
        if (this.isPathFindingBlocked(event, event.getCartEnterState())) {
            event.setBlocked();
            return;
        }
        if (this.isRailSwitcher(event)) {
            event.setRouteSwitchable(true);
        }
        if ((destinationName = this.getRailDestinationName(event)) != null) {
            event.addDestinationName(destinationName);
        }
    }

    public boolean isRailSwitcher(SignActionEvent info) {
        return false;
    }

    public String getRailDestinationName(SignActionEvent info) {
        return null;
    }

    public boolean isPathFindingBlocked(SignActionEvent info, RailState state) {
        return false;
    }

    public void predictPathFinding(SignActionEvent info, PathPredictEvent prediction) {
    }

    public final boolean hasPathFindingPrediction() {
        return this._hasPathPrediction;
    }

    public boolean click(SignActionEvent info, Player player) {
        return false;
    }

    public boolean signTextChanged(SignActionEvent event) {
        return true;
    }

    public String getDescriptiveOutputName(SignActionEvent event) {
        return null;
    }
}

