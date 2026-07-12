/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.config;

public class HeaderBuilder {
    private StringBuilder buffer = new StringBuilder();
    private boolean isFirstHeaderLine = true;

    public boolean handle(CharSequence line) {
        return this.handle(line, 0, line.length());
    }

    public boolean handle(CharSequence line, int contentStart, int contentEnd) {
        if (contentStart == contentEnd) {
            this.buffer.append('\n');
        } else if (line.charAt(contentStart) == '#') {
            if (this.isFirstHeaderLine) {
                this.isFirstHeaderLine = false;
            } else {
                this.buffer.append('\n');
            }
            if (contentStart < contentEnd - 1 && line.charAt(contentStart + 1) == ' ') {
                this.buffer.append(line, contentStart + 2, contentEnd);
            } else {
                this.buffer.append(line, contentStart + 1, contentEnd);
            }
        } else {
            return false;
        }
        return true;
    }

    public void clear() {
        this.buffer.setLength(0);
        this.isFirstHeaderLine = true;
    }

    public boolean hasHeader() {
        return this.buffer.length() > 0;
    }

    public String getHeader() {
        return this.hasHeader() ? this.buffer.toString() : null;
    }
}

