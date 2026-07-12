/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.archive;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.archive.FileOverlayView;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackArchive;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MapResourcePackZipArchive
implements MapResourcePackArchive {
    private ZipFile archive = null;
    private final FileOverlayView overlayView = new FileOverlayView();
    private final File zipFile;

    public MapResourcePackZipArchive(File zipFile) {
        if (zipFile == null) {
            throw new IllegalArgumentException("Zip file cannot be null");
        }
        this.zipFile = zipFile;
    }

    @Override
    public String name() {
        return "ZIP: " + this.zipFile;
    }

    @Override
    public void load(boolean lazy) {
        this.overlayView.clear();
        if (this.openArchive()) {
            try {
                this.overlayView.load(loader -> this.archive.stream().map(ZipEntry::getName).forEach(loader::add));
            }
            catch (Throwable t) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to index resource pack archive", t);
            }
        }
    }

    @Override
    public void configure(MapResourcePack.Metadata metadata) {
        this.overlayView.addOverlays(metadata.getUsedOverlays().stream().map(o -> o.directory).collect(Collectors.toList()));
    }

    private boolean openArchive() {
        boolean preferZip = this.zipFile.getName().toLowerCase(Locale.ENGLISH).endsWith(".zip");
        IOException error = null;
        try {
            this.openWithMode(preferZip);
            return true;
        }
        catch (IOException ex) {
            error = ex;
            this.archive = null;
            try {
                this.openWithMode(!preferZip);
                return true;
            }
            catch (IOException iOException) {
                Logging.LOGGER.log(Level.SEVERE, "Failed to load resource pack " + this.zipFile.getAbsolutePath(), error);
                return false;
            }
        }
    }

    private void openWithMode(boolean useZip) throws IOException {
        this.archive = useZip ? new ZipFile(this.zipFile) : new JarFile(this.zipFile);
    }

    @Override
    public MapResourcePackArchive.ArchiveResource openResource(String path) {
        if (this.archive == null) {
            return null;
        }
        String absoluteFilePath = this.overlayView.getAbsoluteFilePath(path);
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
}

