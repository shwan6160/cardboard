/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.rails.direction;

import com.bergerkiller.bukkit.tc.Direction;
import com.bergerkiller.bukkit.tc.controller.components.RailJunction;
import com.bergerkiller.bukkit.tc.controller.components.RailPiece;
import com.bergerkiller.bukkit.tc.controller.components.RailState;
import com.bergerkiller.bukkit.tc.rails.direction.RailEnterDirectionFromJunction;
import com.bergerkiller.bukkit.tc.rails.direction.RailEnterDirectionImpl;
import com.bergerkiller.bukkit.tc.rails.direction.RailEnterDirectionToFace;
import java.util.ArrayList;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public interface RailEnterDirection {
    public static final RailEnterDirection[] ALL = RailEnterDirectionImpl.ALL;

    public String name();

    public double motionDot(Vector var1);

    public boolean match(RailState var1);

    public static RailEnterDirection toFace(BlockFace face) {
        return RailEnterDirectionToFace.fromFace(face);
    }

    public static RailEnterDirection intoDirection(Direction direction, BlockFace forwardDirection) {
        return RailEnterDirection.toFace(direction.getDirection(forwardDirection));
    }

    public static RailEnterDirection fromJunction(RailJunction junction) {
        return new RailEnterDirectionFromJunction(junction);
    }

    public static RailEnterDirection[] parseAll(RailPiece rail, BlockFace forwardDirection, String text) {
        return RailEnterDirectionImpl.parseAll(rail, forwardDirection, text);
    }

    public static BlockFace[] toFacesOnly(RailEnterDirection[] directions) {
        if (directions == null) {
            return null;
        }
        int len = directions.length;
        if (len == 0) {
            return new BlockFace[0];
        }
        if (len == 1) {
            RailEnterDirection dir = directions[0];
            if (dir instanceof RailEnterDirectionToFace) {
                return new BlockFace[]{((RailEnterDirectionToFace)dir).getFace()};
            }
            return new BlockFace[0];
        }
        ArrayList<BlockFace> faces = new ArrayList<BlockFace>(len);
        for (int i = 0; i < len; ++i) {
            RailEnterDirection dir = directions[i];
            if (!(dir instanceof RailEnterDirectionToFace)) continue;
            faces.add(((RailEnterDirectionToFace)dir).getFace());
        }
        return faces.toArray(new BlockFace[faces.size()]);
    }
}

