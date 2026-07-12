/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.bases.CheckedFunction;
import com.bergerkiller.bukkit.common.bases.IntCuboid;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandlerDisabled;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler_CubicChunks_1_12_2;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler_Vanilla_1_14;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler_Vanilla_1_17;
import com.bergerkiller.bukkit.common.internal.logic.RegionHandler_Vanilla_1_8;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.BitSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.World;

final class RegionHandlerSelector
extends RegionHandler {
    private final RegionHandler fallback = LibraryComponentSelector.forModule(RegionHandler.class).setDefaultComponent((RegionHandler)((Object)((CheckedFunction<LibraryComponentSelector, RegionHandler>)RegionHandlerDisabled::new))).addVersionOption(null, "1.13.2", RegionHandler_Vanilla_1_8::new).addVersionOption("1.14", "1.16.5", RegionHandler_Vanilla_1_14::new).addVersionOption("1.17", null, RegionHandler_Vanilla_1_17::new).update();
    private final RegionHandler cubicchunks = CommonUtil.getClass("io.github.opencubicchunks.cubicchunks.api.world.ICube") != null ? LogicUtil.tryCreate(RegionHandler_CubicChunks_1_12_2::new, RegionHandlerDisabled::new) : new RegionHandlerDisabled((Throwable)null);

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isSupported(World world) {
        return this.getHandler(world).isSupported(world);
    }

    @Override
    public void forceInitialization() {
        this.fallback.forceInitialization();
        if (!(this.cubicchunks instanceof RegionHandlerDisabled)) {
            this.cubicchunks.forceInitialization();
        }
    }

    private RegionHandler getHandler(World world) {
        if (this.cubicchunks.isSupported(world)) {
            return this.cubicchunks;
        }
        return this.fallback;
    }

    @Override
    public int getMinHeight(World world) {
        return this.getHandler(world).getMinHeight(world);
    }

    @Override
    public int getMaxHeight(World world) {
        return this.getHandler(world).getMaxHeight(world);
    }

    @Override
    public IntCuboid createWorldBorder(Location center, double size) {
        return this.getHandler(center.getWorld()).createWorldBorder(center, size);
    }

    @Override
    public void closeStreams(World world) {
        this.getHandler(world).closeStreams(world);
    }

    @Override
    public Set<IntVector3> getRegions3ForXZ(World world, Set<IntVector2> regionXZCoordinates) {
        return this.getHandler(world).getRegions3ForXZ(world, regionXZCoordinates);
    }

    @Override
    public Set<IntVector3> getRegions3(World world) {
        return this.getHandler(world).getRegions3(world);
    }

    @Override
    public BitSet getRegionChunks3(World world, int rx, int ry, int rz) {
        return this.getHandler(world).getRegionChunks3(world, rx, ry, rz);
    }

    @Override
    public boolean isChunkSaved(World world, int cx, int cz) {
        return this.getHandler(world).isChunkSaved(world, cx, cz);
    }
}

