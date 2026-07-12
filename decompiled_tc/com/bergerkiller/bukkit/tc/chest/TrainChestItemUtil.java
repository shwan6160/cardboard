/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.config.BasicConfiguration
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.inventory.CommonItemStack
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.PlayerUtil
 *  com.bergerkiller.bukkit.common.wrappers.ChatText
 *  com.google.common.io.ByteStreams
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.chest;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.config.BasicConfiguration;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.tc.Localization;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.chest.TrainChestExtendableTrain;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartGroupStore;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.components.RailPath;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableMember;
import com.bergerkiller.bukkit.tc.debug.DebugToolUtil;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class TrainChestItemUtil {
    private static final String IDENTIFIER = "Traincarts.chest";
    private static final String TITLE = "Traincarts Chest";
    private static final boolean CAN_USE_NEW_BKCL_ITEM_APIS = Common.hasCapability((String)"Common:CommonItemStack:AddGlint");
    private static final double AUTOCONNECT_EXTRA_DISTANCE = 1.0;
    private static final double SPAWN_LOOKING_AT_REACH = 10.0;

    public static ItemStack createItem() {
        CommonItemStack item = CommonItemStack.create((Material)Material.ENDER_CHEST, (int)1).updateCustomData(tag -> {
            tag.putValue("plugin", (Object)TrainCarts.plugin.getName());
            tag.putValue("identifier", (Object)IDENTIFIER);
            tag.putValue("name", (Object)"");
            tag.putValue("parsed", (Object)false);
            tag.putValue("locked", (Object)false);
            tag.putValue("HideFlags", (Object)1);
        }).hideAllAttributes();
        if (CAN_USE_NEW_BKCL_ITEM_APIS) {
            TrainChestItemUtil.applyNewBKCLChanges(item);
        } else {
            item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        }
        TrainChestItemUtil.updateTitle(item);
        return item.toBukkit();
    }

    private static void applyNewBKCLChanges(CommonItemStack item) {
        item.addGlint().mimicAsType(MaterialUtil.getFirst((String[])new String[]{"PAPER", "LEGACY_PAPER"}));
    }

    private static void updateTitle(CommonItemStack item) {
        String displayTitle = TITLE;
        String name = TrainChestItemUtil.getName(item);
        if (name.isEmpty() && !TrainChestItemUtil.isEmpty(item) && ((Boolean)item.getCustomData().getValue("parsed", (Object)false)).booleanValue()) {
            name = (String)item.getCustomData().getValue("config", (Object)"");
        }
        if (!name.isEmpty()) {
            displayTitle = displayTitle + " (" + name + ")";
        }
        item.setCustomNameMessage(displayTitle);
        item.clearLores();
        if (TrainChestItemUtil.isEmpty(item)) {
            item.addLore(ChatText.fromMessage((String)(ChatColor.RED + "Empty")));
        } else if (TrainChestItemUtil.isFiniteSpawns(item)) {
            item.addLore(ChatText.fromMessage((String)(ChatColor.BLUE + "Single-use")));
        } else {
            item.addLore(ChatText.fromMessage((String)(ChatColor.DARK_PURPLE + "Infinite uses")));
        }
        double speed = TrainChestItemUtil.getSpeed(item);
        if (speed > 0.0) {
            item.addLore(ChatText.fromMessage((String)(ChatColor.YELLOW + "Speed " + DebugToolUtil.formatNumber(speed) + "b/t")));
        }
        if (TrainChestItemUtil.isLocked(item)) {
            item.addLore(ChatText.fromMessage((String)(ChatColor.RED + "Locked")));
        }
    }

    public static boolean isItem(ItemStack item) {
        return TrainChestItemUtil.isItem(CommonItemStack.of((ItemStack)item));
    }

    public static boolean isItem(CommonItemStack item) {
        if (!item.isEmpty() && item.hasCustomData()) {
            return IDENTIFIER.equals(item.getCustomData().getValue("identifier", (Object)""));
        }
        return false;
    }

    public static void setFiniteSpawns(ItemStack item, boolean finite) {
        TrainChestItemUtil.setFiniteSpawns(CommonItemStack.of((ItemStack)item), finite);
    }

    public static void setFiniteSpawns(CommonItemStack item, boolean finite) {
        if (TrainChestItemUtil.isItem(item)) {
            item.updateCustomData(tag -> tag.putValue("finite", (Object)finite));
            TrainChestItemUtil.updateTitle(item);
        }
    }

    public static void setLocked(ItemStack item, boolean locked) {
        TrainChestItemUtil.setLocked(CommonItemStack.of((ItemStack)item), locked);
    }

    public static void setLocked(CommonItemStack item, boolean locked) {
        if (TrainChestItemUtil.isItem(item)) {
            item.updateCustomData(tag -> tag.putValue("locked", (Object)locked));
            TrainChestItemUtil.updateTitle(item);
        }
    }

    public static void setSpeed(ItemStack item, double speed) {
        TrainChestItemUtil.setSpeed(CommonItemStack.of((ItemStack)item), speed);
    }

    public static void setSpeed(CommonItemStack item, double speed) {
        if (TrainChestItemUtil.isItem(item)) {
            item.updateCustomData(tag -> tag.putValue("speed", (Object)speed));
            TrainChestItemUtil.updateTitle(item);
        }
    }

    public static void setSpawnMessage(ItemStack item, String message) {
        TrainChestItemUtil.setSpawnMessage(CommonItemStack.of((ItemStack)item), message);
    }

    public static void setSpawnMessage(CommonItemStack item, String message) {
        if (TrainChestItemUtil.isItem(item)) {
            item.updateCustomData(tag -> tag.putValue("spawnMessage", (Object)message));
        }
    }

    public static String getSpawnMessage(ItemStack item) {
        return TrainChestItemUtil.getSpawnMessage(CommonItemStack.of((ItemStack)item));
    }

    public static String getSpawnMessage(CommonItemStack item) {
        return TrainChestItemUtil.isItem(item) ? (String)item.getCustomData().getValue("spawnMessage", String.class, null) : null;
    }

    public static boolean isLocked(ItemStack item) {
        return TrainChestItemUtil.isLocked(CommonItemStack.of((ItemStack)item));
    }

    public static boolean isLocked(CommonItemStack item) {
        return TrainChestItemUtil.isItem(item) && (Boolean)item.getCustomData().getValue("locked", (Object)false) != false;
    }

    public static boolean isFiniteSpawns(ItemStack item) {
        return TrainChestItemUtil.isFiniteSpawns(CommonItemStack.of((ItemStack)item));
    }

    public static boolean isFiniteSpawns(CommonItemStack item) {
        return TrainChestItemUtil.isItem(item) && (Boolean)item.getCustomData().getValue("finite", (Object)false) != false;
    }

    public static double getSpeed(ItemStack item) {
        return TrainChestItemUtil.getSpeed(CommonItemStack.of((ItemStack)item));
    }

    public static double getSpeed(CommonItemStack item) {
        return TrainChestItemUtil.isItem(item) ? (Double)item.getCustomData().getValue("speed", (Object)0.0) : 0.0;
    }

    public static void setName(ItemStack item, String name) {
        TrainChestItemUtil.setName(CommonItemStack.of((ItemStack)item), name);
    }

    public static void setName(CommonItemStack item, String name) {
        if (TrainChestItemUtil.isItem(item)) {
            item.updateCustomData(tag -> tag.putValue("name", (Object)name));
            TrainChestItemUtil.updateTitle(item);
        }
    }

    public static String getName(ItemStack item) {
        return TrainChestItemUtil.getName(CommonItemStack.of((ItemStack)item));
    }

    public static String getName(CommonItemStack item) {
        return TrainChestItemUtil.isItem(item) ? (String)item.getCustomData().getValue("name", (Object)"") : "";
    }

    public static void clear(ItemStack item) {
        TrainChestItemUtil.clear(CommonItemStack.of((ItemStack)item));
    }

    public static void clear(CommonItemStack item) {
        if (TrainChestItemUtil.isItem(item)) {
            item.updateCustomData(tag -> tag.remove((Object)"config"));
            TrainChestItemUtil.updateTitle(item);
        }
    }

    public static boolean isEmpty(ItemStack item) {
        return TrainChestItemUtil.isEmpty(CommonItemStack.of((ItemStack)item));
    }

    public static boolean isEmpty(CommonItemStack item) {
        return TrainChestItemUtil.isItem(item) && !item.getCustomData().containsKey((Object)"config");
    }

    public static void playSoundStore(Player player) {
        PlayerUtil.playSound((Player)player, (ResourceKey)SoundEffect.PISTON_CONTRACT, (float)0.4f, (float)1.5f);
    }

    public static void playSoundSpawn(Player player) {
        PlayerUtil.playSound((Player)player, (ResourceKey)SoundEffect.PISTON_EXTEND, (float)0.4f, (float)1.5f);
    }

    public static void store(ItemStack item, String spawnPattern) {
        TrainChestItemUtil.store(CommonItemStack.of((ItemStack)item), spawnPattern);
    }

    public static void store(CommonItemStack item, String spawnPattern) {
        if (TrainChestItemUtil.isItem(item)) {
            item.updateCustomData(tag -> {
                tag.putValue("config", (Object)spawnPattern);
                tag.putValue("parsed", (Object)true);
            });
            TrainChestItemUtil.updateTitle(item);
        }
    }

    public static void store(ItemStack item, MinecartGroup group) {
        TrainChestItemUtil.store(CommonItemStack.of((ItemStack)item), group);
    }

    public static void store(CommonItemStack item, MinecartGroup group) {
        if (group != null) {
            TrainChestItemUtil.store(item, group.saveConfig());
        }
    }

    public static void store(ItemStack item, ConfigurationNode config) {
        TrainChestItemUtil.store(CommonItemStack.of((ItemStack)item), config);
    }

    public static void store(CommonItemStack item, ConfigurationNode config) {
        if (TrainChestItemUtil.isItem(item)) {
            item.updateCustomData(tag -> {
                byte[] compressed = new byte[]{};
                try {
                    byte[] uncompressed = config.toString().getBytes("UTF-8");
                    try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(uncompressed.length);){
                        try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream);){
                            zipStream.write(uncompressed);
                        }
                        compressed = byteStream.toByteArray();
                    }
                }
                catch (Throwable t) {
                    TrainCarts.plugin.getLogger().log(Level.SEVERE, "Unhandled error saving item details to config", t);
                }
                tag.putValue("config", (Object)compressed);
                tag.putValue("parsed", (Object)false);
            });
            TrainChestItemUtil.updateTitle(item);
        }
    }

    public static SpawnableGroup getSpawnableGroup(TrainCarts plugin, ItemStack item) {
        return TrainChestItemUtil.getSpawnableGroup(plugin, CommonItemStack.of((ItemStack)item));
    }

    public static SpawnableGroup getSpawnableGroup(TrainCarts plugin, CommonItemStack item) {
        SpawnableGroup group;
        if (!TrainChestItemUtil.isItem(item)) {
            return null;
        }
        if (TrainChestItemUtil.isEmpty(item)) {
            return null;
        }
        if (((Boolean)item.getCustomData().getValue("parsed", (Object)false)).booleanValue()) {
            group = SpawnableGroup.parse(plugin, (String)item.getCustomData().getValue("config", (Object)""));
        } else {
            BasicConfiguration basicConfig = new BasicConfiguration();
            try {
                byte[] uncompressed = new byte[]{};
                byte[] compressed = (byte[])item.getCustomData().getValue("config", (Object)new byte[0]);
                if (compressed != null && compressed.length > 0) {
                    try (ByteArrayInputStream inByteStream = new ByteArrayInputStream(compressed);
                         GZIPInputStream zipStream = new GZIPInputStream(inByteStream);){
                        uncompressed = ByteStreams.toByteArray((InputStream)zipStream);
                    }
                }
                basicConfig.loadFromStream((InputStream)new ByteArrayInputStream(uncompressed));
            }
            catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Unhandled IO error parsing train chest configuration", ex);
                return null;
            }
            group = SpawnableGroup.fromConfig(plugin, (ConfigurationNode)basicConfig);
        }
        if (group.getMembers().isEmpty()) {
            return null;
        }
        return group;
    }

    public static SpawnResult spawnAtBlock(SpawnableGroup group, Block clickedBlock, SpawnOptions options) {
        if (group == null) {
            return SpawnResult.FAIL_EMPTY;
        }
        if (TCConfig.maxCartsPerTrain >= 0 && group.getMembers().size() > TCConfig.maxCartsPerTrain) {
            return SpawnResult.FAIL_TOO_LONG;
        }
        if (group.isExceedingSpawnLimit()) {
            return SpawnResult.FAIL_LIMIT_REACHED;
        }
        BlockFace orientation = FaceUtil.getDirection((Vector)options.player.getEyeLocation().getDirection());
        RailType clickedRailType = RailType.getType(clickedBlock);
        if (clickedRailType == RailType.NONE) {
            return SpawnResult.FAIL_NORAIL;
        }
        Location spawnLoc = clickedRailType.getSpawnLocation(clickedBlock, orientation);
        if (spawnLoc == null) {
            return SpawnResult.FAIL_NORAIL;
        }
        RailState spawnStartState = new RailState();
        spawnStartState.setRailPiece(RailPiece.create(clickedRailType, clickedBlock));
        spawnStartState.setPosition(RailPath.Position.fromTo(spawnLoc, spawnLoc));
        spawnStartState.setMotionVector(spawnLoc.getDirection());
        spawnStartState.initEnterDirection();
        spawnStartState.loadRailLogic().getPath().move(spawnStartState, 0.0);
        if (spawnStartState.position().motDot(options.player.getEyeLocation().getDirection()) < 0.0) {
            spawnStartState.position().invertMotion();
        }
        Vector spawnDirection = spawnStartState.motionVector();
        Optional<SpawnResult> behindResult = TrainChestItemUtil.trySpawnExtendBehind(group, spawnStartState, options);
        if (behindResult.isPresent()) {
            return behindResult.get();
        }
        if (MinecartGroupStore.isPerWorldSpawnLimitReached(clickedBlock, group.getMembers().size())) {
            return SpawnResult.FAIL_MAX_PER_WORLD;
        }
        SpawnableGroup.SpawnLocationList locationList = group.findSpawnLocations(spawnLoc, spawnDirection, SpawnableGroup.SpawnMode.DEFAULT);
        return TrainChestItemUtil.spawnAtLocations(group, locationList, options);
    }

    public static SpawnResult spawnLookingAt(SpawnableGroup group, Player player, Location eyeLocation, SpawnOptions options) {
        double stepSize = 0.05;
        int steps = 200;
        Vector step = eyeLocation.getDirection().multiply(0.05);
        RailState bestState = null;
        Location pos = eyeLocation.clone();
        RailState tmp = new RailState();
        tmp.setRailPiece(RailPiece.createWorldPlaceholder(eyeLocation.getWorld()));
        double bestDistanceSq = 4.0;
        for (int n = 0; n < 200; ++n) {
            pos.add(step);
            tmp.position().setLocation(pos);
            if (!RailType.loadRailInformation(tmp)) continue;
            tmp.loadRailLogic().getPath().move(tmp, 0.0);
            double dist_sq = tmp.position().distanceSquared(pos);
            if (!(dist_sq < bestDistanceSq)) continue;
            bestDistanceSq = dist_sq;
            bestState = tmp.clone();
        }
        if (bestState == null) {
            return SpawnResult.FAIL_NORAIL_LOOK;
        }
        if (bestState.position().motDot(step) < 0.0) {
            bestState.position().invertMotion();
        }
        bestState.initEnterDirection();
        Optional<SpawnResult> behindResult = TrainChestItemUtil.trySpawnExtendBehind(group, bestState, options);
        if (behindResult.isPresent()) {
            return behindResult.get();
        }
        return TrainChestItemUtil.spawnAtState(group, bestState, options);
    }

    private static Optional<SpawnResult> trySpawnExtendBehind(SpawnableGroup group, RailState spawnStartState, SpawnOptions options) {
        if (options.tryExtendTrains) {
            SpawnableMember lastMember = group.getMembers().get(group.getMembers().size() - 1);
            double searchDistance = 1.0 + 2.0 * lastMember.getCartCouplerLength() + 0.5 * lastMember.getLength();
            TrainChestExtendableTrain extendableTrain = TrainChestExtendableTrain.find(spawnStartState.cloneAndInvertMotion(), searchDistance, lastMember);
            if (extendableTrain != null) {
                options.tryExtendTrains = false;
                options.connectWith = extendableTrain.member;
                options.spawnMode = SpawnableGroup.SpawnMode.DEFAULT_EDGE;
                return Optional.of(TrainChestItemUtil.spawnAtState(group, extendableTrain.startState, options));
            }
        }
        return Optional.empty();
    }

    private static SpawnResult spawnAtLocations(SpawnableGroup group, SpawnableGroup.SpawnLocationList locationList, SpawnOptions options) {
        SpawnableMember firstMember;
        double searchDistance;
        TrainChestExtendableTrain extendableTrain;
        if (locationList == null) {
            return SpawnResult.FAIL_RAILTOOSHORT;
        }
        locationList.loadChunks();
        if (options.tryExtendTrains) {
            List<SpawnableGroup.OccupiedLocation> occupiedLocations = locationList.getOccupiedLocations();
            if (!occupiedLocations.isEmpty()) {
                TrainChestExtendableTrain extendableTrain2;
                if (options.tryExtendTrains && (extendableTrain2 = TrainChestExtendableTrain.findOccupied(occupiedLocations, group.getMembers().get(0))) != null) {
                    options.tryExtendTrains = false;
                    options.connectWith = extendableTrain2.member;
                    options.spawnMode = SpawnableGroup.SpawnMode.REVERSE_EDGE;
                    return TrainChestItemUtil.spawnAtState(group, extendableTrain2.startState.cloneAndInvertMotion(), options);
                }
                return SpawnResult.FAIL_BLOCKED;
            }
        } else if (options.connectWith != null) {
            MinecartGroup connectWithGroup = options.connectWith.getGroup();
            for (SpawnableGroup.OccupiedLocation occupied : locationList.getOccupiedLocations()) {
                if (occupied.member.getGroup() == connectWithGroup) continue;
                return SpawnResult.FAIL_BLOCKED;
            }
        } else if (locationList.isOccupied()) {
            return SpawnResult.FAIL_BLOCKED;
        }
        if (locationList.locations.size() < group.getMembers().size()) {
            return SpawnResult.FAIL_RAILTOOSHORT;
        }
        if (options.tryExtendTrains && locationList.endState != null && (extendableTrain = TrainChestExtendableTrain.find(locationList.endState, searchDistance = 1.0 + 2.0 * (firstMember = group.getMembers().get(0)).getCartCouplerLength() + 0.5 * firstMember.getLength(), firstMember)) != null) {
            options.tryExtendTrains = false;
            options.connectWith = extendableTrain.member;
            options.spawnMode = SpawnableGroup.SpawnMode.REVERSE_EDGE;
            return TrainChestItemUtil.spawnAtState(group, extendableTrain.startState.cloneAndInvertMotion(), options);
        }
        MinecartGroup spawnedGroup = group.spawn(locationList, options.initialSpeed);
        if (!spawnedGroup.isEmpty()) {
            spawnedGroup.getTrainCarts().getPlayer(options.player).editMember(spawnedGroup.tail());
        }
        if (options.connectWith != null) {
            MinecartMember<?> with = options.spawnMode.isReverseOrder() ? spawnedGroup.head() : spawnedGroup.tail();
            MinecartGroup.link(with, options.connectWith);
        }
        return SpawnResult.SUCCESS;
    }

    public static SpawnResult spawnAtState(SpawnableGroup group, RailState state, SpawnOptions options) {
        if (group == null) {
            return SpawnResult.FAIL_EMPTY;
        }
        int totalLength = group.getMembers().size();
        if (options.connectWith != null) {
            totalLength += options.connectWith.getGroup().size();
        }
        if (TCConfig.maxCartsPerTrain >= 0 && totalLength > TCConfig.maxCartsPerTrain) {
            return SpawnResult.FAIL_TOO_LONG;
        }
        if (group.isExceedingSpawnLimit()) {
            return SpawnResult.FAIL_LIMIT_REACHED;
        }
        if (MinecartGroupStore.isPerWorldSpawnLimitReached(state.positionLocation(), group.getMembers().size())) {
            return SpawnResult.FAIL_MAX_PER_WORLD;
        }
        SpawnableGroup.SpawnLocationList locationList = group.findSpawnLocations(state, options.spawnMode);
        return TrainChestItemUtil.spawnAtLocations(group, locationList, options);
    }

    public static enum SpawnResult {
        SUCCESS(Localization.CHEST_SPAWN_SUCCESS),
        FAIL_EMPTY(Localization.CHEST_SPAWN_EMPTY),
        FAIL_NORAIL(Localization.CHEST_SPAWN_NORAIL),
        FAIL_NORAIL_LOOK(Localization.CHEST_SPAWN_NORAIL_LOOK),
        FAIL_RAILTOOSHORT(Localization.CHEST_SPAWN_RAILTOOSHORT),
        FAIL_BLOCKED(Localization.CHEST_SPAWN_BLOCKED),
        FAIL_NO_PERM(Localization.SPAWN_FORBIDDEN_CONTENTS),
        FAIL_LIMIT_REACHED(Localization.CHEST_SPAWN_LIMIT_REACHED),
        FAIL_MAX_PER_WORLD(Localization.SPAWN_MAX_PER_WORLD),
        FAIL_TOO_LONG(Localization.SPAWN_TOO_LONG);

        private final Localization locale;

        private SpawnResult(Localization locale) {
            this.locale = locale;
        }

        public boolean hasMessage() {
            return this.locale != null;
        }

        public Localization getLocale() {
            return this.locale;
        }
    }

    public static class SpawnOptions {
        public final Player player;
        public double initialSpeed = 0.0;
        public boolean tryExtendTrains = false;
        public SpawnableGroup.SpawnMode spawnMode = SpawnableGroup.SpawnMode.DEFAULT;
        public MinecartMember<?> connectWith = null;

        public SpawnOptions(Player player) {
            this.player = player;
        }
    }
}

