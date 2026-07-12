/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Art
 *  org.bukkit.ChatColor
 *  org.bukkit.Chunk
 *  org.bukkit.Difficulty
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.WorldType
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.entity.Entity
 *  org.bukkit.inventory.BeaconInventory
 *  org.bukkit.inventory.BrewerInventory
 *  org.bukkit.inventory.CraftingInventory
 *  org.bukkit.inventory.FurnaceInventory
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.MerchantInventory
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.material.MaterialData
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.common.conversion.type;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.conversion.blockstate.BlockStateConversion;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.proxy.NBTTagCompoundConsumer;
import com.bergerkiller.bukkit.common.inventory.CraftInputSlot;
import com.bergerkiller.bukkit.common.inventory.ItemParser;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.resources.BlockStateType;
import com.bergerkiller.bukkit.common.resources.ParticleType;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockStateChange;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.CustomModelData;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.HeightMap;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.InteractionResult;
import com.bergerkiller.bukkit.common.wrappers.InventoryClickType;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.MobSpawner;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPoint;
import com.bergerkiller.bukkit.common.wrappers.PlayerRespawnPointNearBlock;
import com.bergerkiller.generated.net.minecraft.ChatFormattingHandle;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.core.RotationsHandle;
import com.bergerkiller.generated.net.minecraft.core.Vec3iHandle;
import com.bergerkiller.generated.net.minecraft.nbt.CompoundTagHandle;
import com.bergerkiller.generated.net.minecraft.network.syncher.SynchedEntityDataHandle;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerPlayerHandle;
import com.bergerkiller.generated.net.minecraft.sounds.SoundEventHandle;
import com.bergerkiller.generated.net.minecraft.world.DifficultyHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectHandle;
import com.bergerkiller.generated.net.minecraft.world.effect.MobEffectInstanceHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypeHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EquipmentSlotHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeHandle;
import com.bergerkiller.generated.net.minecraft.world.inventory.AbstractContainerMenuHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.item.component.CustomModelDataHandle;
import com.bergerkiller.generated.net.minecraft.world.item.crafting.IngredientHandle;
import com.bergerkiller.generated.net.minecraft.world.level.ChunkPosHandle;
import com.bergerkiller.generated.net.minecraft.world.level.GameTypeHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.entity.BlockEntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.chunk.LevelChunkHandle;
import com.bergerkiller.generated.net.minecraft.world.level.levelgen.HeightmapHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.Vec3Handle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.CraftArtHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.block.data.CraftBlockDataHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryBeaconHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryBrewerHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryCraftingHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryFurnaceHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryMerchantHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftInventoryPlayerHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.inventory.CraftItemStackHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.conversion.annotations.ConverterMethod;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.Art;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class WrapperConversion {
    private static final BlockFace[] enumDirectionValues = new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};

    @ConverterMethod(input="net.minecraft.server.level.ServerPlayer.RespawnConfig", acceptsNull=true)
    public static PlayerRespawnPoint toRespawnPoint(Object respawnConfig) {
        if (respawnConfig == null) {
            return PlayerRespawnPoint.NONE;
        }
        return new PlayerRespawnPointNearBlock(ServerPlayerHandle.RespawnConfigHandle.createHandle(respawnConfig));
    }

    @ConverterMethod(output="net.minecraft.server.level.ServerPlayer.RespawnConfig")
    public static Object toRespawnConfigFromPoint(PlayerRespawnPoint respawnPoint) {
        if (respawnPoint instanceof PlayerRespawnPointNearBlock) {
            return ((PlayerRespawnPointNearBlock)respawnPoint).getHandle().getRaw();
        }
        return null;
    }

    @ConverterMethod(input="net.minecraft.world.item.component.CustomModelData")
    public static CustomModelData handleToCustomModelData(Object handle) {
        return new CustomModelData(CustomModelDataHandle.createHandle(handle));
    }

    @ConverterMethod(output="net.minecraft.world.item.component.CustomModelData")
    public static Object customModelDataToHandle(CustomModelData cmd) {
        return cmd.getRawHandle();
    }

    @ConverterMethod(output="java.util.function.Consumer<net.minecraft.nbt.CompoundTag>")
    public static Consumer<Object> createNBTTagCompoundConsumer(Consumer<CommonTagCompound> consumer) {
        return new NBTTagCompoundConsumer(consumer);
    }

    @ConverterMethod(input="net.minecraft.world.entity.ai.attributes.IAttribute", optional=true)
    public static Holder<AttributeHandle> iAttributeToHolder(Object nmsIAttribute) {
        return WrapperConversion.attributeBaseToHolder(nmsIAttribute);
    }

    @ConverterMethod(input="net.minecraft.world.entity.ai.attributes.Attribute")
    public static Holder<AttributeHandle> attributeBaseToHolder(Object nmsAttributeBase) {
        return Holder.directWrap(nmsAttributeBase, AttributeHandle::createHandle);
    }

    @ConverterMethod(output="net.minecraft.world.entity.ai.attributes.Attribute")
    public static Object holderToAttributeBase(Holder<AttributeHandle> attributeHolder) {
        return attributeHolder.rawValue();
    }

    @ConverterMethod
    public static Holder<MobEffectHandle> holderFromBukkit(PotionEffectType effectType) {
        return null;
    }

    @ConverterMethod
    public static PotionEffectType holderToBukkit(Holder<MobEffectHandle> MobEffectList2) {
        return null;
    }

    @ConverterMethod(input="net.minecraft.world.effect.MobEffect")
    public static Holder<MobEffectHandle> mobEffectListToHolder(Object nmsMobEffectList) {
        return Holder.directWrap(nmsMobEffectList, MobEffectHandle::createHandle);
    }

    @ConverterMethod(output="net.minecraft.world.effect.MobEffect")
    public static Object holderToMobEffectList(Holder<MobEffectHandle> holder) {
        return holder.rawValue();
    }

    @ConverterMethod(input="net.minecraft.nbt.CompoundTag")
    public static BlockStateChange deserializeBlockStateChange(Object nmsNBTTagCompoundMetadataHandle) {
        return BlockStateChange.fromMetadataPacked(CommonTagCompound.create(CompoundTagHandle.createHandle(nmsNBTTagCompoundMetadataHandle)));
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.BlockEntityType")
    public static BlockStateType toBlockStateType(Object nmsTileEntityTypesHandle) {
        return BlockStateType.fromTileEntityTypesHandle(nmsTileEntityTypesHandle);
    }

    @ConverterMethod
    public static BlockStateType tileEntityTypesIdToBlockStateType(int id) {
        return BlockStateType.bySerializedId(id);
    }

    @ConverterMethod(input="net.minecraft.resources.Identifier")
    public static BlockStateType tileEntityTypesKeyToBlockStateType(Object nmsMinecraftKeyHandle) {
        return BlockStateType.byKey(IdentifierHandle.createHandle(nmsMinecraftKeyHandle));
    }

    @ConverterMethod(input="net.minecraft.world.entity.Entity", output="T extends org.bukkit.entity.Entity")
    public static Entity toEntity(Object nmsEntityHandle) {
        return (Entity)EntityHandle.T.getBukkitEntity.invoker.invoke(nmsEntityHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.Level")
    public static World toWorld(Object nmsWorldHandle) {
        return (World)((Template.Method)LevelHandle.T.getWorld.raw).invoke(nmsWorldHandle);
    }

    @ConverterMethod
    public static World getWorld(Location location) {
        return location.getWorld();
    }

    @ConverterMethod
    public static World getWorld(Block block) {
        return block.getWorld();
    }

    @ConverterMethod
    public static World getWorld(BlockState blockState) {
        return blockState.getWorld();
    }

    @ConverterMethod
    public static World getWorld(Entity entity) {
        return entity.getWorld();
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.BlockEntity")
    public static World getWorldFromTileEntity(Object nmsTileEntityHandle) {
        return WrapperConversion.toWorld(((Template.Method)BlockEntityHandle.T.getWorld.raw).invoke(nmsTileEntityHandle));
    }

    @ConverterMethod(input="net.minecraft.world.level.chunk.LevelChunk")
    public static Chunk toChunk(Object nmsChunkHandle) {
        return (Chunk)LevelChunkHandle.T.getBukkitChunk.invoker.invoke(nmsChunkHandle);
    }

    @ConverterMethod
    public static Chunk getChunk(Block block) {
        return block.getChunk();
    }

    @ConverterMethod
    public static Chunk getChunk(BlockState blockState) {
        return blockState.getChunk();
    }

    @ConverterMethod
    public static Chunk getChunk(Location location) {
        return location.getChunk();
    }

    @ConverterMethod
    public static Block getBlock(Location location) {
        return location.getBlock();
    }

    @ConverterMethod
    public static Block getBlock(BlockState blockState) {
        return blockState.getBlock();
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.BlockEntity")
    public static Block getBlockFromTileEntity(Object nmsTileEntityHandle) {
        BlockEntityHandle handle = BlockEntityHandle.createHandle(nmsTileEntityHandle);
        BlockPosHandle pos = handle.getPosition();
        return handle.getWorld().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.BlockEntity")
    public static BlockState toBlockState(Object nmsTileEntityHandle) {
        return BlockStateConversion.INSTANCE.tileEntityToBlockState(nmsTileEntityHandle);
    }

    @ConverterMethod
    public static BlockState getBlockState(Block block) {
        return BlockStateConversion.INSTANCE.blockToBlockState(block);
    }

    @ConverterMethod(input="net.minecraft.network.syncher.SynchedEntityData")
    public static DataWatcher toDataWatcher(Object nmsDataWatcherHandle) {
        return DataWatcher.createForHandle(nmsDataWatcherHandle);
    }

    @Deprecated
    @ConverterMethod
    public static Material getMaterialFromId(Number materialId) {
        return CommonLegacyMaterials.getMaterialFromId(materialId.intValue());
    }

    @ConverterMethod
    public static Material getMaterialFromBlock(Block block) {
        return block.getType();
    }

    @ConverterMethod(input="net.minecraft.world.item.Item")
    public static Material toMaterialFromItemHandle(Object nmsItemHandle) {
        return CraftMagicNumbersHandle.getMaterialFromItem(nmsItemHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.Block")
    public static Material toMaterialFromBlockHandle(Object nmsBlockHandle) {
        return CraftMagicNumbersHandle.getMaterialFromBlock(nmsBlockHandle);
    }

    @ConverterMethod
    public static Material parseMaterial(String text) {
        return ParseUtil.parseMaterial(text, null);
    }

    @ConverterMethod(input="net.minecraft.world.item.ItemStack", acceptsNull=true)
    public static ItemStack toItemStack(Object nmsItemStackHandle) {
        if (nmsItemStackHandle == null || ((Template.Method)ItemStackHandle.T.getTypeField.raw).invoke(nmsItemStackHandle) == null) {
            return null;
        }
        return CraftItemStackHandle.asCraftMirror(nmsItemStackHandle);
    }

    @ConverterMethod
    public static ItemStack parseItemStack(String text) {
        return ItemParser.parse(text).getItemStack();
    }

    @ConverterMethod(input="net.minecraft.world.inventory.CraftingContainer")
    public static CraftingInventory toCraftingInventory(Object nmsInventoryCraftingHandle) {
        return CraftInventoryCraftingHandle.createNew(nmsInventoryCraftingHandle, null);
    }

    @ConverterMethod(input="net.minecraft.world.entity.player.Inventory")
    public static PlayerInventory toPlayerInventory(Object nmsPlayerInventoryHandle) {
        return CraftInventoryPlayerHandle.createNew(nmsPlayerInventoryHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity")
    public static FurnaceInventory toFurnaceInventory(Object nmsTileEntityFurnaceHandle) {
        return CraftInventoryFurnaceHandle.createNew(nmsTileEntityFurnaceHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.BrewingStandBlockEntity")
    public static BrewerInventory toBrewerInventory(Object nmsTileEntityBrewingStandHandle) {
        return CraftInventoryBrewerHandle.createNew(nmsTileEntityBrewingStandHandle);
    }

    @ConverterMethod(input="net.minecraft.world.inventory.MerchantContainer")
    public static MerchantInventory toMerchantInventory(Object nmsInventoryMerchantHandle) {
        return CraftInventoryMerchantHandle.createNew(nmsInventoryMerchantHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.entity.BeaconBlockEntity")
    public static BeaconInventory toBeaconInventory(Object nmsTileEntityBeaconHandle) {
        return CraftInventoryBeaconHandle.createNew(nmsTileEntityBeaconHandle);
    }

    @ConverterMethod(input="net.minecraft.world.Container")
    public static Inventory toInventory(Object nmsIInventoryHandle) {
        return CraftInventoryHandle.createNew(nmsIInventoryHandle);
    }

    @ConverterMethod(input="net.minecraft.world.inventory.AbstractContainerMenu")
    public static InventoryView toInventoryView(Object nmsContainerHandle) {
        return (InventoryView)AbstractContainerMenuHandle.T.getBukkitView.invoker.invoke(nmsContainerHandle);
    }

    @ConverterMethod
    public static Inventory toInventory(InventoryView inventoryView) {
        return inventoryView.getTopInventory();
    }

    @ConverterMethod(input="net.minecraft.world.Difficulty")
    public static Difficulty toDifficulty(Object nmsEnumDifficultyHandle) {
        Integer id = (Integer)DifficultyHandle.T.getId.invoker.invoke(nmsEnumDifficultyHandle);
        return Difficulty.getByValue((int)id);
    }

    @ConverterMethod
    public static Difficulty fromId(Number id) {
        return Difficulty.getByValue((int)id.intValue());
    }

    @ConverterMethod
    public static Difficulty parseDifficulty(String text) {
        return ParseUtil.parseEnum(Difficulty.class, text, null);
    }

    @ConverterMethod
    public static WorldType parseWorldType(String text) {
        return ParseUtil.parseEnum(WorldType.class, text, null);
    }

    @ConverterMethod(input="net.minecraft.world.level.GameType")
    public static GameMode toGameMode(Object nmsEnumGamemodeHandle) {
        return GameMode.getByValue((int)GameTypeHandle.createHandle(nmsEnumGamemodeHandle).getId());
    }

    @ConverterMethod
    public static GameMode getGameModeById(Number id) {
        return GameMode.getByValue((int)id.intValue());
    }

    @ConverterMethod
    public static GameMode parseGameMode(String text) {
        return ParseUtil.parseEnum(GameMode.class, text, null);
    }

    @ConverterMethod(input="net.minecraft.network.protocol.Packet")
    public static CommonPacket toCommonPacket(Object nmsPacketHandle) {
        return new CommonPacket(nmsPacketHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.ChunkPos")
    public static IntVector2 toIntVector2(Object nmsChunkCoordIntPairHandle) {
        return (IntVector2)ChunkPosHandle.T.toIntVector2.invoker.invoke(nmsChunkCoordIntPairHandle);
    }

    @ConverterMethod(input="net.minecraft.core.BlockPos")
    public static IntVector3 toIntVector3(Object nmsBlockPositionHandle) {
        return (IntVector3)Vec3iHandle.T.toIntVector3.invoker.invoke(nmsBlockPositionHandle);
    }

    @ConverterMethod
    public static IntVector3 toIntVector3FromVector(Vector vector) {
        return new IntVector3(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    @ConverterMethod(input="net.minecraft.world.phys.Vec3")
    public static Vector toVector(Object nmsVec3DHandle) {
        return (Vector)Vec3Handle.T.toBukkit.invoker.invoke(nmsVec3DHandle);
    }

    @ConverterMethod(input="net.minecraft.core.Rotations")
    public static Vector fromVector3fToVector(Object nmsVector3fHandle) {
        RotationsHandle handle = RotationsHandle.createHandle(nmsVector3fHandle);
        return new Vector(handle.getX(), handle.getY(), handle.getZ());
    }

    @ConverterMethod
    public static Vector toVectorFromLocation(Location location) {
        return new Vector(location.getX(), location.getY(), location.getZ());
    }

    @ConverterMethod
    public static Vector toVectorFromIntVector3(IntVector3 vector) {
        return new Vector(vector.x, vector.y, vector.z);
    }

    @ConverterMethod(input="net.minecraft.world.entity.player.Abilities")
    public static PlayerAbilities toPlayerAbilities(Object nmsPlayerAbilitiesHandle) {
        return new PlayerAbilities(nmsPlayerAbilitiesHandle);
    }

    @ConverterMethod(input="net.minecraft.server.level.EntityTracker")
    public static EntityTracker toEntityTracker(Object nmsEntityTrackerHandle) {
        return new EntityTracker(nmsEntityTrackerHandle);
    }

    @ConverterMethod(input="com.bergerkiller.bukkit.common.internal.LongHashSet")
    public static LongHashSet toLongHashSet(Object cbLongHashSetHandle) {
        return new LongHashSet(cbLongHashSetHandle);
    }

    @ConverterMethod(input="net.minecraft.util.IntHashMap<T>")
    public static <T> IntHashMap<T> toIntHashMap(Object nmsIntHashMapHandle) {
        return new IntHashMap(nmsIntHashMapHandle);
    }

    @ConverterMethod(input="net.minecraft.util.IntHashMap<?>")
    public static IntHashMap<Object> toRawIntHashMap(Object nmsIntHashMapHandle) {
        return new IntHashMap<Object>(nmsIntHashMapHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.state.BlockState")
    public static BlockData toBlockData(Object nmsIBlockDataHandle) {
        return BlockData.fromBlockData(nmsIBlockDataHandle);
    }

    @ConverterMethod(input="net.minecraft.world.level.block.Block")
    public static BlockData toBlockDataFromBlock(Object nmsBlockHandle) {
        return BlockData.fromBlock(nmsBlockHandle);
    }

    @ConverterMethod
    public static BlockData getBlockData(Material material) {
        return BlockData.fromMaterial(material);
    }

    @ConverterMethod
    public static BlockData getBlockData(MaterialData data) {
        return BlockData.fromMaterialData(data);
    }

    @ConverterMethod
    public static BlockData getBlockData(ItemStack item) {
        Material type = item.getType();
        if (!MaterialUtil.isBlock(type)) {
            return null;
        }
        return BlockData.fromMaterialData(type, item.getDurability());
    }

    @ConverterMethod
    public static BlockData parseBlockData(String text) {
        ItemParser parser = ItemParser.parse(text);
        if (!parser.hasType()) {
            return null;
        }
        if (parser.hasData()) {
            return BlockData.fromMaterialData(parser.getType(), parser.getData());
        }
        return BlockData.fromMaterial(parser.getType());
    }

    @ConverterMethod
    public static PotionEffectType toPotionEffectTypeFromId(int id) {
        return PotionEffectType.getById((int)id);
    }

    @ConverterMethod(input="net.minecraft.world.effect.MobEffect")
    public static PotionEffectType toPotionEffectType(Object nmsMobEffectListHandle) {
        int id = MobEffectHandle.T.getId.invoke(nmsMobEffectListHandle);
        PotionEffectType type = PotionEffectType.getById((int)id);
        if (type != null) {
            return type;
        }
        return null;
    }

    @ConverterMethod(input="net.minecraft.world.effect.MobEffectInstance")
    public static PotionEffect toPotionEffect(Object nmsMobEffectHandle) {
        return MobEffectInstanceHandle.T.toBukkit.invoke(nmsMobEffectHandle);
    }

    @ConverterMethod(input="net.minecraft.network.syncher.EntityDataAccessor")
    public static <T> DataWatcher.Key<T> toKey(Object nmsDataWatcherObjectHandle) {
        return new DataWatcher.Key(nmsDataWatcherObjectHandle);
    }

    @ConverterMethod(input="net.minecraft.network.syncher.SynchedEntityData.DataItem")
    public static <T> DataWatcher.Item<T> toDataWatcherItem(Object nmsDataWatcherItemHandle) {
        SynchedEntityDataHandle.DataItemHandle handle = SynchedEntityDataHandle.DataItemHandle.createHandle(nmsDataWatcherItemHandle);
        return new DataWatcher.Item(handle);
    }

    @ConverterMethod(input="net.minecraft.network.syncher.SynchedEntityData.DataValue")
    public static <T> DataWatcher.PackedItem<T> toDataWatcherPackedItem(Object nmsDataWatcherPackedItemHandle) {
        return DataWatcher.PackedItem.fromHandle(nmsDataWatcherPackedItemHandle);
    }

    @ConverterMethod(input="net.minecraft.resources.ResourceKey")
    public static <T> ResourceKey<T> toResourceKey(Object nmsResourceKeyHandle) {
        return ResourceKey.fromResourceKeyHandle(nmsResourceKeyHandle);
    }

    @ConverterMethod(input="net.minecraft.network.chat.Component")
    public static ChatText toChatText(Object iChatBaseComponentHandle) {
        return ChatText.fromComponent(iChatBaseComponentHandle);
    }

    @ConverterMethod
    public static ChatText fromMessageToChatText(String message) {
        return ChatText.fromMessage(message);
    }

    @ConverterMethod(input="net.minecraft.world.entity.EquipmentSlot")
    public static int enumItemSlotToFilterFlag(Object nmsEnumItemSlot) {
        return (Integer)EquipmentSlotHandle.T.getFilterFlag.invoker.invoke(nmsEnumItemSlot);
    }

    @ConverterMethod(output="net.minecraft.world.entity.EquipmentSlot")
    public static Object enumItemSlotFromFilterFlag(int legacyEquipmentIndex) {
        return EquipmentSlotHandle.fromFilterFlagRaw(legacyEquipmentIndex);
    }

    @ConverterMethod(input="net.minecraft.core.NonNullList<E>", optional=true)
    public static <E> List<E> toList(Object nonNullListHandle) {
        return (List)nonNullListHandle;
    }

    @ConverterMethod
    public static CraftInputSlot toCraftInputSlot(ItemStack defaultChoice) {
        defaultChoice = defaultChoice.clone();
        defaultChoice.setAmount(1);
        return new CraftInputSlot(new ItemStack[]{defaultChoice});
    }

    @ConverterMethod(input="net.minecraft.world.item.crafting.Ingredient", optional=true)
    public static CraftInputSlot toCraftInputSlot(Object recipeItemStackHandle) {
        List<ItemStack> choices = IngredientHandle.T.getChoices.invoke(recipeItemStackHandle);
        if (choices == null) {
            throw new RuntimeException("Choices result field is null");
        }
        return new CraftInputSlot(choices);
    }

    @ConverterMethod(input="net.minecraft.sounds.SoundEvent")
    public static ResourceKey<SoundEffect> soundEffectToResourceKey(Object nmsSoundEffectHandle) {
        return ResourceCategory.sound_effect.createKey(SoundEventHandle.T.name.get(nmsSoundEffectHandle));
    }

    @ConverterMethod(output="net.minecraft.sounds.SoundEvent")
    public static Object soundEffectFromResourceKey(ResourceKey<SoundEffect> soundKey) {
        Object mcKey = soundKey.getName().getRaw();
        Object effect = ((Template.StaticMethod)SoundEventHandle.T.byKey.raw).invoke(mcKey);
        if (effect == null) {
            effect = ((Template.StaticMethod)SoundEventHandle.T.createVariableRangeEvent.raw).invoke(mcKey);
        }
        return effect;
    }

    @ConverterMethod
    public static ResourceKey<SoundEffect> soundNameToResourceKey(String name) {
        return SoundEffect.fromName(name);
    }

    @ConverterMethod
    public static String soundNameFromResourceKey(ResourceKey<SoundEffect> key) {
        return key.getPath();
    }

    @ConverterMethod(output="net.minecraft.resources.Identifier")
    public static Object resourceKeyToMinecraftKey(ResourceKey<?> key) {
        return key.getName().getRaw();
    }

    @ConverterMethod(input="net.minecraft.resources.Identifier")
    public static ResourceKey<SoundEffect> minecraftKeyToSoundEffectResourceKey(Object minecraftKeyHandle) {
        return ResourceCategory.sound_effect.createKey(IdentifierHandle.createHandle(minecraftKeyHandle));
    }

    @ConverterMethod(input="net.minecraft.world.level.BaseSpawner")
    public static MobSpawner toMobSpawner(Object nmsMobSpawnerAbstractHandle) {
        return new MobSpawner(nmsMobSpawnerAbstractHandle);
    }

    @ConverterMethod(input="net.minecraft.world.inventory.ContainerInput", optional=true)
    public static InventoryClickType inventoryClickTypeFromHandle(Object nmsInventoryClickType) {
        return InventoryClickType.byId(((Enum)nmsInventoryClickType).ordinal());
    }

    @ConverterMethod
    public static InventoryClickType inventoryClickTypeFromId(int id) {
        return InventoryClickType.byId(id);
    }

    @ConverterMethod(input="net.minecraft.core.Direction")
    public static BlockFace blockFaceFromEnumDirection(Object nmsEnumDirectionHandle) {
        return enumDirectionValues[((Enum)nmsEnumDirectionHandle).ordinal()];
    }

    @ConverterMethod(output="net.minecraft.core.Direction")
    public static Object blockFaceToEnumDirection(BlockFace direction) {
        Class<?> enumClass = CommonUtil.getClass("net.minecraft.core.Direction");
        if (enumClass != null) {
            ?[] values = enumClass.getEnumConstants();
            for (int i = 0; i < enumDirectionValues.length; ++i) {
                if (enumDirectionValues[i] != direction) continue;
                return values[i];
            }
            return values[2];
        }
        return null;
    }

    @ConverterMethod(input="net.minecraft.world.level.levelgen.Heightmap")
    public static HeightMap heightMapFromHandle(Object handle) {
        return new HeightMap(HeightmapHandle.createHandle(handle));
    }

    @ConverterMethod(input="net.minecraft.world.entity.EntityType", optional=true)
    public static Class<?> entityClassFromEntityTypes(Object nmsEntityTypesHandle) {
        return EntityTypeHandle.T.getEntityClassInst.invoke(nmsEntityTypesHandle);
    }

    @ConverterMethod(input="org.bukkit.block.data.BlockData", optional=true)
    public static BlockData blockDataFromBukkit(Object bukkitBlockData) {
        return CraftBlockDataHandle.T.getState.invoke(bukkitBlockData);
    }

    @ConverterMethod(input="org.bukkit.block.data.BlockData", output="net.minecraft.world.level.block.state.BlockState", optional=true)
    public static Object iblockdataHandleFromBukkit(Object bukkitBlockData) {
        return ((Template.Method)CraftBlockDataHandle.T.getState.raw).invoke(bukkitBlockData);
    }

    @ConverterMethod(input="net.minecraft.ChatFormatting")
    public static ChatColor chatColorFromEnumChatFormatHandle(Object nmsEnumChatFormat) {
        String s = nmsEnumChatFormat.toString();
        if (s.length() >= 2) {
            return ChatColor.getByChar((char)s.charAt(1));
        }
        return ChatColor.RESET;
    }

    @ConverterMethod
    public static ChatColor chatColorFromEnumChatFormatIndex(int nmsEnumChatFormatIndex) {
        return WrapperConversion.chatColorFromEnumChatFormatHandle(ChatFormattingHandle.byId(nmsEnumChatFormatIndex).getRaw());
    }

    @ConverterMethod
    public static Art artFromInternalName(String internalName) {
        return CraftArtHandle.NotchToBukkit(CraftArtHandle.NotchFromInternalName(internalName));
    }

    @ConverterMethod
    public static Art artFromInternalId(int internalId) {
        return CraftArtHandle.NotchToBukkit(CraftArtHandle.NotchFromInternalId(internalId));
    }

    @ConverterMethod(input="net.minecraft.world.InteractionResult", optional=true)
    public static InteractionResult interactionResultFromNMSEnumInteractionResult(Object nmsEnumInteractionResultHandle) {
        return InteractionResult.fromHandle(nmsEnumInteractionResultHandle);
    }

    @ConverterMethod(input="net.minecraft.core.particles.ParticleType<?>", acceptsNull=true)
    public static ParticleType<?> toParticleType(Object nmsParticleHandle) {
        return ParticleType.byNMSParticleHandle(nmsParticleHandle);
    }
}

