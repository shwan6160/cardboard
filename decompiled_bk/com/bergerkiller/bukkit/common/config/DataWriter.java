/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.utils.StreamUtil;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

public abstract class DataWriter {
    private final File file;

    public DataWriter(Plugin plugin, String filename) {
        this(plugin.getDataFolder(), filename);
    }

    public DataWriter(File folder, String filename) {
        this(new File(folder, filename));
    }

    public DataWriter(String filepath) {
        this(new File(filepath));
    }

    public DataWriter(File file) {
        this.file = file;
    }

    public abstract void write(DataOutputStream var1) throws IOException;

    public DataOutputStream getStream(OutputStream stream) {
        return new DataOutputStream(stream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean write() {
        try (DataOutputStream stream = this.getStream(StreamUtil.createOutputStream(this.file));){
            this.write(stream);
            return true;
        }
        catch (FileNotFoundException ex) {
            Logging.LOGGER_CONFIG.log(Level.SEVERE, "Failed to access file '" + this.file + "' for saving:", ex);
            return false;
        }
        catch (Throwable t2) {
            Logging.LOGGER_CONFIG.log(Level.SEVERE, "Failed to save to file '" + this.file + "':", t2);
        }
        return false;
    }
}

