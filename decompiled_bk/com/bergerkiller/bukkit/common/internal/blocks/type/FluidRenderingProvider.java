/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.BlockFace
 */
package com.bergerkiller.bukkit.common.internal.blocks.type;

import com.bergerkiller.bukkit.common.internal.blocks.BlockRenderProvider;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

public class FluidRenderingProvider
extends BlockRenderProvider {
    private final List<Material> fluidMaterials;
    private final String fluidTexture1;
    private final String fluidTexture2;
    private final String tint;

    public FluidRenderingProvider(String texture1, String texture2, String tint, Collection<Material> fluidMaterials) {
        this.fluidTexture1 = texture1;
        this.fluidTexture2 = texture2;
        this.fluidMaterials = new ArrayList<Material>(fluidMaterials);
        this.tint = tint;
    }

    @Override
    public void addOptions(BlockRenderOptions options, World world, int x, int y, int z) {
        if (world == null) {
            return;
        }
        this.storeWaterBlock(options, "neigh_nn", world, x, y, z, BlockFace.NORTH);
        this.storeWaterBlock(options, "neigh_ne", world, x, y, z, BlockFace.NORTH_EAST);
        this.storeWaterBlock(options, "neigh_ee", world, x, y, z, BlockFace.EAST);
        this.storeWaterBlock(options, "neigh_se", world, x, y, z, BlockFace.SOUTH_EAST);
        this.storeWaterBlock(options, "neigh_ss", world, x, y, z, BlockFace.SOUTH);
        this.storeWaterBlock(options, "neigh_sw", world, x, y, z, BlockFace.SOUTH_WEST);
        this.storeWaterBlock(options, "neigh_ww", world, x, y, z, BlockFace.WEST);
        this.storeWaterBlock(options, "neigh_nw", world, x, y, z, BlockFace.NORTH_WEST);
        if (this.tint != null) {
            options.put("tint", this.tint);
        }
    }

    @Override
    public Collection<Material> getTypes() {
        return this.fluidMaterials;
    }

    @Override
    public Model createModel(MapResourcePack resources, BlockRenderOptions options) {
        FluidBlock self = this.getFluidBlock(options.getBlockData());
        FluidBlock neigh_nn = FluidRenderingProvider.readFluidBlock(options, "neigh_nn");
        FluidBlock neigh_ne = FluidRenderingProvider.readFluidBlock(options, "neigh_ne");
        FluidBlock neigh_ee = FluidRenderingProvider.readFluidBlock(options, "neigh_ee");
        FluidBlock neigh_se = FluidRenderingProvider.readFluidBlock(options, "neigh_se");
        FluidBlock neigh_ss = FluidRenderingProvider.readFluidBlock(options, "neigh_ss");
        FluidBlock neigh_sw = FluidRenderingProvider.readFluidBlock(options, "neigh_sw");
        FluidBlock neigh_ww = FluidRenderingProvider.readFluidBlock(options, "neigh_ww");
        FluidBlock neigh_nw = FluidRenderingProvider.readFluidBlock(options, "neigh_nw");
        Model model = new Model();
        Model.Element water = new Model.Element();
        MapTexture waterSide = resources.getTexture(this.fluidTexture1);
        MapTexture waterTexture = resources.getTexture(this.fluidTexture2);
        for (BlockFace blockFace : FaceUtil.BLOCK_SIDES) {
            Model.Element.Face face = new Model.Element.Face();
            MapTexture mapTexture = face.texture = FaceUtil.isVertical(blockFace) ? waterTexture : waterSide;
            if (this.tint != null) {
                face.tintindex = 0;
            }
            face.buildBlock(options);
            water.faces.put(blockFace, face);
        }
        water.buildQuads();
        if (!this.isFlowingDown(options.getBlockData())) {
            Model.Element.Face topFace = water.faces.get(BlockFace.UP);
            topFace.quad.p0.y = this.calcLevel(self, neigh_ww, neigh_nw, neigh_nn);
            topFace.quad.p1.y = this.calcLevel(self, neigh_ss, neigh_sw, neigh_ww);
            topFace.quad.p2.y = this.calcLevel(self, neigh_ee, neigh_se, neigh_ss);
            topFace.quad.p3.y = this.calcLevel(self, neigh_nn, neigh_ne, neigh_ee);
            if (this.tint != null) {
                topFace.tintindex = 0;
            }
            topFace.buildBlock(options);
        }
        model.elements.add(water);
        return model;
    }

    private final float calcLevel(FluidBlock ... blocks) {
        float weight = 0.0f;
        float level = 0.0f;
        for (FluidBlock block : blocks) {
            level += block.level;
            weight += block.weight;
        }
        return level / weight;
    }

    private void storeWaterBlock(BlockRenderOptions options, String name, World world, int x, int y, int z, BlockFace face) {
        options.put(name, this.getFluidBlock(WorldUtil.getBlockData(world, x + face.getModX(), y, z + face.getModZ())).name());
    }

    private static FluidBlock readFluidBlock(BlockRenderOptions options, String name) {
        String valueStr = options.get(name);
        if (valueStr != null) {
            for (FluidBlock block : FluidBlock.values()) {
                if (!valueStr.equals(block.name())) continue;
                return block;
            }
        }
        return FluidBlock.AIR;
    }

    private FluidBlock getFluidBlock(BlockData blockData) {
        Material mat = blockData.getType();
        for (Material f : this.fluidMaterials) {
            if (mat != f) continue;
            return FluidBlock.L_VALUES[blockData.getRawData() & 7];
        }
        if (mat.isSolid()) {
            return FluidBlock.SOLID;
        }
        return FluidBlock.AIR;
    }

    private boolean isFlowingDown(BlockData blockData) {
        return (blockData.getRawData() & 8) == 8;
    }

    private static enum FluidBlock {
        L0(14.0f),
        L1(11.5f),
        L2(10.8f),
        L3(9.0f),
        L4(7.0f),
        L5(5.7f),
        L6(3.8f),
        L7(2.0f),
        SOLID(0.0f, 0.0f),
        AIR(0.0f, 0.5f);

        public static final FluidBlock[] L_VALUES;
        public final float level;
        public final float weight;

        private FluidBlock(float level) {
            this(level, 1.0f);
        }

        private FluidBlock(float level, float weight) {
            this.level = level;
            this.weight = weight;
        }

        static {
            L_VALUES = new FluidBlock[]{L0, L1, L2, L3, L4, L5, L6, L7};
        }
    }
}

