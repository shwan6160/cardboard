/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.mountiplex.MountiplexUtil;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;

public class ForgeSupport {
    private final Server server;
    private final File worldContainer;

    private ForgeSupport(Server server, File worldContainer) {
        this.server = server;
        this.worldContainer = worldContainer;
    }

    public static ForgeSupport bukkit() {
        return new ForgeSupport(Bukkit.getServer(), Bukkit.getWorldContainer());
    }

    public static ForgeSupport of(File worldContainer) {
        return new ForgeSupport(null, worldContainer);
    }

    public String getMainWorldName() {
        return this.worldContainer.getName();
    }

    public Collection<String> getLoadableWorlds() {
        HashSet<String> rval = new HashSet<String>();
        for (LevelDimension dim : this.listDimensions(this.worldContainer)) {
            rval.add(dim.toBukkitName());
        }
        for (File worldFolder : this.worldContainer.listFiles()) {
            for (LevelDimension dim : this.listDimensions(worldFolder)) {
                rval.add(dim.toBukkitName());
            }
        }
        for (World world : Bukkit.getWorlds()) {
            rval.add(world.getName());
        }
        return rval;
    }

    private Set<LevelDimension> listDimensions(File worldFolder) {
        File levelDatFile = new File(worldFolder, "level.dat");
        if (!levelDatFile.exists()) {
            return Collections.emptySet();
        }
        String rootWorldName = worldFolder.getName();
        try {
            CommonTagCompound dimensions;
            CommonTagCompound worldGenSettings;
            CommonTagCompound levelDat = CommonTagCompound.readFromFile(levelDatFile, true);
            CommonTagCompound data = levelDat.get("Data", CommonTagCompound.class);
            if (data != null && (worldGenSettings = data.get("WorldGenSettings", CommonTagCompound.class)) != null && (dimensions = worldGenSettings.get("dimensions", CommonTagCompound.class)) != null) {
                HashSet<LevelDimension> result = new HashSet<LevelDimension>(dimensions.size());
                for (String modnameAndDimension : dimensions.keySet()) {
                    String dimension;
                    String namespace;
                    if (dimensions.get(modnameAndDimension, CommonTagCompound.class) == null) continue;
                    int modNameEnd = modnameAndDimension.indexOf(58);
                    if (modNameEnd == -1) {
                        namespace = "minecraft";
                        dimension = modnameAndDimension;
                    } else {
                        namespace = modnameAndDimension.substring(0, modNameEnd);
                        dimension = modnameAndDimension.substring(modNameEnd + 1);
                    }
                    result.add(new LevelDimension(rootWorldName, namespace, dimension));
                }
                return result;
            }
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to read level.dat of world " + worldFolder.getName(), t);
        }
        HashSet<LevelDimension> result = new HashSet<LevelDimension>(3);
        result.add(new LevelDimension(rootWorldName, "minecraft", "overworld"));
        if (new File(worldFolder, "DIM-1").exists()) {
            result.add(new LevelDimension(rootWorldName, "minecraft", "the_nether"));
        }
        if (new File(worldFolder, "DIM1").exists()) {
            result.add(new LevelDimension(rootWorldName, "minecraft", "the_end"));
        }
        return result;
    }

    public File getWorldLevelFile(String worldName) {
        LevelDimension dimension = this.fromBukkitName(worldName);
        return new File(this.getWorldRootFolder(dimension.world), "level.dat");
    }

    public File getWorldRegionFolder(String worldName) {
        File regionFolder = new File(this.getWorldFolder(worldName), "region");
        return regionFolder.exists() ? regionFolder : null;
    }

    public File getWorldFolder(String worldName) {
        LevelDimension dimension = this.fromBukkitName(worldName);
        return dimension.toWorldFolder(this.getWorldRootFolder(dimension.world));
    }

    private File getWorldRootFolder(String worldName) {
        String mainWorldName = this.getMainWorldName();
        if (worldName.equalsIgnoreCase(mainWorldName)) {
            return this.worldContainer;
        }
        for (File file : this.worldContainer.listFiles()) {
            if (!file.getName().equalsIgnoreCase(worldName)) continue;
            return file;
        }
        return new File(this.worldContainer, worldName);
    }

    public boolean isLoadableWorld(String worldName) {
        if (this.server != null && this.server.getWorld(worldName) != null) {
            return true;
        }
        LevelDimension dimension = this.fromBukkitName(worldName);
        File rootFolder = this.getWorldRootFolder(dimension.world);
        for (LevelDimension loadableDim : this.listDimensions(rootFolder)) {
            if (!dimension.equals(loadableDim)) continue;
            return true;
        }
        return false;
    }

    private LevelDimension fromBukkitName(String bukkitWorldName) {
        String[] parts = bukkitWorldName.split("\\/");
        if (parts.length == 1) {
            return new LevelDimension(bukkitWorldName, "minecraft", "overworld");
        }
        if (parts.length == 2) {
            if (parts[1].equals("DIM-1")) {
                return new LevelDimension(parts[0], "minecraft", "the_nether");
            }
            if (parts[1].equals("DIM1")) {
                return new LevelDimension(parts[0], "minecraft", "the_end");
            }
            return new LevelDimension(parts[0], "minecraft", parts[1]);
        }
        String partWorldName = parts[0];
        String partModName = parts[1];
        String partDimension = Arrays.stream(parts, 2, parts.length).collect(Collectors.joining("/"));
        return new LevelDimension(partWorldName, partModName, partDimension);
    }

    private static class LevelDimension {
        public final String world;
        public final String namespace;
        public final String dimension;

        public LevelDimension(String world, String namespace, String dimension) {
            this.world = world;
            this.namespace = namespace;
            this.dimension = dimension;
        }

        public String toBukkitName() {
            if (this.namespace.equals("minecraft")) {
                if (this.dimension.equals("overworld")) {
                    return this.world;
                }
                if (this.dimension.equals("the_nether")) {
                    return this.world + "/DIM-1";
                }
                if (this.dimension.equals("the_end")) {
                    return this.world + "/DIM1";
                }
                return this.world + "/" + this.dimension;
            }
            return String.format("%s/%s/%s", this.world, this.namespace, this.dimension);
        }

        public File toWorldFolder(File rootWorldFolder) {
            if (this.namespace.equals("minecraft")) {
                if (this.dimension.equals("the_nether")) {
                    return StreamUtil.getFileIgnoreCase(rootWorldFolder, "DIM-1");
                }
                if (this.dimension.equals("the_end")) {
                    return StreamUtil.getFileIgnoreCase(rootWorldFolder, "DIM1");
                }
                return rootWorldFolder;
            }
            return MountiplexUtil.toStream(rootWorldFolder).map(f -> StreamUtil.getFileIgnoreCase(f, "dimensions")).map(f -> StreamUtil.getFileIgnoreCase(f, this.namespace)).map(f -> StreamUtil.getFileIgnoreCase(f, this.dimension)).findFirst().get();
        }

        public boolean equals(Object o) {
            if (o instanceof LevelDimension) {
                LevelDimension other = (LevelDimension)o;
                return this.world.equals(other.world) && this.namespace.equals(other.namespace) && this.dimension.equals(other.dimension);
            }
            return false;
        }
    }
}

