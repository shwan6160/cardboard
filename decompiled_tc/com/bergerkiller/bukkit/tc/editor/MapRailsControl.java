/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapBlendMode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.editor;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.editor.MapControl;
import com.bergerkiller.bukkit.tc.editor.RailsTexture;
import com.bergerkiller.bukkit.tc.editor.TCMapEditor;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;

public class MapRailsControl
extends MapControl {
    private static final byte[] MARKER_COLORS = new byte[]{18, 30, 50, 122, 66, 42};
    private final boolean[] _directions = new boolean[6];
    private RailsTexture _texture = new RailsTexture();
    private BlockFace face = BlockFace.NORTH;
    private int rotation = 0;
    private int index = -1;
    private int blinkCtr = 0;
    private boolean blinkOn = false;

    public void setRails(RailType type, Block railsBlock) {
        this._texture = type.getRailsTexture(railsBlock);
    }

    public void setDirection(BlockFace direction, boolean enabled) {
        int idx = MapRailsControl.faceToIdx(direction);
        if (this._directions[idx] != enabled) {
            this._directions[idx] = enabled;
            this.draw();
        }
    }

    public boolean getDirection(BlockFace direction) {
        return this._directions[MapRailsControl.faceToIdx(direction)];
    }

    public BlockFace[] getDirections() {
        ArrayList<BlockFace> faces = new ArrayList<BlockFace>(2);
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            if (!this.getDirection(face)) continue;
            faces.add(face);
        }
        return (BlockFace[])LogicUtil.toArray(faces, BlockFace.class);
    }

    @Override
    public void onInit() {
        Arrays.fill(this._directions, true);
        this.updateView();
    }

    @Override
    public void onTick() {
        if (this.updateView()) {
            this.draw();
        }
        if (this.isSelected()) {
            if (this.index == -1) {
                this.nextIndex(1);
            }
            if (++this.blinkCtr >= 6) {
                this.blinkCtr = 0;
                this.blinkOn = !this.blinkOn;
                this.draw();
            }
        } else if (this.index != -1) {
            this.index = -1;
            this.draw();
        }
    }

    @Override
    public void onDraw() {
        MapTexture texture = MapTexture.rotate((MapCanvas)this._texture.get(this.face), (int)this.rotation);
        this.display.getLayer(2).setBlendMode(MapBlendMode.NONE);
        this.display.getLayer(2).draw((MapCanvas)texture, this.x, this.y);
        this.display.getLayer(3).setBlendMode(MapBlendMode.NONE);
        this.display.getLayer(3).clearRectangle(this.x, this.y, texture.getWidth(), texture.getHeight());
        MapTexture arrow = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/arrow.png");
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            BlockFace markerFace = this.getMarkerFace(face);
            if (markerFace == null) continue;
            MapTexture tex = null;
            int i = MapRailsControl.faceToIdx(face);
            if (i == this.index && this.blinkOn) {
                tex = arrow;
            } else if (this._directions[i]) {
                tex = arrow.clone();
                tex.setBlendMode(MapBlendMode.MULTIPLY);
                tex.fill(MARKER_COLORS[i]);
            }
            if (tex == null) continue;
            tex = MapTexture.rotate((MapCanvas)tex, (int)(270 - FaceUtil.faceToYaw((BlockFace)markerFace)));
            int arrow_dx = (texture.getWidth() - tex.getWidth()) / 2;
            int arrow_dy = (texture.getHeight() - tex.getHeight()) / 2;
            if (markerFace == BlockFace.NORTH) {
                arrow_dy = 0;
            } else if (markerFace == BlockFace.EAST) {
                arrow_dx = texture.getWidth() - tex.getWidth();
            } else if (markerFace == BlockFace.SOUTH) {
                arrow_dy = texture.getHeight() - tex.getHeight();
            } else if (markerFace == BlockFace.WEST) {
                arrow_dx = 0;
            }
            this.display.getLayer(3).setBlendMode(MapBlendMode.NONE);
            this.display.getLayer(3).draw((MapCanvas)tex, this.x + arrow_dx, this.y + arrow_dy);
        }
    }

    @Override
    public void onKeyPressed(MapKeyEvent event) {
        if (event.getKey() == MapPlayerInput.Key.UP) {
            this.nextIndex(1);
        } else if (event.getKey() == MapPlayerInput.Key.DOWN) {
            this.nextIndex(-1);
        } else if (event.getKey() == MapPlayerInput.Key.ENTER && this.index >= 0 && this.index < this._directions.length) {
            this._directions[this.index] = !this._directions[this.index];
            this.draw();
        }
    }

    private void nextIndex(int n) {
        do {
            this.index += n;
            if (this.index < 0) {
                this.index = this._directions.length - 1;
                continue;
            }
            if (this.index < this._directions.length) continue;
            this.index = 0;
        } while (this.getMarkerFace(MapRailsControl.idxToFace(this.index)) == null);
        this.draw();
    }

    private BlockFace getMarkerFace(BlockFace face) {
        if (FaceUtil.isVertical((BlockFace)this.face) && FaceUtil.isVertical((BlockFace)face)) {
            return null;
        }
        if (this.face == BlockFace.UP) {
            return FaceUtil.yawToFace((float)(FaceUtil.faceToYaw((BlockFace)face) - this.rotation));
        }
        if (this.face == BlockFace.DOWN) {
            BlockFace result = FaceUtil.yawToFace((float)(FaceUtil.faceToYaw((BlockFace)face) + this.rotation));
            if (FaceUtil.isAlongZ((BlockFace)result)) {
                result = result.getOppositeFace();
            }
            return result;
        }
        if (face == BlockFace.UP) {
            return BlockFace.NORTH;
        }
        if (face == BlockFace.DOWN) {
            return BlockFace.SOUTH;
        }
        BlockFace combined = FaceUtil.yawToFace((float)(FaceUtil.faceToYaw((BlockFace)face) - FaceUtil.faceToYaw((BlockFace)this.face)));
        if (FaceUtil.isAlongX((BlockFace)combined)) {
            return combined;
        }
        return null;
    }

    private boolean updateView() {
        Location loc = ((TCMapEditor)this.display).getOwner().getLocation();
        BlockFace face_new = FaceUtil.yawToFace((float)(loc.getYaw() + 90.0f), (boolean)false);
        int rotation_new = 0;
        if (loc.getPitch() > 70.0f) {
            rotation_new = FaceUtil.faceToNotch((BlockFace)face_new) * 45;
            face_new = BlockFace.UP;
        } else if (loc.getPitch() < -70.0f) {
            rotation_new = -FaceUtil.faceToNotch((BlockFace)face_new) * 45;
            face_new = BlockFace.DOWN;
        } else {
            rotation_new = 0;
        }
        if (face_new != this.face || rotation_new != this.rotation) {
            this.face = face_new;
            this.rotation = rotation_new;
            return true;
        }
        return false;
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

    private static final BlockFace idxToFace(int idx) {
        switch (idx) {
            case 0: {
                return BlockFace.NORTH;
            }
            case 1: {
                return BlockFace.EAST;
            }
            case 2: {
                return BlockFace.SOUTH;
            }
            case 3: {
                return BlockFace.WEST;
            }
            case 4: {
                return BlockFace.UP;
            }
            case 5: {
                return BlockFace.DOWN;
            }
        }
        return BlockFace.NORTH;
    }
}

