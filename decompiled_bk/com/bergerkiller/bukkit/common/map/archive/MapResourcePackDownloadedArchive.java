/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.archive;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.GsonBuilder;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.map.MapResourcePack;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackZipArchive;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapResourcePackDownloadedArchive
implements MapResourcePackArchive {
    private final File resourcepacksFolder;
    private final URL resourcePackURL;
    private final Optional<String> resourcePackHash;
    private final Logger log;
    private MapResourcePackArchive archive = null;

    public MapResourcePackDownloadedArchive(URL resourcePackURL, String resourcePackHash) {
        if (resourcePackURL == null) {
            throw new IllegalArgumentException("Resource pack URL cannot be null");
        }
        this.resourcePackURL = resourcePackURL;
        this.resourcePackHash = resourcePackHash == null || resourcePackHash.isEmpty() ? Optional.empty() : Optional.of(resourcePackHash);
        this.log = Logging.LOGGER_MAPDISPLAY;
        File pluginFolder = CommonBootstrap.isTestMode() ? new File(System.getProperty("user.dir")) : CommonPlugin.getInstance().getDataFolder();
        this.resourcepacksFolder = new File(pluginFolder, "resourcepacks");
    }

    @Override
    public String name() {
        return "URL: " + this.resourcePackURL;
    }

    @Override
    public void load(boolean lazy) {
        PackIndex.Entry foundEntry;
        File resourcepacksIndex;
        Gson gson;
        PackIndex index;
        boolean indexChanged;
        block36: {
            PackDownload download;
            block35: {
                this.resourcepacksFolder.mkdirs();
                indexChanged = false;
                index = new PackIndex();
                gson = new GsonBuilder().create();
                resourcepacksIndex = new File(this.resourcepacksFolder, "index.json");
                if (resourcepacksIndex.isFile()) {
                    try (FileReader reader = new FileReader(resourcepacksIndex);){
                        index = gson.fromJson((Reader)reader, PackIndex.class);
                    }
                    catch (Throwable t) {
                        this.log.log(Level.SEVERE, "Failed to read resource packs index", t);
                    }
                }
                if (index == null) {
                    index = new PackIndex();
                }
                foundEntry = null;
                Iterator<PackIndex.Entry> entryIter = index.entries.iterator();
                while (entryIter.hasNext()) {
                    PackIndex.Entry entry = entryIter.next();
                    File entryFile = new File(this.resourcepacksFolder, entry.file);
                    if (!entryFile.isFile()) {
                        entryIter.remove();
                        this.log.log(Level.WARNING, "Resource pack " + entry.file + " no longer exists in the cache");
                        indexChanged = true;
                        continue;
                    }
                    if (entry.url == null) {
                        entryIter.remove();
                        this.log.log(Level.WARNING, "Resource pack " + entry.file + " has an invalid URL set");
                        indexChanged = true;
                        continue;
                    }
                    if (!entry.url.equals(this.resourcePackURL)) continue;
                    foundEntry = entry;
                }
                if (foundEntry == null) break block35;
                if (this.resourcePackHash.map(foundEntry::checkHash).orElse(true).booleanValue()) break block36;
            }
            if (lazy) {
                this.log.severe("A resource pack is being downloaded while the server is running. This will result in lag.");
            }
            if ((download = this.downloadPack()) != null) {
                File f;
                if (foundEntry == null) {
                    foundEntry = new PackIndex.Entry();
                    index.entries = new ArrayList<PackIndex.Entry>(index.entries);
                    index.entries.add(foundEntry);
                }
                if (foundEntry.file.equalsIgnoreCase(download.realName) && !(f = new File(this.resourcepacksFolder, foundEntry.file)).delete()) {
                    this.log.warning("Failed to delete outdated resource pack: " + foundEntry.file);
                }
                File resFile = new File(this.resourcepacksFolder, download.realName);
                int i = 1;
                while (resFile.isFile()) {
                    String prefix = download.realName;
                    String postfix = "";
                    int extIdx = download.realName.lastIndexOf(46);
                    if (extIdx != -1) {
                        prefix = download.realName.substring(0, extIdx);
                        postfix = download.realName.substring(extIdx);
                    }
                    resFile = new File(this.resourcepacksFolder, prefix + i + postfix);
                    ++i;
                }
                File tmpFile = new File(this.resourcepacksFolder, download.tempName);
                if (!tmpFile.renameTo(resFile)) {
                    this.log.warning("Failed to rename " + download.tempName + " to " + resFile.getName());
                }
                foundEntry.url = this.resourcePackURL;
                foundEntry.file = resFile.getName();
                foundEntry.hash = download.hash;
                indexChanged = true;
                if (!this.resourcePackHash.map(foundEntry::checkHash).orElse(true).booleanValue()) {
                    this.log.warning("The downloaded resource pack SHA-1 hash does not match with what was expected");
                    this.log.warning("Expected " + this.resourcePackHash.get() + ", but was " + download.hash);
                    this.log.warning("The resource pack will be re-downloaded every time it is loaded unless this is fixed!");
                }
            }
        }
        if (indexChanged) {
            try (FileWriter writer = new FileWriter(resourcepacksIndex);){
                gson.toJson((Object)index, (Appendable)writer);
            }
            catch (Throwable t) {
                this.log.log(Level.SEVERE, "Failed to write resource packs index", t);
            }
        }
        if (foundEntry != null) {
            this.archive = new MapResourcePackZipArchive(new File(this.resourcepacksFolder, foundEntry.file));
            this.archive.load(lazy);
        } else {
            this.archive = null;
        }
    }

    @Override
    public void configure(MapResourcePack.Metadata metadata) {
        if (this.archive != null) {
            this.archive.configure(metadata);
        }
    }

    private PackDownload downloadPack() {
        this.log.warning("Downloading resource pack " + this.resourcePackURL + "...");
        URL currURL = this.resourcePackURL;
        for (int n = 0; n < 10; ++n) {
            try {
                URL redirectURL;
                int code;
                URLConnection con = currURL.openConnection();
                con.addRequestProperty("X-Minecraft-Version-ID", Common.MC_VERSION);
                con.addRequestProperty("X-Minecraft-Version", Common.MC_VERSION);
                con.addRequestProperty("User-Agent", "Minecraft Java/" + Common.MC_VERSION);
                con.addRequestProperty("X-Minecraft-Username", "plugin_bkcommonlib");
                con.addRequestProperty("X-Minecraft-UUID", new UUID(0L, 0L).toString());
                con.setReadTimeout(60000);
                if (!(con instanceof HttpURLConnection) || (code = ((HttpURLConnection)con).getResponseCode()) != 301 && code != 302) {
                    return this.downloadPackFileContent(con, currURL);
                }
                String redirectLocation = con.getHeaderField("Location");
                redirectLocation = URLDecoder.decode(redirectLocation, "UTF-8");
                currURL = redirectURL = new URL(currURL, redirectLocation);
                this.log.warning("Redirected to " + redirectURL + "...");
                ((HttpURLConnection)con).disconnect();
                continue;
            }
            catch (IOException ex) {
                this.log.log(Level.SEVERE, "Failed to start download for " + currURL, ex);
                return null;
            }
        }
        this.log.log(Level.SEVERE, "Reached the maximum of 10 redirects downloading " + this.resourcePackURL);
        return null;
    }

    private PackDownload downloadPackFileContent(URLConnection con, URL url) throws IOException {
        int filenameEndIndex;
        int filenameIndex;
        PackDownload download = new PackDownload();
        String fieldValue = con.getHeaderField("Content-Disposition");
        if (fieldValue != null && (filenameIndex = fieldValue.indexOf("filename=\"")) != -1 && (filenameEndIndex = fieldValue.indexOf(34, filenameIndex += 10)) != -1) {
            download.realName = fieldValue.substring(filenameIndex, filenameEndIndex);
        }
        if (download.realName == null) {
            download.realName = new File(url.getPath()).getName();
        }
        download.tempName = download.realName + ".tmp";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException ex) {
            this.log.warning("Resource pack SHA-1 hashing is not available");
        }
        File tempFile = new File(this.resourcepacksFolder, download.tempName);
        try (InputStream connInput = con.getInputStream();
             FileOutputStream tmpOutput = new FileOutputStream(tempFile);){
            int len;
            byte[] arr = new byte[4096];
            while ((len = connInput.read(arr)) != -1) {
                tmpOutput.write(arr, 0, len);
                if (md != null) {
                    md.update(arr, 0, len);
                }
                download.size += (long)len;
            }
        }
        download.hash = "";
        if (md != null) {
            Formatter formatter = new Formatter();
            for (byte b : md.digest()) {
                formatter.format("%02x", b);
            }
            download.hash = formatter.toString();
            formatter.close();
        }
        this.log.warning("Resource pack downloaded (" + download.realName + ", " + download.size + " bytes)");
        return download;
    }

    @Override
    public InputStream openFileStream(String path) throws IOException {
        return this.archive == null ? null : this.archive.openFileStream(path);
    }

    @Override
    public MapResourcePackArchive.ArchiveResource openResource(String path) {
        return this.archive == null ? null : this.archive.openResource(path);
    }

    @Override
    public List<String> listFiles(String folder, boolean deep) throws IOException {
        return this.archive == null ? Collections.emptyList() : this.archive.listFiles(folder, deep);
    }

    private static class PackIndex {
        public List<Entry> entries = Collections.emptyList();

        private PackIndex() {
        }

        public static class Entry {
            public URL url = null;
            public String file = "";
            public String hash = "";

            public boolean checkHash(String sha1Hash) {
                if (this.hash == null || this.hash.isEmpty()) {
                    return true;
                }
                return this.hash.equalsIgnoreCase(sha1Hash);
            }
        }
    }

    private static class PackDownload {
        public String tempName = null;
        public String realName = null;
        public String hash = null;
        public long size = 0L;

        private PackDownload() {
        }
    }
}

