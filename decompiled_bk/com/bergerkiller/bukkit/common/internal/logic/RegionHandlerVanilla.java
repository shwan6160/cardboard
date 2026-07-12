/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler;
import com.bergerkiller.generated.net.minecraft.world.level.LevelHandle;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.World;

abstract class RegionHandlerVanilla
extends RegionHandler {
    RegionHandlerVanilla() {
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isSupported(World world) {
        return true;
    }

    @Override
    public Set<IntVector3> getRegions3ForXZ(World world, Set<IntVector2> regionXZCoordinates) {
        LevelHandle worldHandle = LevelHandle.fromBukkit(world);
        int minRegionY = worldHandle.getMinBuildHeight() >> 9;
        int maxRegionY = worldHandle.getMaxBuildHeight() - 1 >> 9;
        HashSet<IntVector3> result = new HashSet<IntVector3>(regionXZCoordinates.size());
        for (IntVector2 coord : regionXZCoordinates) {
            for (int ry = minRegionY; ry <= maxRegionY; ++ry) {
                result.add(coord.toIntVector3(ry));
            }
        }
        return result;
    }

    protected IntVector2 getRegionFileCoordinates(File regionFile) {
        String regionFileName = regionFile.getName();
        if (!regionFileName.startsWith("r.") || !regionFileName.endsWith(".mca")) {
            return null;
        }
        int coord_sep_idx = regionFileName.indexOf(46, 2);
        if (coord_sep_idx == -1 || coord_sep_idx >= regionFileName.length() - 4) {
            return null;
        }
        try {
            int rx = Integer.parseInt(regionFileName.substring(2, coord_sep_idx));
            int rz = Integer.parseInt(regionFileName.substring(coord_sep_idx + 1, regionFileName.length() - 4));
            return new IntVector2(rx, rz);
        }
        catch (Exception exception) {
            return null;
        }
    }

    protected File getRegionFile(World world, int rx, int rz) {
        File regionsFolder = Common.SERVER.getWorldRegionFolder(world.getName());
        StringBuilder fileName = new StringBuilder();
        fileName.append("r.").append(rx).append('.').append(rz).append(".mca");
        return new File(regionsFolder, fileName.toString());
    }
}

