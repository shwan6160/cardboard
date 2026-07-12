/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.config.DataReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.bukkit.plugin.Plugin;

public abstract class CompressedDataReader
extends DataReader {
    public CompressedDataReader(File folder, String filename) {
        super(folder, filename);
    }

    public CompressedDataReader(File file) {
        super(file);
    }

    public CompressedDataReader(Plugin plugin, String filename) {
        super(plugin, filename);
    }

    public CompressedDataReader(String filepath) {
        super(filepath);
    }

    @Override
    public DataInputStream getStream(InputStream stream) {
        return super.getStream(new InflaterInputStream(stream));
    }
}

