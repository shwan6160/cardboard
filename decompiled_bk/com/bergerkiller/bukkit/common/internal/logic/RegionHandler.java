/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.bases.IntCuboid;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandlerSelector;
import com.bergerkiller.mountiplex.reflection.util.LazyInitializedObject;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class RegionHandler
implements LazyInitializedObject,
LibraryComponent {
    public static final RegionHandler INSTANCE = new RegionHandlerSelector();

    public abstract boolean isSupported(World var1);

    public abstract void closeStreams(World var1);

    public abstract Set<IntVector3> getRegions3ForXZ(World var1, Set<IntVector2> var2);

    public abstract Set<IntVector3> getRegions3(World var1);

    @Deprecated
    public final Set<IntVector2> getRegions(World world) {
        Set<IntVector3> coords_3d = this.getRegions3(world);
        HashSet<IntVector2> coords_2d = new HashSet<IntVector2>(coords_3d.size());
        for (IntVector3 coord : coords_3d) {
            coords_2d.add(coord.toIntVector2());
        }
        return coords_2d;
    }

    public abstract BitSet getRegionChunks3(World var1, int var2, int var3, int var4);

    @Deprecated
    public final BitSet getRegionChunks(World world, int rx, int rz) {
        return this.getRegionChunks3(world, rx, 0, rz);
    }

    public abstract boolean isChunkSaved(World var1, int var2, int var3);

    public int getMinHeight(World world) {
        return 0;
    }

    public int getMaxHeight(World world) {
        return world.getMaxHeight();
    }

    public IntCuboid createWorldBorder(Location center, double size) {
        World world = center.getWorld();
        return IntCuboid.createWorldBorder(center, size, this.getMinHeight(world), this.getMaxHeight(world));
    }
}

