/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.World
 *  org.bukkit.WorldCreator
 *  org.bukkit.entity.EntityType
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.server.CommonServer;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.world.LoadableWorld;
import com.bergerkiller.mountiplex.logic.TextValueSequence;
import com.bergerkiller.mountiplex.reflection.util.asm.ASMUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.EntityType;

public abstract class CommonServerBase
implements CommonServer {
    public static final Class<? extends Bukkit> SERVER_CLASS = CommonServerBase.findServerClass();

    private static final Class<?> findServerClass() {
        if (Bukkit.getServer() != null) {
            return Bukkit.getServer().getClass();
        }
        Class<?> cbMailClass = CommonUtil.getClass("org.bukkit.craftbukkit.Main");
        if (cbMailClass != null) {
            for (Class<?> type : ASMUtil.findUsedTypes(cbMailClass)) {
                if (!Server.class.isAssignableFrom(type)) continue;
                return type;
            }
        }
        return null;
    }

    @Override
    public LoadableWorld getLoadableWorld(World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
        File worldFolder = StreamUtil.getFileIgnoreCase(Bukkit.getWorldContainer(), world.getName());
        if (WorldUtil.isLoaded(world)) {
            return new SpigotLoadableWorld(world.getName(), worldFolder, OfflineWorld.of(world));
        }
        return new SpigotLoadableWorld(world.getName(), worldFolder, null);
    }

    @Override
    public LoadableWorld findLoadableWorld(String worldName) {
        World world = Bukkit.getWorld((String)worldName);
        if (world != null) {
            return this.getLoadableWorld(world);
        }
        return this.getLoadableWorlds().stream().filter(l -> l.getNames().contains(worldName)).findFirst().orElse(null);
    }

    @Override
    public boolean isLoadableWorld(String worldName) {
        return this.findLoadableWorld(worldName) != null;
    }

    @Override
    public Collection<LoadableWorld> getLoadableWorlds() {
        File[] subFolders = Bukkit.getWorldContainer().listFiles();
        if (subFolders == null) {
            return Collections.emptyList();
        }
        ArrayList<LoadableWorld> loadableWorlds = new ArrayList<LoadableWorld>(subFolders.length);
        HashSet<String> loadedWorldNames = new HashSet<String>();
        for (World world : Bukkit.getWorlds()) {
            loadedWorldNames.add(world.getName());
            loadableWorlds.add(this.getLoadableWorld(world));
        }
        for (File folder : subFolders) {
            if (loadedWorldNames.contains(folder.getName()) || !new File(folder, "level.dat").exists()) continue;
            loadableWorlds.add(new SpigotLoadableWorld(folder.getName(), folder, null));
        }
        return loadableWorlds;
    }

    @Override
    public Collection<String> getLoadableWorldsLegacy() {
        return this.getLoadableWorlds().stream().map(LoadableWorld::getDisplayName).collect(Collectors.toList());
    }

    @Override
    public File getWorldFolder(String worldName) {
        LoadableWorld world = this.findLoadableWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("Invalid world name: " + worldName);
        }
        return world.getDimensionFolder();
    }

    @Override
    public File getWorldLevelFile(String worldName) {
        LoadableWorld world = this.findLoadableWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("Invalid world name: " + worldName);
        }
        return world.getLevelFile();
    }

    @Override
    public File getWorldRegionFolder(String worldName) {
        LoadableWorld world = this.findLoadableWorld(worldName);
        return world == null ? null : world.getRegionFolder();
    }

    @Override
    public String getMinecraftVersionMajor() {
        return CommonServer.cleanVersion(this.getMinecraftVersion());
    }

    @Override
    public String getMinecraftVersionPre() {
        return CommonServer.preVersion(this.getMinecraftVersion());
    }

    @Override
    public boolean evaluateMCVersion(String operand, String version) {
        return TextValueSequence.evaluateText(this.getMinecraftVersion(), operand, version);
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        variables.put("version", this.getMinecraftVersionMajor());
        String pre_version = this.getMinecraftVersionPre();
        if (pre_version != null) {
            variables.put("pre", pre_version);
        }
    }

    @Override
    public String getServerDetails() {
        StringBuilder serverDesc = new StringBuilder(300);
        serverDesc.append(this.getServerName()).append(" (");
        serverDesc.append(this.getServerDescription());
        if (this.isMojangMappings()) {
            serverDesc.append(" | mojmap");
        }
        serverDesc.append(") : ").append(this.getServerVersion());
        return serverDesc.toString();
    }

    @Override
    public boolean isCustomEntityType(EntityType entityType) {
        return false;
    }

    @Override
    public void enable(CommonPlugin plugin) {
    }

    @Override
    public void disable(CommonPlugin plugin) {
    }

    protected static class SpigotLoadableWorld
    implements LoadableWorld {
        private final String worldName;
        private final File worldFolder;
        private OfflineWorld world;

        public SpigotLoadableWorld(String worldName, File worldFolder, OfflineWorld world) {
            this.worldName = worldName;
            this.worldFolder = worldFolder;
            this.world = world;
        }

        @Override
        public String getDisplayName() {
            return this.worldName;
        }

        @Override
        public Collection<String> getNames() {
            return Collections.singletonList(this.worldName);
        }

        @Override
        public LoadableWorld.Format getFormat() {
            return LoadableWorld.Format.SPIGOT;
        }

        @Override
        public World getWorld() {
            if (this.world == null) {
                World loadedWorld = Bukkit.getWorld((String)this.worldName);
                if (loadedWorld != null) {
                    this.world = OfflineWorld.of(loadedWorld);
                    return loadedWorld;
                }
                return null;
            }
            return this.world.getLoadedWorld();
        }

        @Override
        public File getRootFolder() {
            return this.worldFolder;
        }

        @Override
        public File getDimensionFolder() {
            return this.getRootFolder();
        }

        @Override
        public File getLevelFile() {
            return new File(this.getRootFolder(), "level.dat");
        }

        @Override
        public File getRegionFolder() {
            File mainFolder = this.getDimensionFolder();
            File tmp = new File(mainFolder, "region");
            if (tmp.exists()) {
                return tmp;
            }
            tmp = new File(mainFolder, "DIM-1" + File.separator + "region");
            if (tmp.exists()) {
                return tmp;
            }
            tmp = new File(mainFolder, "DIM1" + File.separator + "region");
            if (tmp.exists()) {
                return tmp;
            }
            return null;
        }

        @Override
        public WorldCreator getWorldCreator() {
            return new WorldCreator(this.worldName);
        }

        public String toString() {
            return "SpigotLoadableWorld{format=" + (Object)((Object)this.getFormat()) + ", displayName='" + this.getDisplayName() + '\'' + ", name='" + this.worldName + '\'' + ", world=" + this.getWorld() + '}';
        }
    }
}

