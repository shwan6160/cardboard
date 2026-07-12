/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.offline.OfflineBlock
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.ChatColor
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.signactions;

import com.bergerkiller.bukkit.common.offline.OfflineBlock;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.signactions.TrainCartsSignAction;
import com.bergerkiller.bukkit.tc.signactions.spawner.SpawnSign;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

public class SignActionSpawn
extends TrainCartsSignAction {
    private static Map<OfflineBlock, Long> cooldownSpawnTimesBySign = new HashMap<OfflineBlock, Long>();

    public SignActionSpawn() {
        super("spawn");
    }

    @Override
    public boolean canSupportFakeSign(SignActionEvent info) {
        return SpawnSign.SpawnOptions.fromEvent((SignActionEvent)info).autoSpawnInterval == 0L;
    }

    @Override
    public void execute(SignActionEvent info) {
        if (!info.isAction(SignActionType.REDSTONE_ON, SignActionType.REDSTONE_OFF)) {
            return;
        }
        SpawnSign sign = info.getTrainCarts().getSpawnSignManager().create(info);
        if (sign.isActive()) {
            sign.spawn(info);
            sign.resetSpawnTime();
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        SignBuildOptions buildOpts = SignBuildOptions.create().setPermission(Permission.BUILD_SPAWNER).setName("train spawner").setDescription("spawn trains on the tracks above when powered by redstone").setTraincartsWIKIHelp("TrainCarts/Signs/Spawner");
        if (!buildOpts.checkBuildPermission(event.getPlayer())) {
            return false;
        }
        SpawnSign sign = event.getTrainCarts().getSpawnSignManager().create(event);
        if (sign.hasInterval() && !Permission.SPAWNER_AUTOMATIC.handleMsg((CommandSender)event.getPlayer(), ChatColor.RED + "You do not have permission to use automatic signs")) {
            sign.remove();
            return false;
        }
        if (!sign.getSpawnableGroup().checkSpawnPermissions((CommandSender)event.getPlayer())) {
            Localization.SPAWN_FORBIDDEN_CONTENTS.message((CommandSender)event.getPlayer(), new String[0]);
            sign.remove();
            return false;
        }
        if (event.isInteractive()) {
            buildOpts.showBuildMessage(event.getPlayer());
            if (sign.hasInterval()) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "This spawner will automatically spawn trains every " + Util.getTimeString(sign.getInterval()) + " while powered");
            }
        }
        return true;
    }

    @Override
    public void destroy(SignActionEvent info) {
        info.getTrainCarts().getSpawnSignManager().remove(info);
    }

    public static SpawnableGroup.SpawnLocationList spawn(SpawnSign spawnSign, SignActionEvent info) {
        if ((info.isTrainSign() || info.isCartSign()) && info.hasRails()) {
            SpawnableGroup.SpawnLocationList spawnOpposite;
            Vector opposite;
            boolean useCentering;
            Vector spawnDirection;
            SpawnableGroup spawnable = spawnSign.getSpawnableGroup();
            if (spawnable.getMembers().isEmpty()) {
                return null;
            }
            if (TCConfig.maxCartsPerTrain >= 0 && spawnable.getMembers().size() > TCConfig.maxCartsPerTrain) {
                spawnSign.showFailParticles(Color.MAROON);
                return null;
            }
            if (spawnable.isExceedingSpawnLimit()) {
                spawnSign.showFailParticles(Color.RED);
                return null;
            }
            if (MinecartGroupStore.isPerWorldSpawnLimitReached(spawnSign.getLocation().getLoadedBlock(), spawnable.getMembers().size())) {
                spawnSign.showFailParticles(Color.ORANGE);
                return null;
            }
            if (TCConfig.spawnSignCooldown >= 0.0) {
                Long lastSpawnTime = cooldownSpawnTimesBySign.get(spawnSign.getLocation());
                long cooldown = (long)(TCConfig.spawnSignCooldown * 1000.0);
                long now = System.currentTimeMillis();
                if (lastSpawnTime != null && now - lastSpawnTime < cooldown) {
                    spawnSign.showFailParticles(Color.YELLOW);
                    return null;
                }
                cooldownSpawnTimesBySign.put(spawnSign.getLocation(), now);
            }
            RailState state = RailState.getSpawnState(info.getRailPiece());
            Vector railDirection = state.motionVector();
            boolean spawnA = info.isWatchedDirection(railDirection.clone().multiply(-1.0));
            boolean spawnB = info.isWatchedDirection(railDirection);
            boolean isBothDirections = spawnA && spawnB;
            if (isBothDirections) {
                BlockFace face = Util.vecToFace(railDirection, false);
                spawnA = info.isPowered(face);
                spawnB = info.isPowered(face.getOppositeFace());
            }
            if (spawnA && !spawnB) {
                spawnDirection = railDirection;
                useCentering = false;
            } else if (!spawnA && spawnB) {
                spawnDirection = railDirection.clone().multiply(-1.0);
                useCentering = false;
            } else {
                Vector facingDir;
                spawnDirection = FaceUtil.isVertical((BlockFace)Util.vecToFace(railDirection, false)) ? (railDirection.getY() < 0.0 ? railDirection : railDirection.clone().multiply(-1.0)) : (railDirection.dot(facingDir = FaceUtil.faceToVector((BlockFace)FaceUtil.rotate((BlockFace)info.getFacing(), (int)-2))) >= 0.0 ? railDirection : railDirection.clone().multiply(-1.0));
                useCentering = true;
            }
            if (spawnable.getCenterMode() == SpawnableGroup.CenterMode.MIDDLE) {
                useCentering = true;
            } else if (spawnable.getCenterMode() == SpawnableGroup.CenterMode.LEFT || spawnable.getCenterMode() == SpawnableGroup.CenterMode.RIGHT) {
                useCentering = false;
            }
            SpawnableGroup.SpawnMode directionalSpawnMode = SpawnableGroup.SpawnMode.DEFAULT;
            if (spawnable.getCenterMode() == SpawnableGroup.CenterMode.LEFT) {
                directionalSpawnMode = SpawnableGroup.SpawnMode.REVERSE;
            }
            SpawnableGroup.SpawnLocationList spawnLocations = null;
            if (useCentering && (spawnLocations = spawnable.findSpawnLocations(info.getRailPiece(), spawnDirection, SpawnableGroup.SpawnMode.CENTER)) != null && !spawnLocations.can_move) {
                opposite = spawnDirection.clone().multiply(-1.0);
                spawnOpposite = spawnable.findSpawnLocations(info.getRailPiece(), opposite, SpawnableGroup.SpawnMode.CENTER);
                if (spawnOpposite != null && spawnOpposite.can_move) {
                    spawnDirection = opposite;
                    spawnLocations = spawnOpposite;
                }
            }
            if (spawnLocations == null) {
                spawnLocations = spawnable.findSpawnLocations(info.getRailPiece(), spawnDirection, directionalSpawnMode);
            }
            if (spawnLocations == null || !spawnLocations.can_move && isBothDirections) {
                opposite = spawnDirection.clone().multiply(-1.0);
                spawnOpposite = spawnable.findSpawnLocations(info.getRailPiece(), opposite, directionalSpawnMode);
                if (spawnOpposite != null && (spawnLocations == null || spawnOpposite.can_move)) {
                    spawnDirection = opposite;
                    spawnLocations = spawnOpposite;
                }
            }
            if (spawnLocations == null && !useCentering) {
                spawnLocations = spawnable.findSpawnLocations(info.getRailPiece(), spawnDirection, SpawnableGroup.SpawnMode.CENTER);
            }
            if (spawnLocations == null) {
                spawnSign.showFailParticles(Color.BLUE);
                return null;
            }
            spawnLocations.loadChunks();
            if (spawnLocations.isOccupied()) {
                spawnSign.showFailParticles(Color.PURPLE);
                return null;
            }
            MinecartGroup group = spawnable.spawn(spawnLocations);
            double spawnForce = spawnSign.getSpawnForce();
            if (group != null && spawnForce != 0.0) {
                Vector headDirection = spawnLocations.locations.get((int)(spawnLocations.locations.size() - 1)).forward;
                BlockFace launchDirection = Util.vecToFace(headDirection, false);
                if (spawnForce < 0.0) {
                    launchDirection = launchDirection.getOppositeFace();
                    spawnForce = -spawnForce;
                }
                group.head().getActions().addActionLaunch(launchDirection, 2.0, spawnForce);
            }
            return spawnLocations;
        }
        return null;
    }

    @Deprecated
    public static List<Location> getSpawnPositions(Location startLoc, boolean atCenter, BlockFace directionFace, List<SpawnableMember> types) {
        return SignActionSpawn.getSpawnPositions(startLoc, atCenter, FaceUtil.faceToVector((BlockFace)directionFace), types);
    }

    @Deprecated
    public static List<Location> getSpawnPositions(Location startLoc, boolean atCenter, Vector direction, List<SpawnableMember> types) {
        ArrayList<Location> result;
        block2: {
            block1: {
                result = new ArrayList<Location>(types.size());
                if (!atCenter || types.size() != 1) break block1;
                if (MinecartMemberStore.getAt(startLoc) != null) break block2;
                TrackWalkingPoint walker = new TrackWalkingPoint(startLoc, direction);
                Location firstPos = walker.state.positionLocation();
                walker.skipFirst();
                if (!walker.moveFull()) break block2;
                result.add(firstPos);
                break block2;
            }
            TrackWalkingPoint walker = new TrackWalkingPoint(startLoc, direction);
            walker.skipFirst();
            for (int i = 0; i < types.size(); ++i) {
                SpawnableMember type = types.get(i);
                if (atCenter && i == 0 ? !walker.move(0.0) : !walker.move(0.5 * type.getLength() - (i == 0 ? 0.5 : 0.0))) break;
                result.add(walker.state.positionLocation());
                if (i == types.size() - 1) break;
                double cartGap = type.getCartCouplerLength() + types.get(i + 1).getCartCouplerLength();
                if (!walker.move(0.5 * type.getLength() + cartGap)) break;
            }
        }
        return result;
    }
}

