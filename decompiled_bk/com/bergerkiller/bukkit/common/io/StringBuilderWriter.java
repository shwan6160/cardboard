/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.io;

import java.io.Writer;

public class StringBuilderWriter
extends Writer {
    private final StringBuilder builder;

    public StringBuilderWriter() {
        this(new StringBuilder());
    }

    public StringBuilderWriter(StringBuilder builder) {
        this.builder = builder;
    }

    public StringBuilder getBuilder() {
        return this.builder;
    }

    @Override
    public Writer append(char value) {
        this.builder.append(value);
        return this;
    }

    @Override
    public Writer append(CharSequence value) {
        this.builder.append(value);
        return this;
    }

    @Override
    public Writer append(CharSequence value, int start, int end) {
        this.builder.append(value, start, end);
        return this;
    }

    @Override
    public void write(String value) {
        this.builder.append(value);
    }

    @Override
    public void write(char[] value, int offset, int length) {
        this.builder.append(value, offset, length);
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    public String toString() {
        return this.builder.toString();
    }
}

