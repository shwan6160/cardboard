/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.archive;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackDirectoryArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackDownloadedArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackZipArchive;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class MapResourcePackAutoArchive
implements MapResourcePackArchive {
    private final String resourcePackPath;
    private MapResourcePackArchive archive;

    public MapResourcePackAutoArchive(String resourcePackPath, String resourcePackHash) {
        this.resourcePackPath = resourcePackPath;
        File file = new File(resourcePackPath);
        if (file.isFile()) {
            this.archive = new MapResourcePackZipArchive(file);
        } else if (file.isDirectory()) {
            this.archive = new MapResourcePackDirectoryArchive(file);
        } else {
            URL resourcePackURL = null;
            try {
                resourcePackURL = new URL(resourcePackPath);
            }
            catch (MalformedURLException malformedURLException) {
                // empty catch block
            }
            if (resourcePackURL != null) {
                this.archive = new MapResourcePackDownloadedArchive(resourcePackURL, resourcePackHash);
            } else {
                Logging.LOGGER_MAPDISPLAY.warning("Resource pack '" + resourcePackPath + "' could not be found or understood");
            }
        }
    }

    @Override
    public String name() {
        return this.resourcePackPath;
    }

    @Override
    public void load(boolean lazy) {
        if (this.archive != null) {
            this.archive.load(lazy);
        }
    }

    @Override
    public void configure(MapResourcePack.Metadata metadata) {
        if (this.archive != null) {
            this.archive.configure(metadata);
        }
    }

    @Override
    public InputStream openFileStream(String path) throws IOException {
        if (this.archive == null) {
            return null;
        }
        return this.archive.openFileStream(path);
    }

    @Override
    public MapResourcePackArchive.ArchiveResource openResource(String path) {
        if (this.archive == null) {
            return null;
        }
        return this.archive.openResource(path);
    }

    @Override
    public List<String> listFiles(String folder, boolean deep) throws IOException {
        if (this.archive == null) {
            return Collections.emptyList();
        }
        return this.archive.listFiles(folder, deep);
    }
}

