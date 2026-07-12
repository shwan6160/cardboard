/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.collections;

import java.util.Arrays;

public class CharArrayBuffer
implements CharSequence {
    private char[] _buffer;
    private int _position;
    private int _length;

    public CharArrayBuffer() {
        this(new char[0], 0, 0);
    }

    public CharArrayBuffer(String value) {
        this(value.toCharArray(), 0, value.length());
    }

    public CharArrayBuffer(char[] buffer, int pos, int len) {
        this.assign(buffer, pos, len);
    }

    public void assign(char[] buffer, int pos, int len) {
        this._buffer = buffer;
        this._position = pos;
        this._length = len;
    }

    public String copyToString(int length) {
        return new String(this._buffer, this._position, length);
    }

    public void copyTo(char[] dest_buffer, int dest_pos, int length) {
        System.arraycopy(this._buffer, this._position, dest_buffer, dest_pos, length);
    }

    public int swapBuffer(char[] buffer, int pos) {
        this._buffer = buffer;
        this._position = pos;
        return this._position + this._length;
    }

    public int moveToBuffer(char[] buffer, int position) {
        System.arraycopy(this._buffer, this._position, buffer, position, this._length);
        this._buffer = buffer;
        this._position = position;
        return this._position + this._length;
    }

    public int update(CharArrayBuffer value) {
        int original_length = this._length;
        this._length = value._length;
        if (this._length <= original_length) {
            System.arraycopy(value._buffer, value._position, this._buffer, this._position, this._length);
        } else {
            this.assign(Arrays.copyOfRange(value._buffer, value._position, value._length), 0, value._length);
        }
        return this._length - original_length;
    }

    public int update(String value) {
        int original_length = this._length;
        this._length = value.length();
        if (this._length <= original_length) {
            value.getChars(0, this._length, this._buffer, this._position);
        } else {
            this.assign(value.toCharArray(), 0, this._length);
        }
        return this._length - original_length;
    }

    public int update(CharSequence value) {
        int original_length = this._length;
        this._length = value.length();
        if (this._length > original_length) {
            this.assign(new char[this._length], 0, this._length);
        }
        for (int i = 0; i < this._length; ++i) {
            this._buffer[this._position + i] = value.charAt(i);
        }
        return this._length - original_length;
    }

    @Override
    public String toString() {
        return new String(this._buffer, this._position, this._length);
    }

    @Override
    public int length() {
        return this._length;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= this._length) {
            throw new IndexOutOfBoundsException();
        }
        return this._buffer[this._position + index];
    }

    @Override
    public CharArrayBuffer subSequence(int start, int end) {
        if (start < 0 || start >= this._length) {
            throw new IndexOutOfBoundsException("Start index is out of bounds");
        }
        if (end < start || end > this._length) {
            throw new IndexOutOfBoundsException("End index is out of bounds");
        }
        return new CharArrayBuffer(this._buffer, this._position + start, end - start);
    }

    public boolean contentEquals(CharSequence other) {
        if (other.length() != this._length) {
            return false;
        }
        for (int i = 0; i < this._length; ++i) {
            if (this._buffer[this._position + i] == other.charAt(i)) continue;
            return false;
        }
        return true;
    }
}

