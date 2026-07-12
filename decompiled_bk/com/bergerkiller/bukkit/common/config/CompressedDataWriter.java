/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.config.DataWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import org.bukkit.plugin.Plugin;

public abstract class CompressedDataWriter
extends DataWriter {
    public CompressedDataWriter(File folder, String filename) {
        super(folder, filename);
    }

    public CompressedDataWriter(File file) {
        super(file);
    }

    public CompressedDataWriter(Plugin plugin, String filename) {
        super(plugin, filename);
    }

    public CompressedDataWriter(String filepath) {
        super(filepath);
    }

    @Override
    public DataOutputStream getStream(OutputStream stream) {
        return super.getStream(new DeflaterOutputStream(stream));
    }
}

