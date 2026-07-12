/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.cdn;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.GsonBuilder;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MojangIO {
    public static File getCacheFolder() {
        File file = CommonBootstrap.isTestMode() ? new File(System.getProperty("user.dir")) : CommonPlugin.getInstance().getDataFolder();
        file = new File(file, "minecraft");
        file.mkdirs();
        return file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T downloadJson(Class<T> type, String urlString) throws IOException {
        Logging.LOGGER.warning("> Fetching JSON from Mojang servers: " + urlString);
        URL url = new URL(urlString);
        try (InputStream inputStream = url.openStream();){
            InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            T t = gson.fromJson((Reader)reader, type);
            return t;
        }
    }

    public static void downloadFile(String title, VersionManifest.VersionAssets.Download download, File destinationFile) throws IOException {
        long logTimeStart = System.currentTimeMillis();
        boolean needsLogging = false;
        boolean hasLoggedProgress = false;
        Logging.LOGGER.warning("> Fetching " + title + " from Mojang servers: " + download.url);
        try (FileOutputStream outStream = new FileOutputStream(destinationFile);){
            URL dlUrl = new URL(download.url);
            try (InputStream dlInputStream = dlUrl.openStream();){
                int n;
                int old_progress = 0;
                long downloaded = 0L;
                byte[] buffer = new byte[4096];
                while ((n = dlInputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, n);
                    downloaded += (long)n;
                    if (download.size == 0L) continue;
                    int progress = (int)(100L * downloaded / download.size);
                    if (progress >= old_progress + 10) {
                        old_progress = progress;
                        needsLogging = true;
                    }
                    if (!needsLogging || System.currentTimeMillis() - logTimeStart <= 1000L) continue;
                    logTimeStart = System.currentTimeMillis();
                    needsLogging = false;
                    hasLoggedProgress = true;
                    Logging.LOGGER.warning("> Downloading " + title + ": " + progress + "%");
                }
            }
        }
        if (needsLogging && hasLoggedProgress) {
            needsLogging = false;
            Logging.LOGGER.warning("> Downloading " + title + ": 100%");
        }
        if (MojangIO.verifyFile(title, download, destinationFile)) {
            Logging.LOGGER.warning("> " + title + " verified, download completed!");
        } else {
            Logging.LOGGER.warning("> " + title + " download completed!");
        }
    }

    private static boolean verifyFile(String title, VersionManifest.VersionAssets.Download download, File file) throws IOException {
        MessageDigest shaDigest;
        BigInteger checksum;
        if (download.sha1 == null) {
            Logging.LOGGER.warning("> Download metadata has no checksum, no file verification will be performed");
            return false;
        }
        try {
            checksum = new BigInteger(download.sha1, 16);
        }
        catch (NumberFormatException ex) {
            Logging.LOGGER.warning("> Download metadata has corrupted checksum, no file verification will be performed");
            return false;
        }
        try {
            shaDigest = MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException e) {
            Logging.LOGGER.warning("> SHA-1 not supported by the JVM, no file verification will be performed");
            return false;
        }
        Logging.LOGGER.warning("> Verifying " + title + "...");
        try (FileInputStream fis = new FileInputStream(file);){
            int bytesCount = 0;
            byte[] buffer = new byte[4096];
            while ((bytesCount = fis.read(buffer)) != -1) {
                shaDigest.update(buffer, 0, bytesCount);
            }
        }
        BigInteger computed = new BigInteger(1, shaDigest.digest());
        if (computed.equals(checksum)) {
            return true;
        }
        throw new IOException("Checksum of downloaded file does not match");
    }

    public static class VersionManifest {
        public static final String URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
        public List<Version> versions = Collections.emptyList();

        public Version findVersion(String minecraftVersion) {
            for (Version version : this.versions) {
                if (version.id == null || !version.id.equals(minecraftVersion)) continue;
                return version;
            }
            return null;
        }

        public static class Version {
            public String id;
            public String url;
        }

        public static class VersionAssets {
            public Map<String, Download> downloads = Collections.emptyMap();

            public static class Download {
                public String sha1;
                public long size;
                public String url;
            }
        }
    }
}

