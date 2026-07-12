/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.entity.Entity
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.material.MaterialData
 */
package com.bergerkiller.bukkit.common.wrappers;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.collections.BlockFaceSet;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.internal.CommonListener;
import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.internal.legacy.IBlockDataToMaterialData;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialDataToIBlockData;
import com.bergerkiller.bukkit.common.internal.legacy.MaterialsByName;
import com.bergerkiller.bukkit.common.internal.logic.BlockDataSerializer;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockDataRegistry;
import com.bergerkiller.bukkit.common.wrappers.BlockProperty;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import com.bergerkiller.generated.net.minecraft.core.BlockPosHandle;
import com.bergerkiller.generated.net.minecraft.core.IdMapperHandle;
import com.bergerkiller.generated.net.minecraft.core.MappedRegistryHandle;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.util.CrudeIncrementalIntIdentityHashBiMapHandle;
import com.bergerkiller.generated.net.minecraft.world.item.ItemStackHandle;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.BlockStateHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.properties.PropertyHandle;
import com.bergerkiller.generated.net.minecraft.world.phys.AABBHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

class BlockDataImpl
extends BlockData {
    private BlockHandle block;
    private BlockStateHandle data;
    private MaterialData materialData;
    private Material type;
    private Material legacyType;
    private boolean hasRenderOptions;
    private int combinedId;
    private BlockFaceSet cachedOpaqueFaces;
    private int cachedOpacity;
    public static final int ID_BITS = 8;
    public static final int DATA_BITS = 4;
    public static final int REGISTRY_SIZE;
    public static final int REGISTRY_MASK;
    public static final BlockDataConstant[] BY_ID_AND_DATA;
    public static final Material LEGACY_AIR_TYPE;
    public static final BlockDataConstant AIR;
    public static final EnumMap<Material, BlockDataConstant> BY_MATERIAL;
    public static final Map<Object, BlockDataConstant> BY_BLOCK;
    public static final Cache BY_BLOCK_DATA;
    public static final int BY_LEGACY_MAT_DATA_SHIFT;
    public static final BlockDataConstant[] BY_LEGACY_MAT_DATA;
    private static final EnumSet<Material> BLOCK_UPDATE_STATE_BLACKLIST;

    private static BlockDataConstant findConstant(BlockStateHandle iblockdata) {
        BlockDataConstant dataBlockConst = BY_BLOCK_DATA.get(iblockdata.getRaw());
        if (dataBlockConst == null) {
            return BY_BLOCK_DATA.create(iblockdata);
        }
        return dataBlockConst;
    }

    public BlockDataImpl() {
        this(AIR.getBlock());
    }

    public BlockDataImpl(BlockStateHandle data) {
        this(data.getBlock(), data);
    }

    public BlockDataImpl(BlockHandle block) {
        this(block, block.getBlockData());
    }

    public BlockDataImpl(BlockHandle block, BlockStateHandle data) {
        this.block = block;
        this.data = data;
        this.refreshBlock();
    }

    @Override
    public void loadBlock(Object block) {
        this.block = BlockHandle.createHandle(block);
        this.data = this.block.getBlockData();
        this.refreshBlock();
    }

    @Override
    public void loadBlockData(Object iBlockData) {
        this.data = BlockStateHandle.createHandle(iBlockData);
        this.block = this.data.getBlock();
        this.refreshBlock();
    }

    @Override
    public void loadMaterialData(MaterialData materialdata) {
        this.data = MaterialDataToIBlockData.getIBlockData(materialdata);
        this.block = this.data.getBlock();
        this.refreshBlock();
    }

    private final void refreshBlock() {
        this.hasRenderOptions = true;
        this.type = LogicUtil.fixNull(CraftMagicNumbersHandle.getMaterialFromBlock(this.block.getRaw()), Material.AIR);
        this.materialData = IBlockDataToMaterialData.getMaterialData(this.data);
        this.legacyType = CommonLegacyMaterials.toLegacy(this.materialData.getItemType());
        this.combinedId = BlockHandle.getCombinedId(this.data);
        this.cachedOpaqueFaces = this.data.getCachedOpaqueFaces();
        this.cachedOpacity = this.data.getCachedOpacity();
    }

    @Override
    public final BlockHandle getBlock() {
        return this.block;
    }

    @Override
    public final Object getData() {
        return this.data.getRaw();
    }

    @Override
    public final int getRawData() {
        return this.materialData.getData();
    }

    @Override
    public final Material getType() {
        return this.type;
    }

    @Override
    public final Material getLegacyType() {
        return this.legacyType;
    }

    @Override
    public final boolean hasLegacyType() {
        return this.legacyType != LEGACY_AIR_TYPE || this == AIR;
    }

    @Override
    public final MaterialData getMaterialData() {
        return this.materialData;
    }

    @Override
    public final int getCombinedId() {
        return this.combinedId;
    }

    @Override
    public final int getCombinedId_1_8_8() {
        if (IdMapperHandle.T.isAssignableFrom(BlockHandle.REGISTRY_ID)) {
            return IdMapperHandle.T.getId.invoke(BlockHandle.REGISTRY_ID, this.data.getRaw());
        }
        return CrudeIncrementalIntIdentityHashBiMapHandle.T.getId.invoke(BlockHandle.REGISTRY_ID, this.data.getRaw());
    }

    @Override
    public String getBlockName() {
        Object minecraftKey = MappedRegistryHandle.T.getKey.invoke(BlockHandle.getRegistry(), this.getBlockRaw());
        return IdentifierHandle.T.getName.invoke(minecraftKey);
    }

    @Override
    public String serializeToString() {
        return BlockDataSerializer.INSTANCE.serialize(this);
    }

    @Override
    public BlockRenderOptions getRenderOptions(World world, int x, int y, int z) {
        BlockRenderOptions options;
        if (!this.hasRenderOptions) {
            return new BlockRenderOptions((BlockData)this, "");
        }
        CommonListener.BLOCK_PHYSICS_FIRED = false;
        Object stateData = world == null || BLOCK_UPDATE_STATE_BLACKLIST.contains(this.getType()) ? this.data.getRaw() : ((Template.Method)BlockHandle.T.updateState.raw).invoke(this.block.getRaw(), this.data.getRaw(), HandleConversion.toWorldHandle(world), ((Template.Constructor)BlockPosHandle.T.constr_x_y_z.raw).newInstance(x, y, z));
        if (stateData == null) {
            options = new BlockRenderOptions((BlockData)this, new HashMap<String, String>(0));
        } else {
            Collection<PropertyHandle> properties = BlockStateHandle.T.getProperties.invoke(stateData);
            HashMap<String, String> statesStr = new HashMap<String, String>(properties.size());
            for (PropertyHandle property : properties) {
                String key = property.getKeyToken();
                String value = property.getValueToken((Comparable)BlockStateHandle.T.get.invoke(stateData, property));
                statesStr.put(key, value);
            }
            options = new BlockRenderOptions((BlockData)this, statesStr);
        }
        BlockRenderProvider renderProvider = BlockRenderProvider.get(this);
        if (renderProvider != null) {
            renderProvider.addOptions(options, world, x, y, z);
        }
        if (options.isEmpty()) {
            this.hasRenderOptions = false;
        }
        if (CommonListener.BLOCK_PHYSICS_FIRED) {
            CommonListener.BLOCK_PHYSICS_FIRED = false;
            BLOCK_UPDATE_STATE_BLACKLIST.add(this.getType());
            Logging.LOGGER.warning("[BlockData] Block physics are occurring when reading state of " + CommonLegacyMaterials.getMaterialName(this.getType()) + " data=" + this.toString() + " options=" + options);
        }
        return options;
    }

    @Override
    public final ResourceKey<SoundEffect> getStepSound() {
        return ResourceCategory.sound_effect.createKey(this.data.getSoundType().getStepSound().getName());
    }

    @Override
    public final ResourceKey<SoundEffect> getPlaceSound() {
        return ResourceCategory.sound_effect.createKey(this.data.getSoundType().getPlaceSound().getName());
    }

    @Override
    public final ResourceKey<SoundEffect> getBreakSound() {
        return ResourceCategory.sound_effect.createKey(this.data.getSoundType().getBreakSound().getName());
    }

    @Override
    public final int getOpacity(World world, int x, int y, int z) {
        if (this.cachedOpacity == -1) {
            try {
                return this.block.getOpacity(this.data, world, x, y, z);
            }
            catch (RuntimeException ex) {
                if (world == null) {
                    throw new IllegalArgumentException("For BlockData " + this + " World Access is required to read opacity");
                }
                throw ex;
            }
        }
        return this.cachedOpacity;
    }

    @Override
    public final int getOpacity(Block block) {
        if (this.cachedOpacity == -1) {
            try {
                return this.block.getOpacity(this.data, block.getWorld(), block.getX(), block.getY(), block.getZ());
            }
            catch (NullPointerException ex) {
                if (block == null) {
                    throw new IllegalArgumentException("For BlockData " + this + " World Access is required to read opacity");
                }
                throw ex;
            }
        }
        return this.cachedOpacity;
    }

    @Override
    public final BlockFaceSet getOpaqueFaces(World world, int x, int y, int z) {
        if (this.cachedOpaqueFaces != null) {
            return this.cachedOpaqueFaces;
        }
        if (world == null) {
            throw new IllegalArgumentException("For BlockData " + this + " World Access is required to read opaque faces");
        }
        int mask = 0;
        if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.NORTH)) {
            mask |= 1;
        }
        if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.EAST)) {
            mask |= 2;
        }
        if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.SOUTH)) {
            mask |= 4;
        }
        if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.WEST)) {
            mask |= 8;
        }
        if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.UP)) {
            mask |= 0x10;
        }
        if (this.block.isFaceOpaque(this.data, world, x, y, z, BlockFace.DOWN)) {
            mask |= 0x20;
        }
        return BlockFaceSet.byMask(mask);
    }

    @Override
    public final BlockFaceSet getOpaqueFaces(Block block) {
        if (this.cachedOpaqueFaces != null) {
            return this.cachedOpaqueFaces;
        }
        if (block == null) {
            throw new IllegalArgumentException("For BlockData " + this + " World Access is required to read opaque faces");
        }
        return this.getOpaqueFaces(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    @Override
    @Deprecated
    public final int getEmission() {
        return this.block.getEmission(this.data, null, 0, 0, 0);
    }

    @Override
    public int getEmission(World world, int x, int y, int z) {
        return this.block.getEmission(this.data, world, x, y, z);
    }

    @Override
    public int getEmission(Block block) {
        return this.block.getEmission(this.data, block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    @Override
    public final boolean isOccluding(Block block) {
        return this.block.isOccluding(this.data, block);
    }

    @Override
    public boolean isOccluding(World world, int x, int y, int z) {
        return this.block.isOccluding_at(this.data, world, x, y, z);
    }

    @Override
    public final boolean isSuffocating(Block block) {
        return this.block.isOccluding(this.data, block);
    }

    @Override
    public final boolean isPowerSource() {
        return this.data.isPowerSource();
    }

    @Override
    public boolean isSolid() {
        return this.data.isSolid();
    }

    @Override
    public final AABBHandle getBoundingBox(Block block) {
        return this.data.getBoundingBox(LevelHandle.fromBukkit(block.getWorld()), new IntVector3(block));
    }

    @Override
    public AABBHandle getInteractableBox(Block block) {
        return this.data.getInteractableBox(LevelHandle.fromBukkit(block.getWorld()), new IntVector3(block));
    }

    @Override
    public final float getDamageResilience() {
        return this.block.getDamageResillience();
    }

    @Override
    public final boolean canSupportTop(Block block) {
        return this.block.canSupportOnFace(this.data, block, BlockFace.UP);
    }

    @Override
    public final boolean canSupportOnFace(Block block, BlockFace face) {
        return this.block.canSupportOnFace(this.data, block, face);
    }

    @Override
    public final void dropNaturally(World world, int x, int y, int z, float yield, int chance) {
        this.block.dropNaturally(this.data, world, new IntVector3(x, y, z), yield, chance);
    }

    @Override
    public void destroy(World world, int x, int y, int z, float yield) {
        this.dropNaturally(world, x, y, z, yield);
        WorldUtil.setBlockData(world, x, y, z, AIR);
    }

    @Override
    public void stepOn(World world, IntVector3 blockPosition, Entity entity) {
        this.block.stepOn(world, blockPosition, this.data, entity);
    }

    @Override
    public BlockData setProperty(String key, Object value) {
        BlockStateHandle updated_data = this.data.set(key, value);
        return BlockDataRegistry.fromBlockData(updated_data.getRaw());
    }

    @Override
    public BlockData setProperty(BlockProperty<?> stateKey, Object value) {
        BlockStateHandle updated_data = this.data.set((PropertyHandle)stateKey.getBackingHandle(), value);
        return BlockDataRegistry.fromBlockData(updated_data.getRaw());
    }

    @Override
    public <T> T getProperty(String key, Class<T> type) {
        return this.data.get(key, type);
    }

    @Override
    public <T extends Comparable<?>> T getProperty(BlockProperty<T> stateKey) {
        return (T)((Comparable)LogicUtil.unsafeCast(this.data.get((PropertyHandle)stateKey.getBackingHandle())));
    }

    @Override
    public Collection<BlockProperty<?>> getProperties() {
        return List.of();
    }

    @Override
    public BlockProperty<?> getPropertyKey(String key) {
        PropertyHandle handle = this.data.findProperty(key);
        return handle == null ? null : new BlockProperty(handle);
    }

    @Override
    public ItemStack createItem(int amount) {
        return ItemStackHandle.fromBlockData(this.data, amount).toBukkit();
    }

    static {
        BlockDataConstant blockConst;
        BY_MATERIAL = new EnumMap(Material.class);
        BY_BLOCK = new IdentityHashMap<Object, BlockDataConstant>();
        BLOCK_UPDATE_STATE_BLACKLIST = EnumSet.noneOf(Material.class);
        CommonBootstrap.initServer();
        Iterable<?> REGISTRY = BlockHandle.getRegistry();
        LEGACY_AIR_TYPE = MaterialsByName.getMaterial("LEGACY_AIR");
        AIR = new BlockDataConstant(BlockHandle.createHandle(CraftMagicNumbersHandle.getBlockFromMaterial(Material.AIR)));
        for (Object rawBlock : REGISTRY) {
            BlockHandle block = BlockHandle.createHandle(rawBlock);
            Material material = CraftMagicNumbersHandle.getMaterialFromBlock(rawBlock);
            blockConst = material == Material.AIR ? AIR : new BlockDataConstant(block);
            BY_BLOCK.put(rawBlock, blockConst);
            if (material == null) continue;
            BY_MATERIAL.put(material, blockConst);
        }
        BY_BLOCK_DATA = new Cache(AIR);
        Object[] tmp = new BlockDataConstant[16384];
        Arrays.fill(tmp, AIR);
        for (Object rawIBlockData : BlockHandle.REGISTRY_ID) {
            BlockStateHandle blockData = BlockStateHandle.createHandle(rawIBlockData);
            BlockDataConstant block_const = BY_BLOCK.get(blockData.getBlock().getRaw());
            if (block_const.getData() != rawIBlockData) {
                block_const = new BlockDataConstant(blockData);
            }
            BY_BLOCK_DATA.cacheWritable.put(rawIBlockData, block_const);
            int combined_id = block_const.getCombinedId_1_8_8();
            while (combined_id >= tmp.length) {
                int oldLength = tmp.length;
                tmp = (BlockDataConstant[])Arrays.copyOf(tmp, oldLength << 1);
                Arrays.fill(tmp, oldLength, tmp.length, AIR);
            }
            tmp[combined_id] = block_const;
        }
        BY_ID_AND_DATA = tmp;
        REGISTRY_SIZE = tmp.length;
        REGISTRY_MASK = REGISTRY_SIZE - 1;
        BlockDataImpl.BY_BLOCK_DATA.updateVisible();
        int shift = 1;
        while (CommonLegacyMaterials.getAllMaterials().length >= 1 << shift) {
            ++shift;
        }
        BY_LEGACY_MAT_DATA_SHIFT = shift;
        BY_LEGACY_MAT_DATA = new BlockDataConstant[16 << BY_LEGACY_MAT_DATA_SHIFT];
        Arrays.fill(BY_LEGACY_MAT_DATA, AIR);
        for (Material mat : CommonLegacyMaterials.getAllMaterials()) {
            MaterialData materialdata;
            if (!MaterialsByName.isBlock(mat)) {
                BY_MATERIAL.put(mat, AIR);
                continue;
            }
            blockConst = BY_MATERIAL.get(mat);
            if (blockConst == null) {
                if (CommonCapabilities.MATERIAL_ENUM_CHANGES && CommonLegacyMaterials.isLegacy(mat)) {
                    MaterialData materialData = IBlockDataToMaterialData.createMaterialData(mat);
                    blockConst = BlockDataImpl.findConstant(MaterialDataToIBlockData.getIBlockData(materialData));
                } else {
                    Object rawBlock = CraftMagicNumbersHandle.getBlockFromMaterial(mat);
                    blockConst = BY_BLOCK.get(rawBlock);
                    if (blockConst == null) {
                        blockConst = new BlockDataConstant(BlockHandle.createHandle(rawBlock));
                        BY_BLOCK.put(rawBlock, blockConst);
                    }
                }
                BY_MATERIAL.put(mat, blockConst);
            }
            if ((materialdata = new MaterialData(mat)).getItemType() == null) {
                int index = CommonLegacyMaterials.getOrdinal(mat);
                for (int data = 0; data < 16; ++data) {
                    BlockDataImpl.BY_LEGACY_MAT_DATA[index | data << BlockDataImpl.BY_LEGACY_MAT_DATA_SHIFT] = blockConst;
                }
                continue;
            }
            for (int data = 0; data < 16; ++data) {
                materialdata.setData((byte)data);
                BlockStateHandle blockData = MaterialDataToIBlockData.getIBlockData(materialdata);
                BlockDataConstant dataBlockConst = blockConst;
                if (blockData == null) {
                    Logging.LOGGER_REGISTRY.warning("Obtaining BlockData of MaterialData " + materialdata + " yielded null result!");
                } else if (blockData.getRaw() != blockConst.getData()) {
                    dataBlockConst = BlockDataImpl.findConstant(blockData);
                }
                int index = CommonLegacyMaterials.getOrdinal(mat);
                BlockDataImpl.BY_LEGACY_MAT_DATA[index |= data << BlockDataImpl.BY_LEGACY_MAT_DATA_SHIFT] = dataBlockConst;
            }
        }
        try {
            String[] blocked_types;
            for (String nmsBlockTypeName : blocked_types = new String[]{"net.minecraft.world.level.block.BlockPlant", "net.minecraft.world.level.block.BlockObserver", "net.minecraft.world.level.block.BlockBubbleColumn", "net.minecraft.world.level.block.BlockConcretePowder", "net.minecraft.world.level.block.BlockLeaves", "net.minecraft.world.level.block.BlockDirtSnow"}) {
                Class<?> nmsBlockType = CommonUtil.getClass(nmsBlockTypeName);
                if (nmsBlockType == null) continue;
                for (Material mat : CommonLegacyMaterials.getAllMaterials()) {
                    Object raw_block;
                    BlockDataConstant blockData = BY_MATERIAL.get(mat);
                    if (blockData == null || (raw_block = blockData.getBlockRaw()) == null || !nmsBlockType.isAssignableFrom(raw_block.getClass())) continue;
                    BLOCK_UPDATE_STATE_BLACKLIST.add(mat);
                }
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error initializing BlockData updateState() blacklist", t);
        }
    }

    public static final class Cache {
        private final IdentityHashMap<Object, BlockDataConstant> cacheWritable = new IdentityHashMap();
        private IdentityHashMap<Object, BlockDataConstant> cache;
        private BlockDataConstant last;

        private Cache(BlockDataConstant air) {
            this.cacheWritable.put(air.getBlockRaw(), air);
            this.last = air;
            this.updateVisible();
        }

        public Collection<BlockData> getAll() {
            return Collections.unmodifiableCollection(this.cache.values());
        }

        public BlockDataConstant get(Object rawIBlockData) {
            if (this.last.getBlockRaw() == rawIBlockData) {
                return this.last;
            }
            return this.cache.get(rawIBlockData);
        }

        public BlockDataConstant getOrCreate(Object rawIBlockData) {
            BlockDataConstant last = this.last;
            if (last.getBlockRaw() == rawIBlockData) {
                return last;
            }
            this.last = LogicUtil.synchronizeCopyOnWrite(this, l -> this.cache, rawIBlockData, IdentityHashMap::get, (map, key) -> this.createUnsynchronized(BlockStateHandle.createHandle(key)));
            return this.last;
        }

        public synchronized BlockDataConstant create(BlockStateHandle iblockdataHandle) {
            return this.createUnsynchronized(iblockdataHandle);
        }

        private BlockDataConstant createUnsynchronized(BlockStateHandle iblockdataHandle) {
            BlockDataConstant c = new BlockDataConstant(iblockdataHandle);
            this.cacheWritable.put(iblockdataHandle.getRaw(), c);
            this.updateVisible();
            return c;
        }

        private void updateVisible() {
            this.cache = (IdentityHashMap)this.cacheWritable.clone();
        }
    }

    public static class BlockDataConstant
    extends BlockDataImpl {
        public BlockDataConstant(BlockHandle block) {
            super(block);
        }

        public BlockDataConstant(BlockStateHandle blockData) {
            super(blockData);
        }

        @Override
        public void loadBlock(Object block) {
            throw new UnsupportedOperationException("Immutable Block Data objects can not be changed");
        }

        @Override
        public void loadBlockData(Object iBlockData) {
            throw new UnsupportedOperationException("Immutable Block Data objects can not be changed");
        }

        @Override
        public void loadMaterialData(MaterialData materialdata) {
            throw new UnsupportedOperationException("Immutable Block Data objects can not be changed");
        }
    }
}

