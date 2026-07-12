/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.config.BasicConfiguration;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class FileConfiguration
extends BasicConfiguration {
    private static final Map<File, CompletableFuture<Void>> ongoingSaves = new HashMap<File, CompletableFuture<Void>>();
    private final File file;

    public FileConfiguration(JavaPlugin plugin) {
        this(plugin, "config.yml");
    }

    public FileConfiguration(JavaPlugin plugin, String filepath) {
        this(new File(CommonUtil.getPluginDataFolder((Plugin)plugin), filepath));
    }

    public FileConfiguration(String filepath) {
        this(new File(filepath));
    }

    public FileConfiguration(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File is not allowed to be null!");
        }
        this.file = file;
    }

    public boolean exists() {
        return this.file.exists();
    }

    public void load() {
        FileConfiguration.flushSaveOperation(this.file);
        if (!this.file.exists()) {
            return;
        }
        try {
            if (CommonPlugin.hasInstance() && CommonPlugin.getInstance().forceSynchronousSaving()) {
                byte[] data_bytes = Files.readAllBytes(this.file.toPath());
                String s = new String(data_bytes, StandardCharsets.UTF_8);
                char[] data = s.toCharArray();
                for (int i = 0; i < data.length; ++i) {
                    if (data[i] != '\u0000') continue;
                    int num_nul_chars = 1;
                    for (int j = i + 1; j < data.length && data[j] == '\u0000'; ++j) {
                        ++num_nul_chars;
                    }
                    char[] new_data = new char[data.length - num_nul_chars];
                    System.arraycopy(data, 0, new_data, 0, i);
                    System.arraycopy(data, i + num_nul_chars, new_data, i, new_data.length - i);
                    data = new_data;
                    --i;
                }
                this.loadFromReader(new CharArrayReader(data));
            } else {
                this.loadFromStream(new FileInputStream(this.file));
            }
        }
        catch (Throwable t) {
            Logging.LOGGER_CONFIG.log(Level.SEVERE, "An error occured while loading file '" + this.file + "'");
            try {
                File backup = new File(this.file.getPath() + ".old");
                StreamUtil.copyFile(this.file, backup);
                Logging.LOGGER_CONFIG.log(Level.SEVERE, "A backup of this (corrupted?) file named '" + backup.getName() + "' can be found in case you wish to restore", t);
            }
            catch (IOException ex) {
                Logging.LOGGER_CONFIG.log(Level.SEVERE, "A backup of this (corrupted?) file could not be made and its contents may be lost (overwritten)", t);
            }
        }
    }

    public void saveSync() {
        this.save();
        FileConfiguration.flushSaveOperation(this.file);
    }

    public void save() {
        final boolean fileWasGenerated = !this.exists();
        FileConfiguration.putSaveOperation(this.file, Long.MAX_VALUE, () -> {
            final CompletableFuture<Void> future = this.saveToFileAsync(this.file);
            return future.handle(new BiFunction<Void, Throwable, Void>(){
                final /* synthetic */ FileConfiguration this$0;
                {
                    this.this$0 = this$0;
                }

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public Void apply(Void ignored, Throwable error) {
                    Map map = ongoingSaves;
                    synchronized (map) {
                        CompletableFuture removed = (CompletableFuture)ongoingSaves.remove(this.this$0.file);
                        if (removed != null && removed != future) {
                            ongoingSaves.put(this.this$0.file, removed);
                        }
                    }
                    if (error != null) {
                        Logging.LOGGER_CONFIG.log(Level.SEVERE, "An error occured while saving to file '" + this.this$0.file + "':", error);
                        return null;
                    }
                    if (fileWasGenerated) {
                        Logging.LOGGER_CONFIG.log(Level.INFO, "File '" + this.this$0.file + "' has been generated");
                    }
                    return null;
                }
            });
        });
    }

    public static void flushSaveOperation(File file) {
        FileConfiguration.flushSaveOperation(file, Long.MAX_VALUE);
    }

    public static boolean flushSaveOperation(File file, long timeout) {
        return FileConfiguration.putSaveOperation(file, timeout, null);
    }

    public static void flushAllSaveOperations() {
        FileConfiguration.flushAllSaveOperationsInDirectory(null);
    }

    public static void flushAllSaveOperationsInDirectory(File directory) {
        for (File file : FileConfiguration.findSaveOperationsInDirectory(directory)) {
            FileConfiguration.flushSaveOperation(file);
        }
    }

    public static void flushAllSaveOperationsForPlugin(Plugin plugin) {
        FileConfiguration.flushAllSaveOperationsInDirectory(plugin.getDataFolder());
    }

    public static Iterable<File> findSaveOperationsInDirectory(final File directory) {
        return () -> new Iterator<File>(){
            private File _next = null;

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public boolean hasNext() {
                if (this._next != null) {
                    return true;
                }
                Map map = ongoingSaves;
                synchronized (map) {
                    if (directory == null) {
                        Iterator iterator = ongoingSaves.keySet().iterator();
                        if (iterator.hasNext()) {
                            File file;
                            this._next = file = (File)iterator.next();
                            return true;
                        }
                    } else {
                        Path directoryPath = directory.getAbsoluteFile().toPath();
                        for (File file : ongoingSaves.keySet()) {
                            if (!file.getAbsoluteFile().toPath().startsWith(directoryPath)) continue;
                            this._next = file;
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public File next() {
                if (this.hasNext()) {
                    File result = this._next;
                    this._next = null;
                    return result;
                }
                throw new NoSuchElementException();
            }
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean putSaveOperation(File file, long timeout, Supplier<CompletableFuture<Void>> futureSupplier) {
        while (true) {
            CompletableFuture<Void> existing;
            Map<File, CompletableFuture<Void>> map = ongoingSaves;
            synchronized (map) {
                existing = ongoingSaves.remove(file);
                if (existing == null || existing.isDone()) {
                    if (futureSupplier != null) {
                        ongoingSaves.put(file, futureSupplier.get());
                        return true;
                    }
                    return true;
                }
                ongoingSaves.put(file, existing);
            }
            if (timeout <= 0L) {
                return false;
            }
            try {
                if (timeout == Long.MAX_VALUE) {
                    existing.get();
                    continue;
                }
                existing.get(timeout, TimeUnit.MILLISECONDS);
                continue;
            }
            catch (TimeoutException ex) {
                return false;
            }
            catch (Throwable throwable) {
                continue;
            }
            break;
        }
    }
}

