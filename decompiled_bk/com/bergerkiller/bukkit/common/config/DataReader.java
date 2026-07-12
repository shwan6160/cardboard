/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.Logging;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

public abstract class DataReader {
    private final File file;

    public DataReader(Plugin plugin, String filename) {
        this(plugin.getDataFolder(), filename);
    }

    public DataReader(File folder, String filename) {
        this(new File(folder, filename));
    }

    public DataReader(String filepath) {
        this(new File(filepath));
    }

    public DataReader(File file) {
        this.file = file;
    }

    public abstract void read(DataInputStream var1) throws IOException;

    public DataInputStream getStream(InputStream stream) {
        return new DataInputStream(stream);
    }

    public File getFile() {
        return this.file;
    }

    public boolean exists() {
        return this.file.exists();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean read() {
        try (DataInputStream stream22 = this.getStream(new FileInputStream(this.file));){
            this.read(stream22);
            return true;
        }
        catch (FileNotFoundException stream22) {
            return false;
        }
        catch (Throwable t2) {
            Logging.LOGGER_CONFIG.log(Level.SEVERE, "An error occured while loading file '" + this.file + "':", t2);
        }
        return false;
    }
}

