/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ByteArrayIOStream
extends ByteArrayOutputStream {
    protected byte[] buf;
    protected int count;

    public ByteArrayIOStream() {
        this(32);
    }

    public ByteArrayIOStream(int initialCapacity) {
        this.buf = new byte[initialCapacity];
    }

    public InputStream toInputStream() {
        return new ByteArrayInputStream(this.buf, 0, this.count);
    }

    public synchronized void readFrom(InputStream input) throws IOException {
        while (true) {
            this.ensureCapacity(this.count + Math.max(4096, input.available()));
            int numRead = input.read(this.buf, this.count, this.buf.length - this.count);
            if (numRead == -1) break;
            this.count += numRead;
        }
    }

    public synchronized void ensureCapacity(int minCapacity) {
        if (minCapacity - this.buf.length > 0) {
            int oldCapacity = this.buf.length;
            int newCapacity = oldCapacity << 1;
            if (newCapacity - minCapacity < 0) {
                newCapacity = minCapacity;
            }
            if (newCapacity < 0) {
                if (minCapacity < 0) {
                    throw new OutOfMemoryError();
                }
                newCapacity = Integer.MAX_VALUE;
            }
            this.buf = Arrays.copyOf(this.buf, newCapacity);
        }
    }
}

