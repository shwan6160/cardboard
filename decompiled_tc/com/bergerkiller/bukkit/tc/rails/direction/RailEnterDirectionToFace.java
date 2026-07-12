/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.FaceUtil
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.rails.direction;

import com.bergerkiller.bukkit.common.utils.FaceUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.direction.RailEnterDirection;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Locale;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public final class RailEnterDirectionToFace
implements RailEnterDirection {
    private static final EnumMap<BlockFace, RailEnterDirection> byFace = new EnumMap(BlockFace.class);
    private static final EnumMap<BlockFace, RailEnterDirection[]> arrByFace = new EnumMap(BlockFace.class);
    private final BlockFace face;
    private final String name;

    static RailEnterDirection fromFace(BlockFace face) {
        return byFace.computeIfAbsent(face, f -> {
            throw new IllegalArgumentException("Invalid block face: " + f);
        });
    }

    static RailEnterDirection[] arrayFromFace(BlockFace face) {
        return arrByFace.computeIfAbsent(face, f -> {
            throw new IllegalArgumentException("Invalid block face: " + f);
        });
    }

    private RailEnterDirectionToFace(BlockFace face) {
        this.face = face;
        this.name = face.name().toLowerCase(Locale.ENGLISH).substring(0, 1);
    }

    public BlockFace getFace() {
        return this.face;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public double motionDot(Vector motion) {
        return motion.dot(FaceUtil.faceToVector((BlockFace)this.face));
    }

    @Override
    public boolean match(RailState state) {
        return this.face == state.enterFace();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RailEnterDirectionToFace) {
            return this.face.equals((Object)((RailEnterDirectionToFace)o).getFace());
        }
        return false;
    }

    public String toString() {
        return "EnterFrom{face=" + this.face.name().toLowerCase(Locale.ENGLISH) + "}";
    }

    static {
        for (BlockFace face : FaceUtil.BLOCK_SIDES) {
            RailEnterDirectionToFace re = new RailEnterDirectionToFace(face);
            byFace.put(face, re);
            arrByFace.put(face, new RailEnterDirection[]{re});
        }
        for (BlockFace face : BlockFace.values()) {
            if (face.getModX() == 0 || face.getModZ() == 0) continue;
            ArrayList<RailEnterDirection> values = new ArrayList<RailEnterDirection>(2);
            for (BlockFace blockFace : FaceUtil.getFaces((BlockFace)Util.snapFace(face))) {
                values.add(RailEnterDirectionToFace.fromFace(blockFace));
            }
            arrByFace.put(face, values.toArray(new RailEnterDirection[values.size()]));
        }
    }
}

