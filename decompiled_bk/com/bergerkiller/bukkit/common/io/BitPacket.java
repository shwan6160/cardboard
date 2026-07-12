/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.io;

public class BitPacket
implements Cloneable {
    public int data;
    public int bits;

    public BitPacket() {
        this.data = 0;
        this.bits = 0;
    }

    public BitPacket(int data, int bits) {
        this.data = data;
        this.bits = bits;
    }

    public int read(int nBits) {
        int result = this.data & (1 << nBits) - 1;
        this.data >>= nBits;
        this.bits -= nBits;
        return result;
    }

    public void write(int data, int nBits) {
        this.data |= data << this.bits;
        this.bits += nBits;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BitPacket) {
            BitPacket other = (BitPacket)o;
            if (other.bits == this.bits) {
                int mask = (1 << this.bits) - 1;
                return (this.data & mask) == (other.data & mask);
            }
            return false;
        }
        return false;
    }

    public BitPacket clone() {
        return new BitPacket(this.data, this.bits);
    }

    public String toString() {
        String str = Integer.toBinaryString(this.data & (1 << this.bits) - 1);
        while (str.length() < this.bits) {
            str = "0" + str;
        }
        return str;
    }
}

