/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.WorldCreator
 */
package com.bergerkiller.bukkit.common.server;

import com.bergerkiller.bukkit.common.offline.OfflineWorld;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.server.CommonServer;
import com.bergerkiller.bukkit.common.server.CommonServerBase;
import com.bergerkiller.bukkit.common.server.CraftBukkitServer;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.world.LoadableWorld;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import com.bergerkiller.generated.net.minecraft.server.level.ServerLevelHandle;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class SpigotServer
extends CraftBukkitServer {
    private boolean _paper;
    private boolean _hasNewPaperWorldFormat;

    @Override
    public boolean init() {
        if (!super.init()) {
            return false;
        }
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            this._paper = true;
        }
        catch (Throwable t1) {
            try {
                Class.forName("com.destroystokyo.paper.PaperMCConfig");
                this._paper = true;
            }
            catch (Throwable t2) {
                this._paper = false;
            }
        }
        try {
            Class.forName(this.CB_ROOT_VERSIONED + ".Spigot");
            return true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            try {
                Class.forName("org.spigotmc.SpigotConfig");
                return true;
            }
            catch (ClassNotFoundException classNotFoundException2) {
                return false;
            }
        }
    }

    @Override
    public void postInit(CommonServer.PostInitEvent event) {
        super.postInit(event);
        this._hasNewPaperWorldFormat = this.isPaperServer() && this.evaluateMCVersion(">=", "26.1");
    }

    public boolean isPaperServer() {
        return this._paper;
    }

    @Override
    public LoadableWorld getLoadableWorld(World world) {
        if (!this._hasNewPaperWorldFormat) {
            return super.getLoadableWorld(world);
        }
        String levelName = MinecraftServerHandle.instance().getLevelName();
        File mainWorldFolder = new File(Bukkit.getWorldContainer(), levelName);
        return this.getPaperLoadableWorld(mainWorldFolder, world);
    }

    private PaperLoadableWorld getPaperLoadableWorld(File mainWorldFolder, World world) {
        return new PaperLoadableWorld(mainWorldFolder, ServerLevelHandle.fromBukkit(world).getDimensionKey(), OfflineWorld.of(world));
    }

    @Override
    public Collection<LoadableWorld> getLoadableWorlds() {
        File[] subFolders;
        if (!this._hasNewPaperWorldFormat) {
            return super.getLoadableWorlds();
        }
        String levelName = MinecraftServerHandle.instance().getLevelName();
        File mainWorldFolder = new File(Bukkit.getWorldContainer(), levelName);
        List loadedWorlds = Bukkit.getWorlds().stream().map(w -> this.getPaperLoadableWorld(mainWorldFolder, (World)w)).collect(Collectors.toList());
        ArrayList<LoadableWorld> loadableWorlds = new ArrayList<LoadableWorld>(loadedWorlds);
        File dimensionsFolder = new File(mainWorldFolder, "dimensions");
        File[] namespaceFolders = dimensionsFolder.listFiles();
        if (namespaceFolders != null) {
            for (File namespaceFolder : namespaceFolders) {
                String namespace = namespaceFolder.getName();
                File[] dimensionFolders = namespaceFolder.listFiles();
                if (dimensionFolders == null) continue;
                for (File dimensionFolder : dimensionFolders) {
                    if (!dimensionFolder.isDirectory()) continue;
                    String dimension = dimensionFolder.getName();
                    boolean loaded = false;
                    for (PaperLoadableWorld loadedWorld : loadedWorlds) {
                        if (!loadedWorld.dimensionNamespace.equals(namespace) || !loadedWorld.dimensionName.equals(dimension)) continue;
                        loaded = true;
                        break;
                    }
                    if (loaded) continue;
                    ResourceKey<World> dimensionKey = ResourceKey.fromPath(ResourceCategory.dimension, namespace, dimension);
                    loadableWorlds.add(new PaperLoadableWorld(mainWorldFolder, dimensionKey, null));
                }
            }
        }
        if ((subFolders = Bukkit.getWorldContainer().listFiles()) != null) {
            for (File subFolder : subFolders) {
                if (mainWorldFolder.equals(subFolder) || !new File(subFolder, "level.dat").exists()) continue;
                loadableWorlds.add(new ConvertedSpigotLoadableWorld(subFolder.getName(), subFolder, null));
            }
        }
        HashMap<String, Integer> byNameCounts = new HashMap<String, Integer>();
        for (LoadableWorld w2 : loadableWorlds) {
            for (String n2 : w2.getNames()) {
                byNameCounts.merge(n2, 1, Integer::sum);
            }
        }
        for (LoadableWorld loadableWorld : loadableWorlds) {
            if (!(loadableWorld instanceof PaperLoadableWorld)) continue;
            ((PaperLoadableWorld)loadableWorld).names.removeIf(n -> byNameCounts.getOrDefault(n, 0) > 1);
        }
        return loadableWorlds;
    }

    @Override
    public void addVariables(Map<String, String> variables) {
        super.addVariables(variables);
        if (this._paper) {
            variables.put("paper", "true");
        }
    }

    @Override
    public String getServerName() {
        return this._paper ? "Paper" : "Spigot";
    }

    protected static class PaperLoadableWorld
    implements LoadableWorld {
        private final File worldFolder;
        private final ResourceKey<World> dimensionKey;
        private final String dimensionNamespace;
        private final String dimensionName;
        private final File dimensionFolder;
        private final List<String> names;
        private OfflineWorld world;

        public PaperLoadableWorld(File worldFolder, ResourceKey<World> dimensionKey, OfflineWorld world) {
            this.worldFolder = worldFolder;
            this.dimensionKey = dimensionKey;
            IdentifierHandle identifier = dimensionKey.getName();
            this.dimensionNamespace = identifier.getNamespace();
            this.dimensionName = identifier.getName();
            this.dimensionFolder = new File(worldFolder, "dimensions" + File.separator + this.dimensionNamespace + File.separator + this.dimensionName);
            this.names = new ArrayList<String>(4);
            if (this.dimensionNamespace.equals("minecraft")) {
                if (this.dimensionName.equals("overworld")) {
                    this.names.add(worldFolder.getName());
                } else if (this.dimensionName.equals("the_nether")) {
                    this.names.add(worldFolder.getName() + "_nether");
                } else if (this.dimensionName.equals("the_end")) {
                    this.names.add(worldFolder.getName() + "_the_end");
                }
            }
            this.names.add(this.dimensionName);
            this.names.add(this.dimensionNamespace + ":" + this.dimensionName);
            this.names.add(worldFolder.getName() + "/" + this.dimensionNamespace + ":" + this.dimensionName);
            this.world = world;
        }

        @Override
        public String getDisplayName() {
            return this.names.get(0);
        }

        @Override
        public Collection<String> getNames() {
            return Collections.unmodifiableList(this.names);
        }

        @Override
        public LoadableWorld.Format getFormat() {
            return LoadableWorld.Format.PAPER;
        }

        @Override
        public World getWorld() {
            if (this.world == null) {
                World loadedWorld = WorldUtil.getWorldByDimensionKey(this.dimensionKey);
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
            return this.dimensionFolder;
        }

        @Override
        public File getLevelFile() {
            return new File(this.worldFolder, "level.dat");
        }

        @Override
        public File getRegionFolder() {
            File regionFolder = new File(this.getDimensionFolder(), "region");
            return regionFolder.isDirectory() ? regionFolder : null;
        }

        @Override
        public WorldCreator getWorldCreator() {
            Method ofKeyMethod;
            Class<?> namespacedKeyType = CommonUtil.getClass("org.bukkit.NamespacedKey");
            try {
                ofKeyMethod = WorldCreator.class.getMethod("ofKey", namespacedKeyType);
            }
            catch (Throwable t) {
                throw new UnsupportedOperationException("Missing ofKey method in WorldCreator", t);
            }
            Object namespacedKey = this.dimensionKey.getName().toBukkit();
            try {
                return (WorldCreator)ofKeyMethod.invoke(null, namespacedKey);
            }
            catch (Throwable t) {
                throw new IllegalStateException("Failed to create world creator", t);
            }
        }

        public String toString() {
            return "PaperLoadableWorld{format=" + (Object)((Object)this.getFormat()) + ", displayName='" + this.getDisplayName() + '\'' + ", worldFolder='" + this.worldFolder + '\'' + ", dimensionNamespace='" + this.dimensionNamespace + '\'' + ", dimensionName='" + this.dimensionName + '\'' + ", world=" + this.getWorld() + '}';
        }
    }

    protected static class ConvertedSpigotLoadableWorld
    extends CommonServerBase.SpigotLoadableWorld {
        public ConvertedSpigotLoadableWorld(String worldName, File worldFolder, OfflineWorld world) {
            super(worldName, worldFolder, world);
        }

        @Override
        public LoadableWorld.Format getFormat() {
            return LoadableWorld.Format.SPIGOT_CONVERTED;
        }
    }
}

