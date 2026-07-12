/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.blocks;

import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.blocks.type.FluidRenderingProvider;
import com.bergerkiller.bukkit.common.internal.blocks.type.GrassRenderingProvider;
import com.bergerkiller.bukkit.common.internal.blocks.type.RedstoneWireRenderingProvider;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.BlockRenderOptions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.World;

public abstract class BlockRenderProvider {
    private static final Map<Material, BlockRenderProvider> providers = new EnumMap<Material, BlockRenderProvider>(Material.class);
    private final HashMap<String, String> resources = new HashMap();

    public static void register(BlockRenderProvider provider) {
        for (Material type : provider.getTypes()) {
            providers.put(type, provider);
        }
    }

    public static BlockRenderProvider get(BlockData blockData) {
        return providers.get(blockData.getType());
    }

    protected void linkResource(MapResourcePack.ResourceType type, String path, String resource) {
        if (type == null) {
            throw new IllegalArgumentException("Input resource type is null");
        }
        if (path == null) {
            throw new IllegalArgumentException("Input path is null");
        }
        this.resources.put(type.makePath(path), resource);
    }

    public MapResourcePack.Resource openResource(final MapResourcePack.ResourceType type, final String path) {
        if (type == null) {
            throw new IllegalArgumentException("Input resource type is null");
        }
        if (path == null) {
            throw new IllegalArgumentException("Input path is null");
        }
        if (this.resources.isEmpty()) {
            return null;
        }
        final String resourcePath = this.resources.get(type.makePath(path));
        if (resourcePath == null) {
            return null;
        }
        return new MapResourcePack.Resource(){
            final /* synthetic */ BlockRenderProvider this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public MapResourcePack getPack() {
                return MapResourcePack.VANILLA;
            }

            @Override
            public MapResourcePack.ResourceType getType() {
                return type;
            }

            @Override
            public String getPath() {
                return path;
            }

            @Override
            public InputStream openStream() throws IOException {
                return BlockRenderProvider.class.getResourceAsStream(resourcePath);
            }
        };
    }

    public Model createModel(MapResourcePack resources, BlockRenderOptions options) {
        return null;
    }

    public void addOptions(BlockRenderOptions options, World world, int x, int y, int z) {
    }

    public abstract Collection<Material> getTypes();

    static {
        String tex_root = CommonBootstrap.evaluateMCVersion(">=", "1.13") ? "block/" : "blocks/";
        String water_overlay = CommonBootstrap.evaluateMCVersion(">=", "1.9") ? "water_overlay" : "water_still";
        BlockRenderProvider.register(new FluidRenderingProvider(tex_root + water_overlay, tex_root + "water_still", "#456ef5", MaterialUtil.ISWATER.getMaterials()));
        BlockRenderProvider.register(new FluidRenderingProvider(tex_root + "lava_still", tex_root + "lava_still", null, MaterialUtil.ISLAVA.getMaterials()));
        BlockRenderProvider.register(new GrassRenderingProvider());
        BlockRenderProvider.register(new RedstoneWireRenderingProvider());
    }
}

