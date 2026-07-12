/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.io;

import com.bergerkiller.bukkit.common.io.BitPacket;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream
extends InputStream {
    private int bitbuff = 0;
    private int bitbuff_len = 0;
    private boolean eos = false;
    private boolean closed = false;
    private final InputStream input;
    private final boolean closeInput;

    public BitInputStream(InputStream inputStream) {
        this(inputStream, true);
    }

    public BitInputStream(InputStream inputStream, boolean closeInputStream) {
        this.input = inputStream;
        this.closeInput = closeInputStream;
    }

    public boolean isEndOfStream() {
        return this.eos;
    }

    @Override
    public int available() throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        return this.input.available();
    }

    @Override
    public int read() throws IOException {
        return this.readBits(8);
    }

    public int readBits(int nBits) throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        while (this.bitbuff_len < nBits) {
            int readByte = this.input.read();
            if (readByte == -1) {
                this.eos = true;
                return -1;
            }
            this.bitbuff |= readByte << this.bitbuff_len;
            this.bitbuff_len += 8;
        }
        int result = this.bitbuff & (1 << nBits) - 1;
        this.bitbuff >>= nBits;
        this.bitbuff_len -= nBits;
        return result;
    }

    public BitPacket readPacket(int nBits) throws IOException {
        return new BitPacket(this.readBits(nBits), nBits);
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            if (this.closeInput) {
                this.input.close();
            }
        }
    }
}

