/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector2
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.util.Model
 *  com.bergerkiller.bukkit.common.math.Vector3
 *  com.bergerkiller.bukkit.common.wrappers.BlockData
 */
package com.bergerkiller.bukkit.tc.attachments.ui.block;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.util.Model;
import com.bergerkiller.bukkit.common.math.Vector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.tc.TCConfig;
import java.util.HashMap;
import java.util.Map;

public class BlockDataTextureCache {
    private static final Map<IntVector2, BlockDataTextureCache> textureCaches = new HashMap<IntVector2, BlockDataTextureCache>();
    private final Map<BlockData, MapTexture> blockTextures = new HashMap<BlockData, MapTexture>();
    private final int width;
    private final int height;
    private final float scale;
    private final int off_x;
    private final int off_y;

    private BlockDataTextureCache(IntVector2 key) {
        this.width = key.x;
        this.height = key.z;
        this.scale = (float)Math.max(this.width, this.height) / 25.7f;
        this.off_x = (int)(this.scale * 24.0f);
        this.off_y = (int)(this.scale * 20.0f);
    }

    public MapTexture get(BlockData data) {
        return this.blockTextures.computeIfAbsent(data, d -> {
            MapTexture texture = MapTexture.createEmpty((int)this.width, (int)this.height);
            Model model = TCConfig.resourcePack.getBlockModel(d);
            texture.setLightOptions(0.0f, 1.0f, new Vector3(-1.0, 1.0, -1.0));
            texture.drawModel(model, this.scale, this.off_x, this.off_y, 225.0f, -60.0f);
            return texture;
        });
    }

    public static BlockDataTextureCache get(int width, int height) {
        return textureCaches.computeIfAbsent(new IntVector2(width, height), BlockDataTextureCache::new);
    }
}

