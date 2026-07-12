/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.Common
 *  com.bergerkiller.bukkit.common.MaterialTypeProperty
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.yaml.YamlPath
 *  com.bergerkiller.bukkit.common.inventory.ItemParser
 *  com.bergerkiller.bukkit.common.math.Quaternion
 *  com.bergerkiller.bukkit.common.utils.BlockUtil
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.bukkit.common.utils.EntityPropertyUtil
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.ItemUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil$ItemSynchronizer
 *  com.bergerkiller.bukkit.common.utils.MaterialUtil
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 *  com.bergerkiller.bukkit.common.utils.PlayerUtil
 *  com.bergerkiller.bukkit.common.utils.StringUtil
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 *  com.bergerkiller.bukkit.common.wrappers.DamageSource
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEquipmentPacketHandle
 *  com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEquipmentPacketHandle$OwnerType
 *  com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle
 *  com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle
 *  com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle
 *  com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle
 *  com.bergerkiller.mountiplex.reflection.util.FastMethod
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Chunk
 *  org.bukkit.Color
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.Particle
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.Sign
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Vehicle
 *  org.bukkit.event.block.BlockPhysicsEvent
 *  org.bukkit.event.block.SignChangeEvent
 *  org.bukkit.event.vehicle.VehicleDamageEvent
 *  org.bukkit.event.vehicle.VehicleDestroyEvent
 *  org.bukkit.inventory.EquipmentSlot
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 *  org.bukkit.material.Rails
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.MaterialTypeProperty;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlPath;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.math.Quaternion;
import com.bergerkiller.bukkit.common.utils.BlockUtil;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.EntityPropertyUtil;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.DamageSource;
import com.bergerkiller.bukkit.tc.TCConfig;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.MinecartMemberStore;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.RailLookup;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.AveragedItemParser;
import com.bergerkiller.bukkit.tc.utils.BlockPhysicsEventDataAccessor;
import com.bergerkiller.bukkit.tc.utils.BoundingRange;
import com.bergerkiller.bukkit.tc.utils.FormattedSpeed;
import com.bergerkiller.bukkit.tc.utils.QuoteEscapedString;
import com.bergerkiller.bukkit.tc.utils.TrackMovingPoint;
import com.bergerkiller.bukkit.tc.utils.TrackWalkingPoint;
import com.bergerkiller.generated.net.minecraft.network.protocol.game.ClientboundSetEquipmentPacketHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.network.ServerGamePacketListenerImplHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Rails;
import org.bukkit.util.Vector;

public class Util {
    public static final MaterialTypeProperty ISVERTRAIL = new MaterialTypeProperty(new Material[]{Material.LADDER});
    public static final MaterialTypeProperty ISTCRAIL = new MaterialTypeProperty(new MaterialTypeProperty[]{ISVERTRAIL, MaterialUtil.ISRAILS, MaterialUtil.ISPRESSUREPLATE});
    private static final String SEPARATOR_REGEX = "[|/\\\\]";
    private static List<Block> blockbuff = new ArrayList<Block>();
    private static final NumberFormat numberBox_NumberFormat = Util.createNumberFormat(1, 4);
    private static final NumberFormat animationodeTime_NumberFormat1000 = Util.createNumberFormat(0, 0);
    private static final NumberFormat animationodeTime_NumberFormat100 = Util.createNumberFormat(1, 1);
    private static final NumberFormat animationodeTime_NumberFormat10 = Util.createNumberFormat(1, 2);
    private static final NumberFormat animationodeTime_NumberFormat1 = Util.createNumberFormat(1, 3);
    private static final double SQ_COS_22_5 = Math.pow(Math.cos(0.39269908169872414), 2.0);
    private static final TeleportPositionMethod TELEPORT_POSITION_METHOD = Util.findRelativeTeleportMethod();
    private static final EnumMap<ChatColor, Color> COLOR_TO_RGB = new EnumMap(ChatColor.class);
    private static final Color UNKNOWN_CHAT_COLOR = Color.fromRGB((int)252, (int)252, (int)252);
    public static final DamageEventConstructor DAMAGE_EVENT_CONSTRUCTOR;

