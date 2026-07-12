/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config;

import com.bergerkiller.bukkit.common.utils.StreamUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TempFileOutputStream
extends OutputStream {
    private final FileOutputStream baseStream;
    private final File outputFile;
    private final File tempFile;
    private boolean closed;

    public TempFileOutputStream(String name) throws IOException {
        this(new File(name), new File(name + ".tmp"));
    }

    public TempFileOutputStream(File file) throws IOException {
        this(file, new File(file.getPath() + ".tmp"));
    }

    public TempFileOutputStream(File outputFile, File tempFile) throws IOException {
        this(outputFile, tempFile, StreamUtil.createOutputStream(tempFile));
    }

    public TempFileOutputStream(File outputFile, File tempFile, FileOutputStream tempBaseStream) {
        this.baseStream = tempBaseStream;
        this.tempFile = tempFile;
        this.outputFile = outputFile;
        this.closed = false;
    }

    @Override
    public void write(int b) throws IOException {
        this.baseStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.baseStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.baseStream.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        this.close(true);
    }

    public void close(boolean completeTransfer) throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        try {
            super.close();
        }
        finally {
            this.baseStream.close();
        }
        if (!completeTransfer) {
            return;
        }
        StreamUtil.atomicReplace(this.tempFile, this.outputFile);
    }
}

