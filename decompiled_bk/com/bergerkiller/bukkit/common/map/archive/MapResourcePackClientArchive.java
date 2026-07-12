/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.archive;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.cdn.MojangIO;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.archive.FileOverlayView;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackArchive;
import com.bergerkiller.bukkit.common.map.gson.MapResourcePackDeserializer;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

public class MapResourcePackClientArchive
implements MapResourcePackArchive {
    private final String minecraftVersion;
    private final Logger log;
    private final File clientJarFile;
    private final File clientJarTempFile;
    private final FileOverlayView overlayView = new FileOverlayView();
    private JarFile archive = null;

    public MapResourcePackClientArchive() {
        this(Common.MC_VERSION);
    }

    public MapResourcePackClientArchive(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
        this.log = Logging.LOGGER;
        File cacheFolder = MojangIO.getCacheFolder();
        this.clientJarFile = new File(cacheFolder, minecraftVersion + ".jar");
        this.clientJarTempFile = new File(cacheFolder, minecraftVersion + ".jar.tmp");
        this.loadArchive();
    }

    @Override
    public String name() {
        return "Vanilla Minecraft " + this.minecraftVersion;
    }

    @Override
    public MapResourcePack.Metadata tryLoadMetadata(MapResourcePackDeserializer deserializer) {
        return MapResourcePack.Metadata.vanilla(this.minecraftVersion);
    }

    @Override
    public void load(boolean lazy) {
        if (this.archive == null && !this.clientJarFile.exists()) {
            if (lazy) {
                this.log.severe("The client for Minecraft " + this.minecraftVersion + " is presently not installed");
                this.log.severe("Since the plugin did not call load(), it will be downloaded now. This may cause lag.");
                this.log.severe("To fix this, please tell the plugin author to call load() on startup.");
            }
            try {
                this.log.warning("To display Minecraft assets (models, textures) on maps, the Minecraft client jar is required");
                this.log.warning("BKCommonLib will now download Minecraft " + this.minecraftVersion + " client jar from Mojang's servers");
                this.log.warning("The file will be installed in: " + this.clientJarFile.toString());
                this.log.warning("By installing this Minecraft client you further agree with Mojang's EULA.");
                this.log.warning("The EULA can be read here: https://account.mojang.com/documents/minecraft_eula");
                MojangIO.VersionManifest versionManifest = MojangIO.downloadJson(MojangIO.VersionManifest.class, "https://launchermeta.mojang.com/mc/game/version_manifest.json");
                if (versionManifest.versions.isEmpty()) {
                    throw new DownloadFailure("Failed to download the game version manifest from Mojangs servers");
                }
                MojangIO.VersionManifest.Version currentVersion = versionManifest.findVersion(this.minecraftVersion);
                if (currentVersion == null) {
                    throw new DownloadFailure("This Minecraft version is not available");
                }
                MojangIO.VersionManifest.VersionAssets versionAssets = MojangIO.downloadJson(MojangIO.VersionManifest.VersionAssets.class, currentVersion.url);
                MojangIO.VersionManifest.VersionAssets.Download download = versionAssets.downloads.get("client");
                if (download == null) {
                    throw new DownloadFailure("This Minecraft version has no downloadable client jar");
                }
                MojangIO.downloadFile("Minecraft Client " + this.minecraftVersion + ".jar", download, this.clientJarTempFile);
                this.clientJarFile.delete();
                this.clientJarTempFile.renameTo(this.clientJarFile);
                this.loadArchive();
                if (this.archive == null) {
                    throw new IOException("Jar file is corrupt");
                }
            }
            catch (DownloadFailure f) {
                this.log.severe("Failed to download the Minecraft " + this.minecraftVersion + " client: " + f.getMessage());
                this.clientJarFile.delete();
                this.clientJarTempFile.delete();
                this.logAlternative();
            }
            catch (Throwable t) {
                this.log.log(Level.SEVERE, "Failed to download the Minecraft " + this.minecraftVersion + " client:", t);
                this.clientJarFile.delete();
                this.clientJarTempFile.delete();
                this.logAlternative();
            }
        }
    }

    private void logAlternative() {
        this.log.severe("If automatically downloading the client is impossible at this time, you can manually install it.");
        this.log.severe("Install the correct Minecraft client jar file for " + this.minecraftVersion + " in the following location:");
        this.log.severe("> " + this.clientJarFile.getAbsolutePath());
    }

    private void loadArchive() {
        this.overlayView.clear();
        if (!this.clientJarFile.exists()) {
            return;
        }
        try {
            this.archive = new JarFile(this.clientJarFile);
        }
        catch (IOException ex) {
            this.archive = null;
            this.log.severe("Failed to load the Minecraft client jar for accessing resources!");
            this.log.severe("In case the file is corrupt, try deleting it so it is re-downloaded:");
            this.log.severe("> " + this.clientJarFile.getAbsolutePath());
            this.log.log(Level.SEVERE, "Loading " + this.clientJarFile.getName() + " failed!", ex);
            return;
        }
        try {
            this.overlayView.load(loader -> this.archive.stream().map(ZipEntry::getName).forEach(loader::add));
        }
        catch (Throwable t) {
            Logging.LOGGER.log(Level.SEVERE, "Failed to index resource pack archive", t);
        }
    }

    @Override
    public MapResourcePackArchive.ArchiveResource openResource(String path) {
        String absoluteFilePath = path;
        if (!this.overlayView.hasAbsoluteFile(absoluteFilePath)) {
            return null;
        }
        return () -> {
            ZipEntry entry = this.archive.getEntry(absoluteFilePath);
            if (entry == null) {
                throw new IOException("Unexpected resource not found: " + path);
            }
            return this.archive.getInputStream(entry);
        };
    }

    @Override
    public List<String> listFiles(String folder, boolean deep) throws IOException {
        return this.overlayView.listFiles(folder, deep);
    }

    private static class DownloadFailure
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DownloadFailure(String message) {
            super(message);
        }
    }
}