    public static NumberFormat createNumberFormat(int min_fractionDigits, int max_fractionDigits) {
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.ENGLISH);
        fmt.setMinimumFractionDigits(min_fractionDigits);
        fmt.setMaximumFractionDigits(max_fractionDigits);
        fmt.setGroupingUsed(false);
        return fmt;
    }

    public static int minStringIndex(int a, int b) {
        if (a == -1 || b == -1) {
            return a > b ? a : b;
        }
        return a < b ? a : b;
    }

    public static String[] splitBySeparator(String text) {
        return text.split(SEPARATOR_REGEX);
    }

    public static BlockFace getVerticalFace(boolean up) {
        return up ? BlockFace.UP : BlockFace.DOWN;
    }

    public static BlockFace snapFace(BlockFace face) {
        switch (face) {
            case NORTH_NORTH_EAST: {
                return BlockFace.NORTH_EAST;
            }
            case EAST_NORTH_EAST: {
                return BlockFace.EAST;
            }
            case EAST_SOUTH_EAST: {
                return BlockFace.SOUTH_EAST;
            }
            case SOUTH_SOUTH_EAST: {
                return BlockFace.SOUTH;
            }
            case SOUTH_SOUTH_WEST: {
                return BlockFace.SOUTH_WEST;
            }
            case WEST_SOUTH_WEST: {
                return BlockFace.WEST;
            }
            case WEST_NORTH_WEST: {
                return BlockFace.NORTH_WEST;
            }
            case NORTH_NORTH_WEST: {
                return BlockFace.NORTH;
            }
        }
        return face;
    }

    @Deprecated
    public static List<Block> getSignsFromRails(Block railsblock) {
        return Util.getSignsFromRails(blockbuff, railsblock);
    }

    @Deprecated
    public static List<Block> getSignsFromRails(List<Block> rval, Block railsblock) {
        rval.clear();
        Util.addSignsFromRails(rval, railsblock);
        return rval;
    }

    @Deprecated
    public static void addSignsFromRails(List<Block> rval, Block railsBlock) {
        RailType railType = RailType.getType(railsBlock);
        if (railType == RailType.NONE) {
            return;
        }
        for (RailLookup.TrackedSign trackedSign : RailPiece.create(railType, railsBlock).signs()) {
            rval.add(trackedSign.signBlock);
        }
    }

    public static boolean hasAttachedSigns(Block middle) {
        return Util.addAttachedSigns(middle, null);
    }

    public static boolean addAttachedSigns(Block middle, Collection<Block> rval) {
        boolean found = false;
        for (BlockFace face : FaceUtil.AXIS) {
            Block b = middle.getRelative(face);
            if (!((Boolean)MaterialUtil.ISSIGN.get(b)).booleanValue() || BlockUtil.getAttachedFace((Block)b) != face.getOppositeFace()) continue;
            found = true;
            if (rval == null) continue;
            rval.add(b);
        }
        return found;
    }

    @Deprecated
    public static Block getRailsFromSign(Block signblock) {
        return RailLookup.discoverRailPieceFromSign(signblock).block();
    }

    @Deprecated
    public static Block findRailsVertical(Block from, BlockFace mode) {
        RailPiece piece = Util.findNextRailPiece(from, mode);
        return piece == null ? null : piece.block();
    }

    public static RailPiece findNextRailPiece(Block from, BlockFace mode) {
        int maxSteps = 1024;
        World world = from.getWorld();
        Block block = from;
        if (mode == BlockFace.DOWN) {
            int min = WorldUtil.getWorldMinimumHeight((World)world);
            int y = block.getY();
            while (--y >= min && --maxSteps > 0) {
                RailType type = RailType.getType(block = block.getRelative(mode));
                if (type == RailType.NONE) continue;
                return RailPiece.create(type, block);
            }
        } else if (mode == BlockFace.UP) {
            int max = WorldUtil.getWorldMaximumHeight((World)world);
            int y = block.getY();
            while (++y < max && --maxSteps > 0) {
                RailType type = RailType.getType(block = block.getRelative(mode));
                if (type == RailType.NONE) continue;
                return RailPiece.create(type, block);
            }
        } else {
            while (--maxSteps > 0) {
                RailType type = RailType.getType(block = block.getRelative(mode));
                if (type == RailType.NONE) continue;
                return RailPiece.create(type, block);
            }
        }
        return null;
    }

    public static ItemParser[] getParsers(String ... items) {
        return Util.getParsers(StringUtil.join((String)";", (String[])items));
    }

    public static ItemParser[] getParsers(String items) {
        ArrayList<ItemParser> parsers = new ArrayList<ItemParser>();
        int multiplier = -1;
        for (String type : items.split(";")) {
            if ((type = type.trim()).isEmpty()) continue;
            int multiIndex = type.indexOf(35);
            if (multiIndex != -1) {
                multiplier = ParseUtil.parseInt((String)type.substring(0, multiIndex), (int)-1);
                type = type.substring(multiIndex + 1);
            }
            int amount = -1;
            int idx = StringUtil.firstIndexOf((String)type, (String[])new String[]{"x", "X", " ", "*"});
            if (idx > 0 && (amount = ParseUtil.parseInt((String)type.substring(0, idx), (int)-1)) != -1) {
                type = type.substring(idx + 1);
            }
            ItemParser[] keyparsers = TrainCarts.plugin.getParsers(type, amount);
            if (multiIndex != -1) {
                for (int i = 0; i < keyparsers.length; ++i) {
                    keyparsers[i] = new AveragedItemParser(keyparsers[i], multiplier);
                }
            }
            parsers.addAll(Arrays.asList(keyparsers));
        }
        if (parsers.isEmpty()) {
            parsers.add(new ItemParser(null));
        }
        return parsers.toArray(new ItemParser[0]);
    }

    public static Block getRailsBlock(Block from) {
        if (((Boolean)ISTCRAIL.get(from)).booleanValue()) {
            return from;
        }
        return (Boolean)ISTCRAIL.get(from = from.getRelative(BlockFace.DOWN)) != false ? from : null;
    }

    public static String getTimeString(long time) {
        if (time == 0L) {
            return "00:00:00";
        }
        time = (long)Math.ceil(0.001 * (double)time);
        int seconds = (int)(time % 60L);
        int minutes = (int)(time % 3600L / 60L);
        int hours = (int)(time / 3600L);
        StringBuilder rval = new StringBuilder(8);
        if (hours < 10) {
            rval.append('0');
        }
        rval.append(hours).append(':');
        if (minutes < 10) {
            rval.append('0');
        }
        rval.append(minutes).append(':');
        if (seconds < 10) {
            rval.append('0');
        }
        rval.append(seconds);
        return rval.toString();
    }

    private static boolean isRailsAt(Block block, BlockFace direction) {
        return Util.getRailsBlock(block.getRelative(direction)) != null;
    }

    public static BlockFace getPlateDirection(Block plate) {
        boolean w;
        boolean s = Util.isRailsAt(plate, BlockFace.NORTH) || Util.isRailsAt(plate, BlockFace.SOUTH);
        boolean bl = w = Util.isRailsAt(plate, BlockFace.EAST) || Util.isRailsAt(plate, BlockFace.WEST);
        if (s && w) {
            return BlockFace.SELF;
        }
        if (w) {
            return BlockFace.EAST;
        }
        if (s) {
            return BlockFace.SOUTH;
        }
        return BlockFace.SELF;
    }

    public static boolean isSloped(int railsData) {
        return (railsData &= 7) >= 2 && railsData <= 5;
    }

    public static boolean isVerticalAbove(Block rails, BlockFace direction) {
        BlockData blockData = WorldUtil.getBlockData((World)rails.getWorld(), (int)rails.getX(), (int)(rails.getY() + 1), (int)rails.getZ());
        return ISVERTRAIL.get(blockData) != false && Util.getVerticalRailDirection(blockData.getRawData()) == direction;
    }

    public static boolean isVerticalBelow(Block rails, BlockFace direction) {
        BlockData blockData = WorldUtil.getBlockData((World)rails.getWorld(), (int)rails.getX(), (int)(rails.getY() - 1), (int)rails.getZ());
        return ISVERTRAIL.get(blockData) != false && Util.getVerticalRailDirection(blockData.getRawData()) == direction;
    }

    public static BlockFace getVerticalRailDirection(Block railsBlock) {
        return Util.getVerticalRailDirection(MaterialUtil.getRawData((Block)railsBlock));
    }

    public static BlockFace getVerticalRailDirection(int raildata) {
        switch (raildata) {
            case 2: {
                return BlockFace.SOUTH;
            }
            case 3: {
                return BlockFace.NORTH;
            }
            case 4: {
                return BlockFace.EAST;
            }
        }
        return BlockFace.WEST;
    }

    public static int getOperatorIndex(String text) {
        for (int i = 0; i < text.length(); ++i) {
            if (!Util.isOperator(text.charAt(i))) continue;
            return i;
        }
        return -1;
    }

    public static boolean isOperator(char character) {
        return LogicUtil.containsChar((char)character, (char[])new char[]{'!', '=', '<', '>'});
    }

    public static boolean canBePassenger(Entity entity) {
        return entity instanceof LivingEntity;
    }

    public static boolean matchText(Collection<String> textValues, String expression) {
        if (expression.startsWith("!")) {
            return !Util.matchText(textValues, expression.substring(1));
        }
        if (expression.isEmpty() || textValues.isEmpty()) {
            return false;
        }
        String[] elements = expression.split("\\*");
        boolean first = expression.startsWith("*");
        boolean last = expression.endsWith("*");
        for (String text : textValues) {
            if (!Util.matchText(text, elements, first, last)) continue;
            return true;
        }
        return false;
    }

    public static boolean matchText(String text, String expression) {
        if (expression.isEmpty()) {
            return false;
        }
        if (expression.startsWith("!")) {
            return !Util.matchText(text, expression.substring(1));
        }
        return Util.matchText(text, expression.split("\\*"), expression.startsWith("*"), expression.endsWith("*"));
    }

    public static boolean matchText(String text, String[] elements, boolean firstAny, boolean lastAny) {
        if (elements == null || elements.length == 0) {
            return true;
        }
        int index = 0;
        boolean has = true;
        boolean first = true;
        for (String element : elements) {
            if (element.length() == 0) continue;
            if ((index = text.indexOf(element, index)) == -1 || first && !firstAny && index != 0) {
                has = false;
                break;
            }
            index += element.length();
            first = false;
        }
        return has && (lastAny || index == text.length());
    }

    public static boolean evaluate(double value, String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        int idx = Util.getOperatorIndex(text);
        if (idx == -1) {
            return value > 0.0;
        }
        if ((text = text.substring(idx)).startsWith(">=") || text.startsWith("=>")) {
            return value >= ParseUtil.parseDouble((String)text.substring(2), (double)0.0);
        }
        if (text.startsWith("<=") || text.startsWith("=<")) {
            return value <= ParseUtil.parseDouble((String)text.substring(2), (double)0.0);
        }
        if (text.startsWith("==")) {
            return value == ParseUtil.parseDouble((String)text.substring(2), (double)0.0);
        }
        if (text.startsWith("!=") || text.startsWith("<>") || text.startsWith("><")) {
            return value != ParseUtil.parseDouble((String)text.substring(2), (double)0.0);
        }
        if (text.startsWith(">")) {
            return value > ParseUtil.parseDouble((String)text.substring(1), (double)0.0);
        }
        if (text.startsWith("<")) {
            return value < ParseUtil.parseDouble((String)text.substring(1), (double)0.0);
        }
        if (text.startsWith("=")) {
            return value == ParseUtil.parseDouble((String)text.substring(1), (double)0.0);
        }
        return false;
    }

    public static boolean canInstantlyBreakMinecart(Entity entity) {
        if (!TCConfig.instantCreativeDestroy || !Util.canInstantlyBuild(entity)) {
            return false;
        }
        return !(entity instanceof Player) || !((Player)entity).isSneaking();
    }

    public static boolean canInstantlyBuild(Entity entity) {
        return entity instanceof HumanEntity && EntityPropertyUtil.getAbilities((HumanEntity)((HumanEntity)entity)).canInstantlyBuild();
    }

    public static boolean isSignSupported(Block block, BlockData blockDataOfBlock) {
        BlockFace face = blockDataOfBlock.getAttachedFace();
        return WorldUtil.getBlockData((World)block.getWorld(), (int)(block.getX() + face.getModX()), (int)(block.getY() + face.getModY()), (int)(block.getZ() + face.getModZ())).isSolid();
    }

    public static boolean isSignSupported(Block block) {
        return Util.isSignSupported(block, WorldUtil.getBlockData((Block)block));
    }

    public static Rails getRailsRO(Block block) {
        MaterialData data = WorldUtil.getBlockData((Block)block).getMaterialData();
        return data instanceof Rails ? (Rails)data : null;
    }

    public static boolean isValidEntity(String entityName) {
        try {
            return EntityType.valueOf((String)entityName) != null;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public static Vector parseVector(String text, Vector def) {
        String[] offsettext = Util.splitBySeparator(text);
        Vector offset = new Vector();
        if (offsettext.length == 3) {
            offset.setX(ParseUtil.parseDouble((String)offsettext[0], (double)0.0));
            offset.setY(ParseUtil.parseDouble((String)offsettext[1], (double)0.0));
            offset.setZ(ParseUtil.parseDouble((String)offsettext[2], (double)0.0));
        } else if (offsettext.length == 2) {
            offset.setX(ParseUtil.parseDouble((String)offsettext[0], (double)0.0));
            offset.setZ(ParseUtil.parseDouble((String)offsettext[1], (double)0.0));
        } else if (offsettext.length == 1) {
            offset.setY(ParseUtil.parseDouble((String)offsettext[0], (double)0.0));
        } else {
            return def;
        }
        return offset;
    }

    public static double parseAcceleration(String accelerationString, double defaultValue) {
        if (accelerationString.isEmpty()) {
            return defaultValue;
        }
        accelerationString = accelerationString.toLowerCase(Locale.ENGLISH);
        accelerationString = accelerationString.replace("kmh", "kmph");
        accelerationString = accelerationString.replace("kmph", "km/h");
        accelerationString = accelerationString.replace("miph", "mph");
        int slashIndex = (accelerationString = accelerationString.replace("mph", "mi/h")).indexOf(47);
        if (slashIndex != -1) {
            char c;
            int i;
            double factor = 1.0;
            StringBuilder valueStr = new StringBuilder(slashIndex + 1);
            for (i = 0; i < slashIndex; ++i) {
                c = accelerationString.charAt(i);
                if (Character.isDigit(c) || c == '.' || c == ',' || c == '-') {
                    valueStr.append(c);
                    continue;
                }
                if (c == 'k') {
                    factor = 1000.0;
                    continue;
                }
                if (c == 'f' && accelerationString.charAt(i + 1) == 't') {
                    factor = 0.3048780487804878;
                    ++i;
                    continue;
                }
                if (c != 'm' || accelerationString.charAt(i + 1) != 'i') continue;
                factor = 1609.344;
                ++i;
            }
            double value = ParseUtil.parseDouble((String)valueStr.toString(), (double)Double.NaN);
            if (Double.isNaN(value)) {
                return defaultValue;
            }
            value *= factor;
            int num_units = 0;
            double factor2 = 1.0;
            for (i = slashIndex + 1; i < accelerationString.length() && num_units < 2; ++i) {
                c = accelerationString.charAt(i);
                if (c == 's') {
                    factor2 *= 20.0;
                    ++num_units;
                    continue;
                }
                if (c == 'm') {
                    factor2 *= 1200.0;
                    ++num_units;
                    continue;
                }
                if (c != 'h') continue;
                factor2 *= 72000.0;
                ++num_units;
            }
            if (num_units == 1) {
                factor2 *= factor2;
            }
            return value / factor2;
        }
        char lastChar = accelerationString.charAt(accelerationString.length() - 1);
        if (lastChar == 'g') {
            String g_value_str = accelerationString.substring(0, accelerationString.length() - 1);
            double value = ParseUtil.parseDouble((String)g_value_str, (double)Double.NaN);
            if (Double.isNaN(value)) {
                return defaultValue;
            }
            return 0.024525 * value;
        }
        return ParseUtil.parseDouble((String)accelerationString, (double)defaultValue);
    }

    public static double parseVelocity(String velocityString, double defaultValue) {
        FormattedSpeed speed = FormattedSpeed.parse(velocityString, null);
        return speed != null ? speed.getValue() : defaultValue;
    }

    public static double calculateStraightLength(Block railsBlock, BlockFace direction) {
        TrackWalkingPoint p = new TrackWalkingPoint(railsBlock, direction);
        Vector start_dir = null;
        while (p.movedTotal < 20.0 && p.move(0.1)) {
            if (start_dir == null) {
                start_dir = p.state.motionVector();
                continue;
            }
            if (!(p.state.position().motDot(start_dir) < 0.75)) continue;
            break;
        }
        return p.movedTotal;
    }

    public static int parseTimeTicks(String text) {
        text = text.toLowerCase(Locale.ENGLISH);
        double ticks = -1.0;
        if (text.endsWith("ms")) {
            ticks = 0.02 * ParseUtil.parseDouble((String)text.substring(0, text.length() - 2), (double)-1.0);
        } else if (text.endsWith("m")) {
            ticks = 1200.0 * ParseUtil.parseDouble((String)text.substring(0, text.length() - 1), (double)-1.0);
        } else if (text.endsWith("s")) {
            ticks = 20.0 * ParseUtil.parseDouble((String)text.substring(0, text.length() - 1), (double)-1.0);
        } else if (text.endsWith("t")) {
            ticks = ParseUtil.parseInt((String)text.substring(0, text.length() - 1), (int)-1);
        }
        return ticks < 0.0 ? -1 : (int)ticks;
    }

    public static String getUnicode(char unicode) {
        return "\\u" + Integer.toHexString(unicode | 0x10000).substring(1);
    }

    public static String getCleanLine(SignChangeEvent event, int line) {
        if (event == null) {
            return "";
        }
        return Util.cleanSignLine(event.getLine(line));
    }

    public static String getCleanLine(Sign sign, int line) {
        if (sign == null) {
            return "";
        }
        return Util.cleanSignLine(sign.getLine(line));
    }

    public static String cleanSignLine(String line) {
        if (line == null) {
            return "";
        }
        for (int i = 0; i < line.length(); ++i) {
            if (!Util.isInvalidCharacter(line.charAt(i))) continue;
            StringBuilder clear = new StringBuilder(line.length() - 1);
            clear.append(line, 0, i);
            for (int j = i + 1; j < line.length(); ++j) {
                char c = line.charAt(j);
                if (Util.isInvalidCharacter(c)) continue;
                clear.append(c);
            }
            return clear.toString();
        }
        return line;
    }

    public static String[] cleanSignLines(String[] lines) {
        if (lines == null) {
            return new String[]{"", "", "", ""};
        }
        boolean hasInvalid = false;
        if (lines.length != 4) {
            hasInvalid = true;
            String[] newLines = new String[]{"", "", "", ""};
            for (int i = 0; i < Math.min(lines.length, 4); ++i) {
                newLines[i] = lines[i];
            }
            lines = newLines;
        }
        for (int i = 0; i < lines.length; ++i) {
            String oldLine = lines[i];
            String newLine = Util.cleanSignLine(oldLine);
            if (oldLine == newLine) continue;
            if (!hasInvalid) {
                hasInvalid = true;
                lines = (String[])lines.clone();
            }
            lines[i] = newLine;
        }
        return lines;
    }

    public static boolean isInvalidCharacter(char c) {
        return Character.getType(c) == 18;
    }

    public static boolean isProtocolRotationGlitched(float angleOld, float angleNew) {
        int protOld = EntityTrackerEntryStateHandle.getProtocolRotation((float)angleOld);
        int protNew = EntityTrackerEntryStateHandle.getProtocolRotation((float)angleNew);
        return Math.abs(protNew - protOld) > 128;
    }

    public static float atOppositeRotationGlitchBoundary(float angle) {
        return angle >= 180.0f ? 179.0f : 181.0f;
    }

    public static void spawnParticle(Location loc, Particle particle) {
        loc.getWorld().spawnParticle(particle, loc, 1);
    }

    public static void spawnBubble(Location loc) {
        Util.spawnParticle(loc, Particle.WATER_BUBBLE);
    }

    public static void spawnDustParticle(Location loc, Color color) {
        Util.spawnDustParticle(loc, (double)color.getRed() / 255.0, (double)color.getGreen() / 255.0, (double)color.getBlue() / 255.0);
    }

    public static void spawnDustParticle(Location loc, double red, double green, double blue) {
        int c_red = (int)MathUtil.clamp((double)(255.0 * red), (double)0.0, (double)255.0);
        int c_green = (int)MathUtil.clamp((double)(255.0 * green), (double)0.0, (double)255.0);
        int c_blue = (int)MathUtil.clamp((double)(255.0 * blue), (double)0.0, (double)255.0);
        Color color = Color.fromRGB((int)c_red, (int)c_green, (int)c_blue);
        Vector position = loc.toVector();
        for (Player player : loc.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(loc) > 65536.0) continue;
            PlayerUtil.spawnDustParticles((Player)player, (Vector)position, (Color)color);
        }
    }

    public static Location invertRotation(Location loc) {
        Quaternion q = Quaternion.fromYawPitchRoll((double)loc.getPitch(), (double)loc.getYaw(), (double)0.0);
        q.rotateYFlip();
        Vector ypr_new = q.getYawPitchRoll();
        loc.setYaw((float)ypr_new.getY());
        loc.setPitch((float)ypr_new.getX());
        return loc;
    }

    public static BlockFace vecToFace(Vector vector, boolean useSubCardinalDirections) {
        return Util.vecToFace(vector.getX(), vector.getY(), vector.getZ(), useSubCardinalDirections);
    }

    public static BlockFace vecToFace(double dx, double dy, double dz, boolean useSubCardinalDirections) {
        double sqleny = dy * dy;
        double sqlenxz = dx * dx + dz * dz;
        if (sqleny > sqlenxz + 1.0E-6) {
            return FaceUtil.getVertical((double)dy);
        }
        return FaceUtil.getDirection((double)dx, (double)dz, (boolean)useSubCardinalDirections);
    }

    public static Vector lerpOrientation(Vector up0, Vector up1, double theta) {
        Quaternion qa = Quaternion.fromLookDirection((Vector)up0);
        Quaternion qb = Quaternion.fromLookDirection((Vector)up1);
        Quaternion q = Quaternion.slerp((Quaternion)qa, (Quaternion)qb, (double)theta);
        return q.forwardVector();
    }

    public static Vector getArmorStandPose(Quaternion rotation) {
        double qx = rotation.getX();
        double qy = rotation.getY();
        double qz = rotation.getZ();
        double qw = rotation.getW();
        double rx = 1.0 + 2.0 * (-qy * qy - qz * qz);
        double ry = 2.0 * (qx * qy + qz * qw);
        double rz = 2.0 * (qx * qz - qy * qw);
        double uz = 2.0 * (qy * qz + qx * qw);
        double fz = 1.0 + 2.0 * (-qx * qx - qy * qy);
        if (Math.abs(rz) < 0.999999999999999) {
            return new Vector(MathUtil.atan2((double)uz, (double)fz), Util.fastAsin(rz), MathUtil.atan2((double)(-ry), (double)rx));
        }
        double sign = rz < 0.0 ? -1.0 : 1.0;
        return new Vector(0.0, sign * 90.0, -sign * 2.0 * (double)MathUtil.atan2((double)qx, (double)qw));
    }

    public static float fastAsin(double x) {
        return MathUtil.atan((double)(x / Math.sqrt(1.0 - x * x)));
    }

    public static Block getNextPos(Block railBlock, BlockFace direction) {
        TrackMovingPoint p = new TrackMovingPoint(railBlock, direction);
        if (!p.hasNext()) {
            return null;
        }
        p.next();
        if (!p.hasNext()) {
            return null;
        }
        p.next(false);
        return p.currentLocation.getBlock();
    }

    public static final void markChunkDirty(Chunk chunk) {
        LevelChunkHandle.fromBukkit((Chunk)chunk).markDirty();
    }

    public static RailJunction faceToJunction(List<RailJunction> junctions, BlockFace face) {
        return RailJunction.findBest(junctions, FaceUtil.faceToVector((BlockFace)face)).orElse(null);
    }

    public static void loadInventoryFromConfig(Inventory inventory, ConfigurationNode config) {
        inventory.clear();
        if (config.isNode("contents")) {
            ConfigurationNode contents = config.getNode("contents");
            for (String indexStr : contents.getKeys()) {
                int index;
                try {
                    index = Integer.parseInt(indexStr);
                }
                catch (NumberFormatException ex) {
                    continue;
                }
                ItemStack item = (ItemStack)contents.get(indexStr, ItemStack.class);
                if (ItemUtil.isEmpty((ItemStack)item)) continue;
                inventory.setItem(index, item.clone());
            }
        }
    }

    public static void saveInventoryToConfig(Inventory inventory, ConfigurationNode config) {
        ConfigurationNode contents = null;
        for (int i = 0; i < inventory.getSize(); ++i) {
            ItemStack item = inventory.getItem(i);
            if (ItemUtil.isEmpty((ItemStack)item)) continue;
            if (contents == null) {
                contents = config.getNode("contents");
            }
            contents.set(Integer.toString(i), (Object)item.clone());
        }
    }

    public static void setVector(Vector v, Vector v2) {
        v.setX(v2.getX());
        v.setY(v2.getY());
        v.setZ(v2.getZ());
    }

    public static boolean isOrientationInverted(Vector vel, Quaternion q) {
        double pz;
        double py;
        double x = q.getX();
        double y = q.getY();
        double z = q.getZ();
        double w = q.getW();
        double px = vel.getX();
        return px * (x * z + y * w) + (py = vel.getY()) * (y * z - x * w) - (pz = vel.getZ()) * (x * x + y * y - 0.5) <= 0.0;
    }

    public static double fastGetRotationYaw(Quaternion rotation) {
        double yaw;
        double x = rotation.getX();
        double y = rotation.getY();
        double z = rotation.getZ();
        double w = rotation.getW();
        double test = 2.0 * (w * x - y * z);
        if (Math.abs(test) < 0.999999999999999) {
            double x2 = x * x;
            double y2 = y * y;
            double z2 = z * z;
            yaw = MathUtil.atan2((double)(2.0 * (w * y + z * x)), (double)(1.0 - 2.0 * (x2 + y2)));
            if (x2 + z2 > 0.5) {
                yaw += yaw < 0.0 ? 180.0 : -180.0;
            }
        } else {
            yaw = 2.0 * (double)MathUtil.atan2((double)z, (double)w);
            if (test >= 0.0) {
                yaw = -yaw;
            }
        }
        if (yaw > 180.0) {
            yaw -= 360.0;
        } else if (yaw < -180.0) {
            yaw += 360.0;
        }
        return -yaw;
    }

    public static void checkMainThread(String what) {
        if (!CommonUtil.isMainThread()) {
            TrainCarts.plugin.log(Level.WARNING, what + " called from a thread other than the main thread!");
            Thread.dumpStack();
        }
    }

    public static void correctTeleportPosition(Location loc) {
        Block locBlock = loc.getBlock();
        Vector rel = loc.toVector();
        rel.setX(rel.getX() - (double)locBlock.getX());
        rel.setY(rel.getY() - (double)locBlock.getY());
        rel.setZ(rel.getZ() - (double)locBlock.getZ());
        AABBHandle bounds = WorldUtil.getBlockData((Block)locBlock).getBoundingBox(locBlock);
        if (bounds != null && rel.getX() >= bounds.getMinX() && rel.getX() <= bounds.getMaxX() && rel.getY() >= bounds.getMinY() && rel.getY() <= bounds.getMaxY() && rel.getZ() >= bounds.getMinZ() && rel.getZ() <= bounds.getMaxZ()) {
            loc.setY((double)locBlock.getY() + bounds.getMaxY() + 1.0E-5);
        }
    }

    public static float getNextEntityYaw(float old_yaw, double yaw_change) {
        if (yaw_change < -90.0 || yaw_change > 90.0) {
            return old_yaw;
        }
        int prot_yaw_rot_old = EntityTrackerEntryStateHandle.getProtocolRotation((float)old_yaw);
        int prot_yaw_rot_new = EntityTrackerEntryStateHandle.getProtocolRotation((float)((float)((double)old_yaw + yaw_change)));
        if (prot_yaw_rot_new != prot_yaw_rot_old) {
            float new_yaw = EntityTrackerEntryStateHandle.getRotationFromProtocol((int)prot_yaw_rot_new);
            double new_yaw_change = MathUtil.wrapAngle((double)((double)new_yaw - (double)old_yaw));
            if (yaw_change < 0.0) {
                if (new_yaw_change < yaw_change) {
                    new_yaw = EntityTrackerEntryStateHandle.getRotationFromProtocol((int)(++prot_yaw_rot_new));
                }
            } else if (new_yaw_change > yaw_change) {
                new_yaw = EntityTrackerEntryStateHandle.getRotationFromProtocol((int)(--prot_yaw_rot_new));
            }
            return new_yaw;
        }
        return old_yaw;
    }

    public static String stringifyNumberBoxValue(double value) {
        return numberBox_NumberFormat.format(value);
    }

    public static String stringifyAnimationNodeTime(double time) {
        if (time >= 9999.0) {
            return "9999";
        }
        if (time >= 999.95) {
            return animationodeTime_NumberFormat1000.format(time);
        }
        if (time >= 99.995) {
            return animationodeTime_NumberFormat100.format(time);
        }
        if (time >= 9.9995) {
            return animationodeTime_NumberFormat10.format(time);
        }
        if (time >= 5.0E-4) {
            return animationodeTime_NumberFormat1.format(time);
        }
        return "0.0";
    }

    public static boolean isDiagonal(Vector direction) {
        double sq_z;
        double sq_x = direction.getX() * direction.getX();
        double sq_xz = sq_x + (sq_z = direction.getZ() * direction.getZ());
        return sq_xz >= 1.0E-10 && sq_x / sq_xz < SQ_COS_22_5 && sq_z / sq_xz < SQ_COS_22_5;
    }

    public static boolean isConnectedRailsFrom(RailPiece rails, BlockFace direction) {
        if (rails == null || rails.block() == null) {
            return false;
        }
        RailJunction junction = Util.faceToJunction(rails.type().getJunctions(rails.block()), direction);
        if (junction == null) {
            return false;
        }
        RailState state = rails.type().takeJunction(rails.block(), junction);
        if (state == null) {
            return false;
        }
        state.setMotionVector(state.motionVector().multiply(-1.0));
        state.initEnterDirection();
        TrackWalkingPoint wp = new TrackWalkingPoint(state);
        wp.skipFirst();
        if (!wp.moveFull()) {
            return false;
        }
        return wp.state.railType() == rails.type() && wp.state.railBlock().equals((Object)rails.block());
    }

    public static boolean isConnectedRails(RailPiece rails, BlockFace direction) {
        if (rails == null || rails.block() == null) {
            return false;
        }
        RailJunction junction = Util.faceToJunction(rails.type().getJunctions(rails.block()), direction);
        if (junction == null) {
            return false;
        }
        RailState state = rails.type().takeJunction(rails.block(), junction);
        if (state == null) {
            return false;
        }
        state.initEnterDirection();
        TrackWalkingPoint wp = new TrackWalkingPoint(state);
        wp.skipFirst();
        return wp.moveFull();
    }

    public static boolean isUpsideDownRailSupport(Block block) {
        BlockData blockdata = WorldUtil.getBlockData((Block)block);
        if (blockdata == BlockData.AIR) {
            return false;
        }
        if (blockdata.isSuffocating(block)) {
            return true;
        }
        return TCConfig.upsideDownSupportedByAll && blockdata.canSupportOnFace(block, BlockFace.DOWN);
    }

    public static int getDefaultDisplayedBlockOffset() {
        return 6;
    }

    public static Optional<Set<String>> getConfigStringSetOptional(ConfigurationNode config, String key) {
        if (config.contains(key)) {
            List configList = config.getList(key, String.class);
            HashSet resultSet = new HashSet(configList);
            return Optional.of(Collections.unmodifiableSet(resultSet));
        }
        return Optional.empty();
    }

    public static Optional<List<String>> getConfigStringListOptional(ConfigurationNode config, String key) {
        if (config.contains(key)) {
            List configList = config.getList(key, String.class);
            ArrayList listCopy = new ArrayList(configList);
            return Optional.of(Collections.unmodifiableList(listCopy));
        }
        return Optional.empty();
    }

    public static void setConfigStringCollectionOptional(ConfigurationNode config, String key, Optional<? extends Collection<String>> value) {
        if (value.isPresent()) {
            LogicUtil.synchronizeList((List)config.getList(key, String.class), value.get(), (LogicUtil.ItemSynchronizer)new LogicUtil.ItemSynchronizer<String, String>(){

                public boolean isItem(String item, String value) {
                    return Objects.equals(item, value);
                }

                public String onAdded(String value) {
                    return value;
                }

                public void onRemoved(String item) {
                }
            });
        } else {
            config.remove(key);
        }
    }

    public static <T> Optional<T> getConfigOptional(ConfigurationNode config, String key, Class<T> type) {
        if (config.contains(key)) {
            return Optional.ofNullable(config.get(key, type, null));
        }
        return Optional.empty();
    }

    public static void setConfigOptional(ConfigurationNode config, String key, Optional<?> value) {
        if (value.isPresent()) {
            config.set(key, value.get());
        } else if (config.contains(key)) {
            ConfigurationNode parent;
            String parentPath;
            config.remove(key);
            for (YamlPath parentYamlPath = YamlPath.create((String)key).parent(); parentYamlPath != YamlPath.ROOT && config.isNode(parentPath = parentYamlPath.toString()) && (parent = config.getNode(parentPath)).isEmpty(); parentYamlPath = parentYamlPath.parent()) {
                parent.remove();
            }
        }
    }

    public static BlockData getBlockDataOfPhysicsEvent(BlockPhysicsEvent event) {
        return BlockPhysicsEventDataAccessor.INSTANCE.get(event);
    }

    public static Player findPlayer(CommandSender sender, String name) {
        if (name.equals("@p")) {
            BoundingRange.Axis axis = BoundingRange.Axis.forSender(sender);
            if (axis.world == null) {
                sender.sendMessage(ChatColor.RED + "Can only use @p executing as a Player or CommandBlock");
                return null;
            }
            Iterator iter = axis.world.getPlayers().iterator();
            if (!iter.hasNext()) {
                sender.sendMessage(ChatColor.RED + "There is no player nearby");
                return null;
            }
            Player result = (Player)iter.next();
            Location tmpLoc = result.getLocation();
            double lowestDistance = axis.distanceSquared(tmpLoc);
            while (iter.hasNext()) {
                Player p = (Player)iter.next();
                double distance = axis.distanceSquared(p.getLocation(tmpLoc));
                if (!(distance < lowestDistance)) continue;
                lowestDistance = distance;
                result = p;
            }
            return result;
        }
        Player p = Bukkit.getPlayer((String)name);
        if (p == null) {
            sender.sendMessage(ChatColor.RED + "Failed to find player with name " + name + ": not online");
        }
        return p;
    }

    public static boolean hasPaperViewDistanceSupport() {
        try {
            Player.class.getMethod("setViewDistance", Integer.TYPE);
            Player.class.getMethod("setNoTickViewDistance", Integer.TYPE);
            Player.class.getMethod("setSendViewDistance", Integer.TYPE);
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static boolean hasPaperCustomTrackingRangeSupport() {
        try {
            Entity.class.getMethod("setCustomTrackingRange", Integer.TYPE);
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static double absMaxAxis(Vector v) {
        return Math.max(Math.max(Math.abs(v.getX()), Math.abs(v.getY())), Math.abs(v.getZ()));
    }

    public static double absMinAxis(Vector v) {
        return Math.min(Math.min(Math.abs(v.getX()), Math.abs(v.getY())), Math.abs(v.getZ()));
    }

    public static Location getRealEyeLocation(Player player) {
        Location eye;
        CartAttachmentSeat seat;
        MinecartMember<?> member = MinecartMemberStore.getFromEntity(player.getVehicle());
        if (member != null && (seat = member.getAttachments().findSeat((Entity)player)) != null && (eye = seat.getFirstPersonEyeLocation()) != null) {
            return eye;
        }
        return player.getEyeLocation();
    }

    public static ClientboundSetEquipmentPacketHandle createPlayerEquipmentPacket(int entityId, EquipmentSlot slot, ItemStack itemStack) {
        return ClientboundSetEquipmentPacketHandle.createNew((ClientboundSetEquipmentPacketHandle.OwnerType)ClientboundSetEquipmentPacketHandle.OwnerType.PLAYER, (int)entityId, (EquipmentSlot)slot, (ItemStack)itemStack);
    }

    public static ClientboundSetEquipmentPacketHandle createNonPlayerEquipmentPacket(int entityId, EquipmentSlot slot, ItemStack itemStack) {
        return ClientboundSetEquipmentPacketHandle.createNew((ClientboundSetEquipmentPacketHandle.OwnerType)ClientboundSetEquipmentPacketHandle.OwnerType.NON_PLAYER, (int)entityId, (EquipmentSlot)slot, (ItemStack)itemStack);
    }

    public static <T> List<T> filterList(List<T> list, Predicate<T> filter) {
        return Util.filterAndMapList(list, filter, null);
    }

    public static <I, O> List<O> filterAndMapList(List<I> list, Predicate<I> filter, Function<I, O> mapper) {
        return Util.filterAndMultiMapList(list, filter, mapper == null ? null : i -> Collections.singletonList(mapper.apply(i)));
    }

    public static <I, O> List<O> filterAndMultiMapList(List<I> list, Predicate<I> filter, Function<I, Collection<O>> mapper) {
        int numItems = list.size();
        if (numItems == 0) {
            return Collections.emptyList();
        }
        if (numItems == 1) {
            I first = list.get(0);
            if (!filter.test(first)) {
                return Collections.emptyList();
            }
            if (mapper != null) {
                Collection<O> results = mapper.apply(first);
                int numResults = results.size();
                if (numResults == 0) {
                    return Collections.emptyList();
                }
                if (numResults == 1) {
                    return Collections.singletonList(results.iterator().next());
                }
                return Collections.unmodifiableList(new ArrayList<O>(results));
            }
            return Collections.singletonList(first);
        }
        if (mapper != null) {
            ArrayList<O> result = new ArrayList<O>(numItems);
            for (int i = 0; i < numItems; ++i) {
                I input = list.get(i);
                if (!filter.test(input)) continue;
                result.addAll(mapper.apply(input));
            }
            return Collections.unmodifiableList(result);
        }
        for (int i = 0; i < numItems; ++i) {
            int j;
            I input = list.get(i);
            if (filter.test(input)) continue;
            ArrayList<I> result = new ArrayList<I>(numItems - 1);
            for (j = 0; j < i; ++j) {
                result.add(list.get(j));
            }
            for (j = i + 1; j < numItems; ++j) {
                input = list.get(j);
                if (!filter.test(input)) continue;
                result.add(input);
            }
            return Collections.unmodifiableList(result);
        }
        return Collections.unmodifiableList(list);
    }

    public static byte[] readByteArray(InputStream stream) throws IOException {
        byte[] data = new byte[Util.readVariableLengthInt(stream)];
        if (stream instanceof DataInputStream) {
            ((DataInputStream)stream).readFully(data);
        } else {
            int numRead;
            int offset = 0;
            for (int remaining = data.length; remaining > 0; remaining -= numRead) {
                numRead = stream.read(data, offset, remaining);
                if (numRead <= 0) {
                    throw new EOFException();
                }
                offset += numRead;
            }
        }
        return data;
    }

    public static void writeByteArray(OutputStream stream, byte[] array) throws IOException {
        Util.writeVariableLengthInt(stream, array.length);
        stream.write(array);
    }

    public static int readVariableLengthInt(InputStream stream) throws IOException {
        int b;
        int value = 0;
        do {
            if ((b = stream.read()) == -1) {
                throw new EOFException("Unexpected end of stream");
            }
            value <<= 7;
            value |= b & 0x7F;
        } while ((b & 0x80) != 0);
        return value;
    }

    public static void writeVariableLengthInt(OutputStream stream, int value) throws IOException {
        for (int numExtraBits = (32 - Integer.numberOfLeadingZeros(value)) / 7 * 7; numExtraBits > 0; numExtraBits -= 7) {
            stream.write(0x80 | value >> numExtraBits & 0x7F);
        }
        stream.write(value & 0x7F);
    }

    private static TeleportPositionMethod findRelativeTeleportMethod() {
        try {
            Class<?> flagsClass = Class.forName("io.papermc.paper.entity.TeleportFlag");
            Class<?> relativeFlagsClass = Class.forName("io.papermc.paper.entity.TeleportFlag$Relative");
            Object[] relativeRotFlags = LogicUtil.createArray(flagsClass, (int)2);
            relativeRotFlags[0] = relativeFlagsClass.getField("YAW").get(null);
            relativeRotFlags[1] = relativeFlagsClass.getField("PITCH").get(null);
            FastMethod teleportWithFlagsMethod = new FastMethod();
            teleportWithFlagsMethod.init(Entity.class.getMethod("teleport", Location.class, relativeRotFlags.getClass()));
            teleportWithFlagsMethod.forceInitialization();
            return (entity, to) -> {
                if (entity instanceof Player) {
                    return (Boolean)teleportWithFlagsMethod.invoke((Object)entity, (Object)to, (Object)relativeRotFlags);
                }
                return entity.teleport(to);
            };
        }
        catch (Throwable throwable) {
            return Entity::teleport;
        }
    }

    public static boolean teleportPosition(Entity entity, Location to) {
        Location toCorrected = entity instanceof LivingEntity ? ((LivingEntity)entity).getEyeLocation() : entity.getLocation();
        toCorrected.setWorld(to.getWorld());
        toCorrected.setX(to.getX());
        toCorrected.setY(to.getY());
        toCorrected.setZ(to.getZ());
        return TELEPORT_POSITION_METHOD.teleportPosition(entity, toCorrected);
    }

    public static void resetPlayerAwaitingTeleport(Player player) {
        ServerGamePacketListenerImplHandle connection = ServerGamePacketListenerImplHandle.forPlayer((Player)player);
        if (connection != null) {
            connection.resetAwaitTeleport();
        }
    }

    public static String unescapeString(String str) {
        int len = str.length();
        if (len == 0 || str.charAt(0) != '\"') {
            return str;
        }
        StringBuilder newStr = new StringBuilder(len - 1);
        boolean escaped = false;
        for (int i = 1; i < len; ++i) {
            char c = str.charAt(i);
            if (escaped) {
                escaped = false;
                newStr.append(c);
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '\"') break;
            newStr.append(c);
        }
        return newStr.toString();
    }

    @Deprecated
    public static String escapeQuotedArgument(String text) {
        return QuoteEscapedString.quoteEscape(text).getEscaped();
    }

    public static Color toColor(ChatColor chatColor) {
        return COLOR_TO_RGB.getOrDefault(chatColor, UNKNOWN_CHAT_COLOR);
    }

    private static DamageEventConstructor createDamageEventConstructor() {
        if (Common.hasCapability((String)"Common:DamageSource:CreateVehicleDamageEvent")) {
            return new DamageEventConstructor(){

                @Override
                public VehicleDamageEvent createDamageEvent(Vehicle vehicle, DamageSource damageSource, Entity attacker, double damage) {
                    return damageSource.createVehicleDamageEvent(vehicle, attacker, damage);
                }

                @Override
                public VehicleDestroyEvent createDestroyEvent(Vehicle vehicle, DamageSource damageSource, Entity attacker) {
                    return damageSource.createVehicleDestroyEvent(vehicle, attacker);
                }
            };
        }
        return new DamageEventConstructor(){

            @Override
            public VehicleDamageEvent createDamageEvent(Vehicle vehicle, DamageSource damageSource, Entity attacker, double damage) {
                return new VehicleDamageEvent(vehicle, attacker, damage);
            }

            @Override
            public VehicleDestroyEvent createDestroyEvent(Vehicle vehicle, DamageSource damageSource, Entity attacker) {
                return new VehicleDestroyEvent(vehicle, attacker);
            }
        };
    }

    static {
        COLOR_TO_RGB.put(ChatColor.BLACK, Color.fromRGB((int)0, (int)0, (int)0));
        COLOR_TO_RGB.put(ChatColor.DARK_BLUE, Color.fromRGB((int)0, (int)0, (int)168));
        COLOR_TO_RGB.put(ChatColor.DARK_GREEN, Color.fromRGB((int)0, (int)168, (int)0));
        COLOR_TO_RGB.put(ChatColor.DARK_AQUA, Color.fromRGB((int)0, (int)168, (int)168));
        COLOR_TO_RGB.put(ChatColor.DARK_RED, Color.fromRGB((int)168, (int)0, (int)0));
        COLOR_TO_RGB.put(ChatColor.DARK_PURPLE, Color.fromRGB((int)168, (int)0, (int)168));
        COLOR_TO_RGB.put(ChatColor.GOLD, Color.fromRGB((int)252, (int)168, (int)0));
        COLOR_TO_RGB.put(ChatColor.GRAY, Color.fromRGB((int)168, (int)168, (int)168));
        COLOR_TO_RGB.put(ChatColor.DARK_GRAY, Color.fromRGB((int)84, (int)84, (int)84));
        COLOR_TO_RGB.put(ChatColor.BLUE, Color.fromRGB((int)84, (int)84, (int)252));
        COLOR_TO_RGB.put(ChatColor.GREEN, Color.fromRGB((int)84, (int)252, (int)84));
        COLOR_TO_RGB.put(ChatColor.AQUA, Color.fromRGB((int)84, (int)252, (int)252));
        COLOR_TO_RGB.put(ChatColor.RED, Color.fromRGB((int)252, (int)84, (int)84));
        COLOR_TO_RGB.put(ChatColor.LIGHT_PURPLE, Color.fromRGB((int)252, (int)84, (int)252));
        COLOR_TO_RGB.put(ChatColor.YELLOW, Color.fromRGB((int)252, (int)252, (int)84));
        COLOR_TO_RGB.put(ChatColor.WHITE, Color.fromRGB((int)252, (int)252, (int)252));
        DAMAGE_EVENT_CONSTRUCTOR = Util.createDamageEventConstructor();
    }

    private static interface TeleportPositionMethod {
        public boolean teleportPosition(Entity var1, Location var2);
    }

    public static interface DamageEventConstructor {
        public VehicleDamageEvent createDamageEvent(Vehicle var1, DamageSource var2, Entity var3, double var4);

        public VehicleDestroyEvent createDestroyEvent(Vehicle var1, DamageSource var2, Entity var3);
    }
}

