/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.spawnable;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.Permission;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableMember;
import com.bergerkiller.bukkit.tc.controller.spawnable.TrainSpawnPattern;
import com.bergerkiller.bukkit.tc.properties.SavedTrainProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import com.bergerkiller.bukkit.tc.properties.defaults.DefaultProperties;
import com.bergerkiller.bukkit.tc.properties.standard.StandardProperties;
import com.bergerkiller.bukkit.tc.properties.standard.type.TrainNameFormat;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpawnableGroup
implements TrainCarts.Provider {
    private final TrainCarts plugin;
    private final List<SpawnableMember> members = new ArrayList<SpawnableMember>();
    private final ConfigurationNode config;
    private CenterMode centerMode = CenterMode.NONE;
    private static final double CAN_MOVE_DISTANCE = 2.0;

    @Deprecated
    public SpawnableGroup() {
        this(TrainCarts.plugin);
    }

    public SpawnableGroup(TrainCarts plugin) {
        this.plugin = plugin;
        this.config = new ConfigurationNode();
    }

    @Override
    public TrainCarts getTrainCarts() {
        return this.plugin;
    }

    @Deprecated
    public TrainCarts getPlugin() {
        return this.plugin;
    }

    public ConfigurationNode getConfig() {
        return this.config;
    }

    public TrainNameFormat getNameFormat() {
        return StandardProperties.TRAIN_NAME_FORMAT.readFromConfig(this.config).orElse(StandardProperties.TRAIN_NAME_FORMAT.getDefault());
    }

    public String getSavedName() {
        if (this.config.contains("savedName")) {
            return (String)this.config.get("savedName", (Object)"dummyname");
        }
        return this.getNameFormat().generate(1);
    }

    public CenterMode getCenterMode() {
        return this.centerMode;
    }

    public void setCenterMode(CenterMode mode) {
        this.centerMode = mode;
    }

    public List<SpawnableMember> getMembers() {
        return this.members;
    }

    public SpawnableMember addMember(ConfigurationNode config) {
        SpawnableMember newMember = new SpawnableMember(this, config.clone());
        this.members.add(newMember);
        return newMember;
    }

    public SpawnableMember addMember(SpawnableMember member) {
        SpawnableMember newMember = member.cloneWithGroup(this);
        this.members.add(newMember);
        return newMember;
    }

    public ConfigurationNode getFullConfig() {
        ConfigurationNode fullConfig = this.config.clone();
        List cartConfigList = fullConfig.getNodeList("carts");
        for (int i = this.members.size() - 1; i >= 0; --i) {
            cartConfigList.add(this.members.get(i).getConfig().clone());
        }
        return fullConfig;
    }

    public List<SavedTrainProperties> getActiveSavedTrainSpawnLimits() {
        Optional<List<String>> names = StandardProperties.ACTIVE_SAVED_TRAIN_SPAWN_LIMITS.readFromConfig(this.config);
        if (names.isPresent()) {
            ArrayList<SavedTrainProperties> propsList = new ArrayList<SavedTrainProperties>(names.get().size());
            for (String name : names.get()) {
                SavedTrainProperties props = this.getTrainCarts().getSavedTrains().getProperties(name);
                if (props == null || props.getSpawnLimit() < 0) continue;
                propsList.add(props);
            }
            return Collections.unmodifiableList(propsList);
        }
        return Collections.emptyList();
    }

    public boolean isExceedingSpawnLimit() {
        Optional<List<String>> names = StandardProperties.ACTIVE_SAVED_TRAIN_SPAWN_LIMITS.readFromConfig(this.config);
        if (names.isPresent()) {
            for (String name : names.get()) {
                int limit;
                SavedTrainProperties props = this.getTrainCarts().getSavedTrains().getProperties(name);
                if (props == null || (limit = props.getSpawnLimit()) < 0 || props.getSpawnLimitCurrentCount() < limit) continue;
                return true;
            }
        }
        return false;
    }

    public List<SpawnableMember> addTrainWithConfig(SavedTrainProperties savedTrainProperties) {
        if (savedTrainProperties == null || savedTrainProperties.isEmpty()) {
            return Collections.emptyList();
        }
        List<SpawnableMember> addedMembers = this.addTrainWithConfig(savedTrainProperties.getConfig());
        if (!addedMembers.isEmpty() && savedTrainProperties.getSpawnLimit() >= 0) {
            StandardProperties.ACTIVE_SAVED_TRAIN_SPAWN_LIMITS.addSavedTrainToConfig(this.config, savedTrainProperties.getName());
        }
        return addedMembers;
    }

    public List<SpawnableMember> addTrainWithConfig(ConfigurationNode savedConfig) {
        for (String key : savedConfig.getKeys()) {
            if (key.equals("carts")) continue;
            this.config.set(key, savedConfig.get(key));
        }
        List cartConfigList = savedConfig.getNodeList("carts");
        ArrayList<SpawnableMember> newMembers = new ArrayList<SpawnableMember>(cartConfigList.size());
        for (int i = cartConfigList.size() - 1; i >= 0; --i) {
            newMembers.add(this.addMember((ConfigurationNode)cartConfigList.get(i)));
        }
        return newMembers;
    }

    public double getTotalLength() {
        if (this.members.isEmpty()) {
            return 0.0;
        }
        boolean first = true;
        double totalLength = 0.0;
        double previousCartCouplerLength = 0.0;
        for (SpawnableMember member : this.members) {
            if (first) {
                first = false;
            } else {
                totalLength += previousCartCouplerLength + member.getCartCouplerLength();
            }
            previousCartCouplerLength = member.getCartCouplerLength();
            totalLength += member.getLength();
        }
        return totalLength;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("{").append("center=").append((Object)this.centerMode);
        str.append(", types=[");
        boolean first = true;
        for (SpawnableMember member : this.members) {
            if (first) {
                first = false;
            } else {
                str.append(", ");
            }
            str.append(member.toString());
        }
        str.append("]}");
        return str.toString();
    }

    public SpawnLocationList findSpawnLocations(Block startRailBlock, Vector forwardDirection, SpawnMode mode) {
        return this.findSpawnLocations(RailPiece.create(startRailBlock), forwardDirection, mode);
    }

    public SpawnLocationList findSpawnLocations(RailPiece startRails, Vector forwardDirection, SpawnMode mode) {
        if (startRails == null || startRails.isNone()) {
            return null;
        }
        RailState state = RailState.getSpawnState(startRails);
        if (state.motionVector().dot(forwardDirection) < 0.0) {
            state.position().invertMotion();
        }
        return this.findSpawnLocations(state, mode);
    }

    public SpawnLocationList findSpawnLocations(Location startLocation, Vector forwardDirection, SpawnMode mode) {
        RailPiece piece = RailType.findRailPiece(startLocation);
        if (piece == null || piece.isNone()) {
            return null;
        }
        RailState state = new RailState();
        state.setRailPiece(piece);
        state.position().setLocation(startLocation);
        RailType.loadRailInformation(state);
        state.setMotionVector(forwardDirection);
        state.loadRailLogic().getPath().snap(state.position(), state.railBlock());
        return this.findSpawnLocations(state, mode);
    }

    public SpawnLocationList findSpawnLocations(RailState startState, SpawnMode mode) {
        boolean edgeAtStart;
        if (this.members.isEmpty()) {
            return null;
        }
        if (startState.railType() == RailType.NONE) {
            return null;
        }
        boolean bl = edgeAtStart = mode == SpawnMode.DEFAULT_EDGE || mode == SpawnMode.REVERSE_EDGE;
        if (!edgeAtStart && this.members.size() == 1) {
            SpawnLocationList result = new SpawnLocationList();
            result.addMember(this.members.get(0), startState.motionVector(), startState.positionLocation());
            result.endState = startState.clone();
            TrackWalkingPoint walker = new TrackWalkingPoint(startState);
            walker.skipFirst();
            result.can_move = walker.move(0.5 * this.members.get(0).getLength() + 2.0);
            return result;
        }
        if (mode == SpawnMode.CENTER) {
            SpawnableMember member;
            int i;
            double halfLength = 0.5 * this.getTotalLength();
            if (halfLength < 1.0E-10) {
                SpawnLocationList result = new SpawnLocationList();
                Vector forward = startState.motionVector();
                Location location = startState.positionLocation();
                for (SpawnableMember member2 : this.members) {
                    result.addMember(member2, forward, location);
                }
                result.endState = startState.clone();
                TrackWalkingPoint walker = new TrackWalkingPoint(startState);
                walker.skipFirst();
                result.can_move = walker.move(2.0);
                return result;
            }
            ArrayList<SpawnableMember> backward = new ArrayList<SpawnableMember>(this.members.size());
            ArrayList<SpawnableMember> forward = new ArrayList<SpawnableMember>(this.members.size());
            double backwardOffset = 0.0;
            double forwardOffset = 0.0;
            double accumLength = 0.0;
            double prevMemberLengthWithCoupler = Double.NaN;
            boolean isForwardPortion = false;
            for (SpawnableMember member3 : this.members) {
                double distanceBeyondHalf;
                if (isForwardPortion) {
                    forward.add(member3);
                    continue;
                }
                double memberStartLength = 0.5 * member3.getLength();
                if (!Double.isNaN(prevMemberLengthWithCoupler)) {
                    memberStartLength += member3.getCartCouplerLength() + prevMemberLengthWithCoupler;
                }
                if ((distanceBeyondHalf = accumLength + memberStartLength - halfLength) >= 0.0) {
                    backwardOffset = halfLength - accumLength;
                    forwardOffset = distanceBeyondHalf;
                    isForwardPortion = true;
                    forward.add(member3);
                    continue;
                }
                accumLength += memberStartLength;
                prevMemberLengthWithCoupler = 0.5 * member3.getLength() + member3.getCartCouplerLength();
                backward.add(member3);
            }
            if (!isForwardPortion) {
                backwardOffset = halfLength - accumLength;
                forwardOffset = halfLength;
            }
            Collections.reverse(backward);
            SpawnLocationList result = new SpawnLocationList();
            TrackWalkingPoint walker = new TrackWalkingPoint(startState.cloneAndInvertMotion());
            walker.skipFirst();
            if (!walker.move(backwardOffset)) {
                return null;
            }
            for (i = 0; i < backward.size(); ++i) {
                member = (SpawnableMember)backward.get(i);
                if (i > 0 && !walker.move(0.5 * member.getLength())) {
                    return null;
                }
                result.addMember(member, walker.state.motionVector().multiply(-1.0), Util.invertRotation(walker.state.positionLocation()));
                double gap = 0.0;
                if (i < backward.size() - 1) {
                    gap = member.getCartCouplerLength() + ((SpawnableMember)backward.get(i + 1)).getCartCouplerLength();
                }
                if (walker.move(0.5 * member.getLength() + gap)) continue;
                return null;
            }
            Collections.reverse(result.locations);
            walker = new TrackWalkingPoint(startState);
            walker.skipFirst();
            if (!walker.move(forwardOffset)) {
                return null;
            }
            for (i = 0; i < forward.size(); ++i) {
                member = (SpawnableMember)forward.get(i);
                if (i > 0 && !walker.move(0.5 * member.getLength())) {
                    return null;
                }
                result.addMember(member, walker.state.motionVector(), walker.state.positionLocation());
                double gap = 0.0;
                if (i < forward.size() - 1) {
                    gap = member.getCartCouplerLength() + ((SpawnableMember)forward.get(i + 1)).getCartCouplerLength();
                }
                if (walker.move(0.5 * member.getLength() + gap)) continue;
                return null;
            }
            result.endState = walker.state.clone();
            result.can_move = walker.move(2.0);
            return result;
        }
        if (mode == SpawnMode.DEFAULT || mode == SpawnMode.DEFAULT_EDGE) {
            SpawnLocationList result = new SpawnLocationList();
            TrackWalkingPoint walker = new TrackWalkingPoint(startState);
            walker.skipFirst();
            for (int i = 0; i < this.members.size(); ++i) {
                SpawnableMember member = this.members.get(i);
                if (!edgeAtStart && i == 0 ? !walker.move(0.0) : !walker.move(0.5 * member.getLength())) {
                    return null;
                }
                result.addMember(member, walker.state.motionVector(), walker.state.positionLocation());
                double gap = 0.0;
                if (i < this.members.size() - 1) {
                    gap = member.getCartCouplerLength() + this.members.get(i + 1).getCartCouplerLength();
                }
                if (walker.move(0.5 * member.getLength() + gap)) continue;
                return null;
            }
            result.endState = walker.state.clone();
            result.can_move = walker.move(2.0);
            return result;
        }
        if (mode == SpawnMode.REVERSE || mode == SpawnMode.REVERSE_EDGE) {
            SpawnLocationList result = new SpawnLocationList();
            TrackWalkingPoint walker = new TrackWalkingPoint(startState.cloneAndInvertMotion());
            walker.skipFirst();
            for (int i = this.members.size() - 1; i >= 0; --i) {
                SpawnableMember member = this.members.get(i);
                if (!edgeAtStart && i == this.members.size() - 1 ? !walker.move(0.0) : !walker.move(0.5 * member.getLength())) {
                    return null;
                }
                result.addMember(member, walker.state.motionVector().multiply(-1.0), Util.invertRotation(walker.state.positionLocation()));
                double gap = 0.0;
                if (i > 0) {
                    gap = member.getCartCouplerLength() + this.members.get(i - 1).getCartCouplerLength();
                }
                if (walker.move(0.5 * member.getLength() + gap)) continue;
                return null;
            }
            Collections.reverse(result.locations);
            result.endState = walker.state.clone();
            result.can_move = walker.move(2.0);
            return result;
        }
        return null;
    }

    public MinecartGroup spawn(SpawnLocationList spawnLocations) {
        return MinecartGroupStore.spawn(this, spawnLocations);
    }

    public MinecartGroup spawn(SpawnLocationList spawnLocations, double initialSpeed) {
        return MinecartGroupStore.spawn(this, spawnLocations, initialSpeed);
    }

    @Deprecated
    public static SpawnableGroup fromConfig(ConfigurationNode savedConfig) {
        return SpawnableGroup.fromConfig(TrainCarts.plugin, savedConfig);
    }

    public static SpawnableGroup fromConfig(SavedTrainProperties savedTrainProperties) {
        SpawnableGroup result = new SpawnableGroup(savedTrainProperties.getTrainCarts());
        result.addTrainWithConfig(savedTrainProperties);
        return result;
    }

    public static SpawnableGroup fromConfig(TrainCarts plugin, ConfigurationNode savedConfig) {
        SpawnableGroup result = new SpawnableGroup(plugin);
        result.addTrainWithConfig(savedConfig);
        return result;
    }

    public boolean checkSpawnPermissions(CommandSender sender) {
        boolean canHaveItems = false;
        for (SpawnableMember member : this.getMembers()) {
            if (!member.getPermission().handleMsg(sender, Localization.SPAWN_DISALLOWED_TYPE.get(member.toString()))) {
                return false;
            }
            if (canHaveItems || !member.hasInventoryItems() || (canHaveItems = Permission.SPAWNER_INVENTORY_ITEMS.has(sender))) continue;
            Localization.SPAWN_DISALLOWED_INVENTORY.message(sender, new String[0]);
            return false;
        }
        DefaultProperties defaults = sender instanceof Player ? TrainPropertiesStore.getDefaultsByPlayer((Player)sender) : TrainPropertiesStore.getDefaultsByName("default");
        return defaults.checkSavedTrainPermissions(sender, this);
    }

    @Deprecated
    public static SpawnableGroup parse(String typesText) {
        return SpawnableGroup.parse(TrainCarts.plugin, typesText);
    }

    public static SpawnableGroup parse(TrainCarts plugin, String typesText) {
        Function<String, String> savedTrainMatcher = name -> plugin.getSavedTrains().findName((String)name);
        TrainSpawnPattern.ParsedSpawnPattern pattern = TrainSpawnPattern.parse(typesText, savedTrainMatcher);
        SpawnableGroup result = new SpawnableGroup(plugin);
        result.setCenterMode(pattern.centerMode());
        try {
            pattern.newGroupApplier().apply(result, new Random(), savedTrainMatcher);
        }
        catch (TrainSpawnPattern.TrainTooLongException trainTooLongException) {
            // empty catch block
        }
        return result;
    }

    @Deprecated
    public static SpawnableGroup ofMembers(Iterable<SpawnableMember> members) {
        return SpawnableGroup.ofMembers(TrainCarts.plugin, members);
    }

    public static SpawnableGroup ofMembers(TrainCarts plugin, Iterable<SpawnableMember> members) {
        SpawnableGroup group = new SpawnableGroup(plugin);
        for (SpawnableMember member : members) {
            group.addMember(member);
        }
        return group;
    }

    public static enum CenterMode {
        NONE,
        MIDDLE,
        LEFT,
        RIGHT;


        public CenterMode next(CenterMode adjusted) {
            if (this == NONE || this == adjusted) {
                return adjusted;
            }
            return MIDDLE;
        }
    }

    public static enum SpawnMode {
        DEFAULT,
        REVERSE,
        DEFAULT_EDGE,
        REVERSE_EDGE,
        CENTER;


        public boolean isReverseOrder() {
            switch (this.ordinal()) {
                case 1: 
                case 3: {
                    return true;
                }
            }
            return false;
        }
    }

    public static final class SpawnLocationList {
        public final List<SpawnableMember.SpawnLocation> locations = new ArrayList<SpawnableMember.SpawnLocation>();
        public RailState endState;
        public boolean can_move = true;

        public void addMember(SpawnableMember member, Vector forward, Location location) {
            this.locations.add(new SpawnableMember.SpawnLocation(member, forward, location));
        }

        public void loadChunks() {
            for (SpawnableMember.SpawnLocation loc : this.locations) {
                WorldUtil.loadChunks((Location)loc.location, (int)2);
            }
        }

        public List<OccupiedLocation> getOccupiedLocations() {
            List<OccupiedLocation> occupying = Collections.emptyList();
            for (SpawnableMember.SpawnLocation loc : this.locations) {
                MinecartMember<?> member = MinecartMemberStore.getAt(loc.location);
                if (member == null || member.isUnloaded() || ((CommonMinecart)member.getEntity()).isRemoved()) continue;
                if (occupying.isEmpty()) {
                    occupying = new ArrayList<OccupiedLocation>();
                }
                occupying.add(new OccupiedLocation(loc, member));
            }
            return occupying;
        }

        public boolean isOccupied() {
            return !this.getOccupiedLocations().isEmpty();
        }
    }

    public static class OccupiedLocation {
        public final SpawnableMember.SpawnLocation spawnLocation;
        public final MinecartMember<?> member;

        public OccupiedLocation(SpawnableMember.SpawnLocation spawnLocation, MinecartMember<?> member) {
            this.spawnLocation = spawnLocation;
            this.member = member;
        }
    }

    public static enum VanillaCartType {
        RIDEABLE('m', EntityType.MINECART),
        STORAGE('s', EntityType.MINECART_CHEST),
        POWERED('p', EntityType.MINECART_FURNACE),
        HOPPER('h', EntityType.MINECART_HOPPER),
        TNT('t', EntityType.MINECART_TNT);

        private final char code;
        private final EntityType type;

        private VanillaCartType(char code, EntityType type) {
            this.code = code;
            this.type = type;
        }

        public char getCode() {
            return this.code;
        }

        public EntityType getType() {
            return this.type;
        }

        public String toString() {
            return Character.toString(this.code);
        }

        public static Optional<VanillaCartType> parse(char c) {
            c = Character.toLowerCase(c);
            for (VanillaCartType type : VanillaCartType.values()) {
                if (type.getCode() != c) continue;
                return Optional.of(type);
            }
            return Optional.empty();
        }
    }
}

