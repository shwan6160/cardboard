/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.Difficulty
 *  org.bukkit.GameMode
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Entity
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
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.net.minecraft.nbt.TagHandle;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class DuplexConversion {
    public static final DuplexConverter NONE;
    public static final DuplexConverter<Object, Entity> entity;
    public static final DuplexConverter<Object, Player> player;
    public static final DuplexConverter<Object[], ItemStack[]> itemStackArr;
    public static final DuplexConverter<Object, World> world;
    public static final DuplexConverter<Object, Chunk> chunk;
    public static final DuplexConverter<Object, ItemStack> itemStack;
    public static final DuplexConverter<Object, Inventory> inventory;
    public static final DuplexConverter<Object, Difficulty> difficulty;
    public static final DuplexConverter<Object, GameMode> gameMode;
    public static final DuplexConverter<Object, DataWatcher> dataWatcher;
    public static final DuplexConverter<Object, DataWatcher.Key<?>> dataWatcherKey;
    public static final DuplexConverter<Object, DataWatcher.Item<?>> dataWatcherItem;
    public static final DuplexConverter<Object, DataWatcher.PackedItem<?>> dataWatcherPackedItem;
    public static final DuplexConverter<Object, CommonTag> commonTag;
    public static final DuplexConverter<Object, CommonTagCompound> commonTagCompound;
    public static final DuplexConverter<Object, CommonTagList> commonTagList;
    public static final DuplexConverter<Integer, Object> paintingFacing;
    public static final DuplexConverter<Object, IntVector3> blockPosition;
    public static final DuplexConverter<Object, IntVector2> chunkIntPair;
    public static final DuplexConverter<Object, Vector> vector;
    public static final DuplexConverter<Object, PlayerAbilities> playerAbilities;
    public static final DuplexConverter<Object, EntityTracker> entityTracker;
    public static final DuplexConverter<Object, LongHashSet> longHashSet;
    public static final DuplexConverter<Object, IntHashMap<Object>> intHashMap;
    public static final DuplexConverter<Object, BlockState> blockState;
    public static final DuplexConverter<Object, Material> block;
    public static final DuplexConverter<Object, Material> item;
    public static final DuplexConverter<Object, UUID> gameProfileId;
    public static final DuplexConverter<Object, BlockData> blockData;
    public static final DuplexConverter<Object, ChunkSection> chunkSection;
    public static final DuplexConverter<Object, PotionEffectType> potionEffectType;
    public static final DuplexConverter<Object, PotionEffect> potionEffect;
    public static final DuplexConverter<Object, MapCursor> mapCursor;
    public static final DuplexConverter<Object, ChatText> chatText;
    public static final DuplexConverter<List<Object>, List<Entity>> entityList;
    public static final DuplexConverter<Collection<Object>, Collection<Entity>> entityCollection;
    public static final DuplexConverter<List<Object>, List<Player>> playerList;
    public static final DuplexConverter<Set<Object>, Set<Player>> playerSet;
    public static final DuplexConverter<Collection<Object>, Collection<Chunk>> chunkCollection;
    public static final DuplexConverter<List<Object>, List<ItemStack>> itemStackList;
    public static final DuplexConverter<List<Object>, List<DataWatcher>> dataWatcherList;
    public static final DuplexConverter<List<Object>, List<DataWatcher.Item<?>>> dataWatcherItemList;
    public static final DuplexConverter<Collection<Object>, Collection<BlockState>> blockStateCollection;
    public static final DuplexConverter<Object[], ChatText[]> chatTextArray;
    public static final DuplexConverter<Object[], ChunkSection[]> chunkSectionArray;
    public static final DuplexConverter<Object[], MapCursor[]> mapCursorArray;
    public static final DuplexConverter<Object, CommonTag> nbtBase_commonTag;
    public static final DuplexConverter<Object, CommonTag> nbtBase_commonTag_readOnly;
    public static final DuplexConverter<Object, TagHandle> nbtBase_nbtBaseHandle;
    public static final DuplexConverter<String, String> string_string;

    private static final <T> T pairElem(Class<?> type, DuplexConverter<?, ?> elementConverter) {
        TypeDeclaration input = TypeDeclaration.createGeneric(type, elementConverter.input);
        TypeDeclaration output = TypeDeclaration.createGeneric(type, elementConverter.output);
        return (T)com.bergerkiller.mountiplex.conversion.Conversion.findDuplex(input, output);
    }

    private static final <T> T pairArray(DuplexConverter<?, ?> elementConverter) {
        TypeDeclaration input = TypeDeclaration.createArray(elementConverter.input.type);
        TypeDeclaration output = TypeDeclaration.createArray(elementConverter.output.type);
        return (T)com.bergerkiller.mountiplex.conversion.Conversion.findDuplex(input, output);
    }

    private static final <T> T findByPath(String nmsClassName, Class<?> output) {
        Class<?> type = CommonUtil.getClass(nmsClassName, false);
        if (type == null) {
            throw new IllegalStateException("Class does not exist for duplex converter: " + nmsClassName);
        }
        return DuplexConversion.find(type, output);
    }

    private static final <T> T find(Class<?> input, Class<?> output) {
        Converter<?, ?> conv = com.bergerkiller.mountiplex.conversion.Conversion.find(input, output);
        if (conv == null) {
            throw new IllegalStateException("Failed to find converter from " + input + " to " + output);
        }
        Converter<?, ?> conv_rev = com.bergerkiller.mountiplex.conversion.Conversion.find(output, input);
        if (conv_rev == null) {
            throw new IllegalStateException("Failed to find reverse converter from " + output + " to " + input);
        }
        return (T)DuplexConverter.pair(conv, conv_rev);
    }

    static {
        if (!CommonBootstrap.isCommonServerInitialized()) {
            throw new IllegalStateException("CommonBootstrap must be bootstrapped before duplex conversion can be used");
        }
        NONE = DuplexConverter.createNull(TypeDeclaration.OBJECT);
        entity = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.world.entity.Entity", Entity.class);
        player = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.server.level.ServerPlayer", Player.class);
        itemStackArr = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.world.item.ItemStack[]", ItemStack[].class);
        world = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.server.level.ServerLevel", World.class);
        chunk = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.world.level.chunk.LevelChunk", Chunk.class);
        itemStack = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.world.item.ItemStack", ItemStack.class);
        inventory = DuplexConverter.pair(Conversion.toInventory, Conversion.toInventoryHandle);
        difficulty = DuplexConverter.pair(Conversion.toDifficulty, Conversion.toDifficultyHandle);
        gameMode = DuplexConverter.pair(Conversion.toGameMode, Conversion.toGameModeHandle);
        dataWatcher = DuplexConverter.pair(Conversion.toDataWatcher, Conversion.toDataWatcherHandle);
        dataWatcherKey = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.network.syncher.EntityDataAccessor", DataWatcher.Key.class);
        dataWatcherItem = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.network.syncher.SynchedEntityData.DataItem", DataWatcher.Item.class);
        dataWatcherPackedItem = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.network.syncher.SynchedEntityData.DataValue", DataWatcher.PackedItem.class);
        commonTag = DuplexConverter.pair(Conversion.toCommonTag, Conversion.toNBTTagHandle);
        commonTagCompound = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.nbt.CompoundTag", CommonTagCompound.class);
        commonTagList = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.nbt.ListTag", CommonTagList.class);
        paintingFacing = DuplexConverter.pair(Conversion.toPaintingFacing, Conversion.toPaintingFacingId);
        blockPosition = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.core.BlockPos", IntVector3.class);
        chunkIntPair = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.world.level.ChunkPos", IntVector2.class);
        vector = (DuplexConverter)DuplexConversion.findByPath("net.minecraft.world.phys.Vec3", Vector.class);
        playerAbilities = DuplexConverter.pair(Conversion.toPlayerAbilities, Conversion.toPlayerAbilitiesHandle);
        entityTracker = DuplexConverter.pair(Conversion.toEntityTracker, Conversion.toEntityTrackerHandle);
        longHashSet = DuplexConverter.pair(Conversion.toLongHashSet, Conversion.toLongHashSetHandle);
        intHashMap = DuplexConverter.pair(Conversion.toIntHashMap, Conversion.toIntHashMapHandle);
        blockState = DuplexConverter.pair(Conversion.toBlockState, Conversion.toTileEntityHandle);
        block = DuplexConverter.pair(Conversion.toMaterial, Conversion.toBlockHandle);
        item = DuplexConverter.pair(Conversion.toMaterial, Conversion.toItemHandle);
        gameProfileId = DuplexConverter.pair(Conversion.toGameProfileId, Conversion.toGameProfileFromId);
        blockData = DuplexConverter.pair(Conversion.toBlockData, Conversion.toBlockDataHandle);
        chunkSection = DuplexConverter.pair(Conversion.toChunkSection, Conversion.toChunkSectionHandle);
        potionEffectType = DuplexConverter.pair(Conversion.toPotionEffectType, Conversion.toMobEffectList);
        potionEffect = DuplexConverter.pair(Conversion.toPotionEffect, Conversion.toMobEffect);
        mapCursor = DuplexConverter.pair(Conversion.toMapCursor, Conversion.toMapIconHandle);
        chatText = DuplexConverter.pair(Conversion.toChatText, Conversion.toChatComponentHandle);
        entityList = (DuplexConverter)DuplexConversion.pairElem(List.class, entity);
        entityCollection = (DuplexConverter)DuplexConversion.pairElem(Collection.class, entity);
        playerList = (DuplexConverter)DuplexConversion.pairElem(List.class, player);
        playerSet = (DuplexConverter)DuplexConversion.pairElem(Set.class, player);
        chunkCollection = (DuplexConverter)DuplexConversion.pairElem(Collection.class, chunk);
        itemStackList = (DuplexConverter)DuplexConversion.pairElem(List.class, itemStack);
        dataWatcherList = (DuplexConverter)DuplexConversion.pairElem(List.class, dataWatcher);
        dataWatcherItemList = (DuplexConverter)DuplexConversion.pairElem(List.class, dataWatcherItem);
        blockStateCollection = (DuplexConverter)DuplexConversion.pairElem(Collection.class, blockState);
        chatTextArray = (DuplexConverter)DuplexConversion.pairArray(chatText);
        chunkSectionArray = (DuplexConverter)DuplexConversion.pairArray(chunkSection);
        mapCursorArray = (DuplexConverter)DuplexConversion.pairArray(mapCursor);
        nbtBase_commonTag = new DuplexConverter<Object, CommonTag>(CommonUtil.getClass("net.minecraft.nbt.Tag", false), CommonTag.class){

            @Override
            public CommonTag convertInput(Object value) {
                return TagHandle.createHandleForData(value).toCommonTag();
            }

            @Override
            public Object convertOutput(CommonTag value) {
                return value.getRawHandle();
            }
        };
        nbtBase_commonTag_readOnly = new DuplexConverter<Object, CommonTag>(CommonUtil.getClass("net.minecraft.nbt.Tag", false), CommonTag.class){

            @Override
            public CommonTag convertInput(Object value) {
                return CommonTag.makeReadOnly(TagHandle.createHandleForData(value).toCommonTag());
            }

            @Override
            public Object convertOutput(CommonTag value) {
                return value.getRawHandle();
            }
        };
        nbtBase_nbtBaseHandle = new DuplexConverter<Object, TagHandle>(CommonUtil.getClass("net.minecraft.nbt.Tag", false), TagHandle.class){

            @Override
            public TagHandle convertInput(Object value) {
                return TagHandle.createHandleForData(value);
            }

            @Override
            public Object convertOutput(TagHandle value) {
                return value.getRaw();
            }
        };
        string_string = DuplexConverter.createNull(TypeDeclaration.fromClass(String.class));
    }
}

