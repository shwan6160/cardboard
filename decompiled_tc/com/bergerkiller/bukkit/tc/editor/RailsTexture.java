/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.editor;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import java.util.Arrays;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;

public class RailsTexture {
    private static MapTexture default_texture = null;
    private final MapTexture[] textures = new MapTexture[6];
    private final JavaPlugin owner;
    private final String root;

    public RailsTexture() {
        this((JavaPlugin)TrainCarts.plugin, "com/bergerkiller/bukkit/tc/textures/rails/");
    }

    public RailsTexture(JavaPlugin owner, String textureRoot) {
        this.owner = owner;
        this.root = textureRoot;
        if (default_texture == null) {
            default_texture = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/rails/unknown.png");
        }
        Arrays.fill(this.textures, default_texture);
    }

    public RailsTexture setOpposites(BlockFace face, String filename) {
        return this.setOpposites(face, this.load(filename));
    }

    public RailsTexture setOpposites(BlockFace face, MapTexture texture) {
        return this.set(face, texture).set(face.getOppositeFace(), FaceUtil.isVertical((BlockFace)face) ? MapTexture.flipV((MapCanvas)texture) : MapTexture.flipH((MapCanvas)texture));
    }

    public RailsTexture set(BlockFace face, String filename) {
        return this.set(face, this.load(filename));
    }

    public RailsTexture set(BlockFace face, MapTexture texture) {
        this.textures[RailsTexture.faceToIdx((BlockFace)face)] = texture;
        return this;
    }

    public MapTexture get(BlockFace face) {
        return this.textures[RailsTexture.faceToIdx(face)];
    }

    private final MapTexture load(String filename) {
        return MapTexture.loadPluginResource((JavaPlugin)this.owner, (String)(this.root + filename));
    }

    private static final int faceToIdx(BlockFace face) {
        switch (face) {
            case NORTH: {
                return 0;
            }
            case EAST: {
                return 1;
            }
            case SOUTH: {
                return 2;
            }
            case WEST: {
                return 3;
            }
            case UP: {
                return 4;
            }
            case DOWN: {
                return 5;
            }
        }
        return 0;
    }
}

