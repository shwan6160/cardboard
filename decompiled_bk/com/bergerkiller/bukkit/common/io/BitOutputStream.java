/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.io;

import com.bergerkiller.bukkit.common.io.BitPacket;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream
extends OutputStream {
    private int bitbuff = 0;
    private int bitbuff_len = 0;
    private boolean closed = false;
    private final OutputStream output;
    private final boolean closeOutput;

    public BitOutputStream(OutputStream outputStream) {
        this(outputStream, true);
    }

    public BitOutputStream(OutputStream outputStream, boolean closeOutputStream) {
        this.output = outputStream;
        this.closeOutput = closeOutputStream;
    }

    @Override
    public void write(int b) throws IOException {
        this.writeBits(b, 8);
    }

    public void writeBits(int data, int nBits) throws IOException {
        if (this.closed) {
            throw new IOException("Stream is closed");
        }
        this.bitbuff |= data << this.bitbuff_len;
        this.bitbuff_len += nBits;
        while (this.bitbuff_len >= 8) {
            this.output.write(this.bitbuff & 0xFF);
            this.bitbuff_len -= 8;
            this.bitbuff >>= 8;
        }
    }

    public void writePacket(BitPacket packet) throws IOException {
        this.writeBits(packet.data, packet.bits);
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            if (this.bitbuff_len > 0) {
                this.output.write(this.bitbuff);
                this.bitbuff = 0;
                this.bitbuff_len = 0;
            }
            this.closed = true;
            if (this.closeOutput) {
                this.output.close();
            }
        }
    }
}

