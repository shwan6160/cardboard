/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Difficulty
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.WorldType
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.map.MapCursor
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.conversion;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.type.PropertyConverter;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.type.InputConverter;
import com.bergerkiller.mountiplex.conversion.type.NullConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.util.UUID;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Conversion {
    public static final Converter NONE;
    public static final InputConverter<String> toString;
    public static final InputConverter<Byte> toByte;
    public static final InputConverter<Short> toShort;
    public static final InputConverter<Integer> toInt;
    public static final InputConverter<Long> toLong;
    public static final InputConverter<Float> toFloat;
    public static final InputConverter<Double> toDouble;
    public static final InputConverter<Boolean> toBool;
    public static final InputConverter<Character> toChar;
    public static final InputConverter<Object> toEntityHandle;
    public static final InputConverter<Object> toEntityPlayerHandle;
    public static final InputConverter<Object> toWorldHandle;
    public static final InputConverter<Object> toChunkHandle;
    public static final InputConverter<Object> toItemStackHandle;
    public static final InputConverter<Object> toItemHandle;
    public static final InputConverter<Object> toTileEntityHandle;
    public static final InputConverter<Object> toInventoryHandle;
    public static final InputConverter<Object> toDataWatcherHandle;
    public static final InputConverter<Object> toDataWatcherObjectHandle;
    public static final InputConverter<Object> toDataWatcherItemHandle;
    public static final InputConverter<Object> toNBTTagHandle;
    public static final InputConverter<Object> toBlockHandle;
    public static final InputConverter<Object> toGameModeHandle;
    public static final InputConverter<Object> toDifficultyHandle;
    public static final InputConverter<Object> toPacketHandle;
    public static final InputConverter<Object> toVec3DHandle;
    public static final InputConverter<Object> toChunkCoordIntPairHandle;
    public static final InputConverter<Object> toBlockPositionHandle;
    public static final InputConverter<Object> toPlayerAbilitiesHandle;
    public static final InputConverter<Object> toEntityTrackerHandle;
    public static final InputConverter<Object> toLongHashSetHandle;
    public static final InputConverter<Object> toIntHashMapHandle;
    public static final InputConverter<Object> toBlockDataHandle;
    public static final InputConverter<Object> toChunkSectionHandle;
    public static final InputConverter<Object> toMobEffectList;
    public static final InputConverter<Object> toMobEffect;
    public static final InputConverter<Object> toMapIconHandle;
    public static final InputConverter<Object> toChatComponentHandle;
    public static final InputConverter<Object> toEnumItemSlotHandle;
    public static final InputConverter<Entity> toEntity;
    public static final InputConverter<Player> toPlayer;
    public static final InputConverter<HumanEntity> toHumanEntity;
    public static final InputConverter<Item> toItem;
    public static final InputConverter<World> toWorld;
    public static final InputConverter<Chunk> toChunk;
    public static final InputConverter<Block> toBlock;
    public static final InputConverter<BlockState> toBlockState;
    public static final InputConverter<CommonTag> toCommonTag;
    public static final InputConverter<DataWatcher> toDataWatcher;
    public static final InputConverter<DataWatcher.Key<?>> toDataWatcherKey;
    public static final InputConverter<DataWatcher.Item<?>> toDataWatcherItem;
    public static final InputConverter<ItemStack> toItemStack;
    public static final InputConverter<Material> toMaterial;
    public static final InputConverter<Inventory> toInventory;
    public static final InputConverter<Difficulty> toDifficulty;
    public static final InputConverter<WorldType> toWorldType;
    public static final InputConverter<GameMode> toGameMode;
    public static final InputConverter<CommonPacket> toCommonPacket;
    public static final InputConverter<IntVector2> toIntVector2;
    public static final InputConverter<IntVector3> toIntVector3;
    public static final InputConverter<Vector> toVector;
    public static final InputConverter<PlayerAbilities> toPlayerAbilities;
    public static final InputConverter<EntityTracker> toEntityTracker;
    public static final InputConverter<LongHashSet> toLongHashSet;
    public static final InputConverter<IntHashMap<Object>> toIntHashMap;
    public static final InputConverter<BlockData> toBlockData;
    public static final InputConverter<ChunkSection> toChunkSection;
    public static final InputConverter<PotionEffectType> toPotionEffectType;
    public static final InputConverter<PotionEffect> toPotionEffect;
    public static final InputConverter<MapCursor> toMapCursor;
    public static final InputConverter<ChatText> toChatText;
    public static final InputConverter<ItemStack[]> toItemStackArr;
    public static final InputConverter<Object[]> toItemStackHandleArr;
    public static final InputConverter<Object[]> toObjectArr;
    public static final InputConverter<boolean[]> toBoolArr;
    public static final InputConverter<char[]> toCharArr;
    public static final InputConverter<byte[]> toByteArr;
    public static final InputConverter<short[]> toShortArr;
    public static final InputConverter<int[]> toIntArr;
    public static final InputConverter<long[]> toLongArr;
    public static final InputConverter<float[]> toFloatArr;
    public static final InputConverter<double[]> toDoubleArr;
    @Deprecated
    public static final PropertyConverter<Integer> toItemId;
    public static final PropertyConverter<Material> toItemMaterial;
    public static final PropertyConverter<Integer> toPaintingFacingId;
    public static final PropertyConverter<Object> toPaintingFacing;
    public static final PropertyConverter<EntityType> toMinecartType;
    public static final PropertyConverter<Object> toGameProfileFromId;
    public static final PropertyConverter<UUID> toGameProfileId;

    private static InputConverter<Object> getConverterToHandle(String className) {
        Class<?> type = CommonUtil.getClass(className, false);
        if (type == null) {
            throw new IllegalArgumentException("Class does not exist for converter: " + className);
        }
        return Conversion.getConverterTo(type);
    }

    private static <T> InputConverter<T> getConverterTo(TypeDeclaration type) {
        return com.bergerkiller.mountiplex.conversion.Conversion.find(type);
    }

    private static <T> InputConverter<T> getConverterTo(Class<T> type) {
        return com.bergerkiller.mountiplex.conversion.Conversion.find(type);
    }

    public static <T> T convert(Object input, T defaultValue) {
        return (T)Conversion.convert(input, defaultValue.getClass(), defaultValue);
    }

    public static <T> T convert(Object input, Class<T> type, T defaultValue) {
        return com.bergerkiller.mountiplex.conversion.Conversion.find(type).convert(input, defaultValue);
    }

    static {
        if (!CommonBootstrap.isCommonServerInitialized()) {
            throw new IllegalStateException("CommonBootstrap must be bootstrapped before conversion can be used");
        }
        NONE = new NullConverter(Object.class, Object.class);
        toString = Conversion.getConverterTo(String.class);
        toByte = Conversion.getConverterTo(Byte.TYPE);
        toShort = Conversion.getConverterTo(Short.TYPE);
        toInt = Conversion.getConverterTo(Integer.TYPE);
        toLong = Conversion.getConverterTo(Long.TYPE);
        toFloat = Conversion.getConverterTo(Float.TYPE);
        toDouble = Conversion.getConverterTo(Double.TYPE);
        toBool = Conversion.getConverterTo(Boolean.TYPE);
        toChar = Conversion.getConverterTo(Character.TYPE);
        toEntityHandle = Conversion.getConverterToHandle("net.minecraft.world.entity.Entity");
        toEntityPlayerHandle = Conversion.getConverterToHandle("net.minecraft.server.level.ServerPlayer");
        toWorldHandle = Conversion.getConverterToHandle("net.minecraft.world.level.Level");
        toChunkHandle = Conversion.getConverterToHandle("net.minecraft.world.level.chunk.LevelChunk");
        toItemStackHandle = Conversion.getConverterToHandle("net.minecraft.world.item.ItemStack");
        toItemHandle = Conversion.getConverterToHandle("net.minecraft.world.item.Item");
        toTileEntityHandle = Conversion.getConverterToHandle("net.minecraft.world.level.block.entity.BlockEntity");
        toInventoryHandle = Conversion.getConverterToHandle("net.minecraft.world.Container");
        toDataWatcherHandle = Conversion.getConverterToHandle("net.minecraft.network.syncher.SynchedEntityData");
        toDataWatcherObjectHandle = Conversion.getConverterToHandle("net.minecraft.network.syncher.EntityDataAccessor");
        toDataWatcherItemHandle = Conversion.getConverterToHandle("net.minecraft.network.syncher.SynchedEntityData.DataItem");
        toNBTTagHandle = Conversion.getConverterToHandle("net.minecraft.nbt.Tag");
        toBlockHandle = Conversion.getConverterToHandle("net.minecraft.world.level.block.Block");
        toGameModeHandle = Conversion.getConverterToHandle("net.minecraft.world.level.GameType");
        toDifficultyHandle = Conversion.getConverterToHandle("net.minecraft.world.Difficulty");
        toPacketHandle = Conversion.getConverterToHandle("net.minecraft.network.protocol.Packet");
        toVec3DHandle = Conversion.getConverterToHandle("net.minecraft.world.phys.Vec3");
        toChunkCoordIntPairHandle = Conversion.getConverterToHandle("net.minecraft.world.level.ChunkPos");
        toBlockPositionHandle = Conversion.getConverterToHandle("net.minecraft.core.BlockPos");
        toPlayerAbilitiesHandle = Conversion.getConverterToHandle("net.minecraft.world.entity.player.Abilities");
        toEntityTrackerHandle = Conversion.getConverterToHandle("net.minecraft.server.level.EntityTracker");
        toLongHashSetHandle = Conversion.getConverterToHandle("com.bergerkiller.bukkit.common.internal.LongHashSet");
        toIntHashMapHandle = Conversion.getConverterToHandle("net.minecraft.util.IntHashMap");
        toBlockDataHandle = Conversion.getConverterToHandle("net.minecraft.world.level.block.state.BlockState");
        toChunkSectionHandle = Conversion.getConverterToHandle("net.minecraft.world.level.chunk.LevelChunkSection");
        toMobEffectList = Conversion.getConverterToHandle("net.minecraft.world.effect.MobEffect");
        toMobEffect = Conversion.getConverterToHandle("net.minecraft.world.effect.MobEffectInstance");
        toMapIconHandle = Conversion.getConverterToHandle("net.minecraft.world.level.saveddata.maps.MapDecoration");
        toChatComponentHandle = Conversion.getConverterToHandle("net.minecraft.network.chat.Component");
        toEnumItemSlotHandle = Conversion.getConverterToHandle("net.minecraft.world.entity.EquipmentSlot");
        toEntity = Conversion.getConverterTo(Entity.class);
        toPlayer = Conversion.getConverterTo(Player.class);
        toHumanEntity = Conversion.getConverterTo(HumanEntity.class);
        toItem = Conversion.getConverterTo(Item.class);
        toWorld = Conversion.getConverterTo(World.class);
        toChunk = Conversion.getConverterTo(Chunk.class);
        toBlock = Conversion.getConverterTo(Block.class);
        toBlockState = Conversion.getConverterTo(BlockState.class);
        toCommonTag = Conversion.getConverterTo(CommonTag.class);
        toDataWatcher = Conversion.getConverterTo(DataWatcher.class);
        toDataWatcherKey = (InputConverter)LogicUtil.unsafeCast(Conversion.getConverterTo(DataWatcher.Key.class));
        toDataWatcherItem = (InputConverter)LogicUtil.unsafeCast(Conversion.getConverterTo(DataWatcher.Item.class));
        toItemStack = Conversion.getConverterTo(ItemStack.class);
        toMaterial = Conversion.getConverterTo(Material.class);
        toInventory = Conversion.getConverterTo(Inventory.class);
        toDifficulty = Conversion.getConverterTo(Difficulty.class);
        toWorldType = Conversion.getConverterTo(WorldType.class);
        toGameMode = Conversion.getConverterTo(GameMode.class);
        toCommonPacket = Conversion.getConverterTo(CommonPacket.class);
        toIntVector2 = Conversion.getConverterTo(IntVector2.class);
        toIntVector3 = Conversion.getConverterTo(IntVector3.class);
        toVector = Conversion.getConverterTo(Vector.class);
        toPlayerAbilities = Conversion.getConverterTo(PlayerAbilities.class);
        toEntityTracker = Conversion.getConverterTo(EntityTracker.class);
        toLongHashSet = Conversion.getConverterTo(LongHashSet.class);
        toIntHashMap = (InputConverter)LogicUtil.unsafeCast(Conversion.getConverterTo(IntHashMap.class));
        toBlockData = Conversion.getConverterTo(BlockData.class);
        toChunkSection = Conversion.getConverterTo(ChunkSection.class);
        toPotionEffectType = Conversion.getConverterTo(PotionEffectType.class);
        toPotionEffect = Conversion.getConverterTo(PotionEffect.class);
        toMapCursor = Conversion.getConverterTo(MapCursor.class);
        toChatText = Conversion.getConverterTo(ChatText.class);
        toItemStackArr = Conversion.getConverterTo(ItemStack[].class);
        toItemStackHandleArr = Conversion.getConverterTo(TypeDeclaration.createArray(CommonUtil.getClass("net.minecraft.world.item.ItemStack", false)));
        toObjectArr = Conversion.getConverterTo(Object[].class);
        toBoolArr = Conversion.getConverterTo(boolean[].class);
        toCharArr = Conversion.getConverterTo(char[].class);
        toByteArr = Conversion.getConverterTo(byte[].class);
        toShortArr = Conversion.getConverterTo(short[].class);
        toIntArr = Conversion.getConverterTo(int[].class);
        toLongArr = Conversion.getConverterTo(long[].class);
        toFloatArr = Conversion.getConverterTo(float[].class);
        toDoubleArr = Conversion.getConverterTo(double[].class);
        toItemId = PropertyConverter.toItemId;
        toItemMaterial = PropertyConverter.toItemMaterial;
        toPaintingFacingId = PropertyConverter.toPaintingFacingId;
        toPaintingFacing = PropertyConverter.toPaintingFacing;
        toMinecartType = PropertyConverter.toMinecartType;
        toGameProfileFromId = PropertyConverter.toGameProfileFromId;
        toGameProfileId = PropertyConverter.toGameProfileId;
    }
}

