/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.properties.standard.type;

public final class TrainDisplayedBlocks {
    public static final int BLOCK_OFFSET_NONE = Integer.MAX_VALUE;
    public static final TrainDisplayedBlocks DEFAULT = new TrainDisplayedBlocks("", Integer.MAX_VALUE);
    private final String typesPattern;
    private final int offset;

    private TrainDisplayedBlocks(String typesPattern, int offset) {
        this.typesPattern = typesPattern;
        this.offset = offset;
    }

    public String getBlockTypesPattern() {
        return this.typesPattern;
    }

    public int getOffset() {
        return this.offset;
    }

    public int hashCode() {
        return this.typesPattern.hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TrainDisplayedBlocks) {
            TrainDisplayedBlocks options = (TrainDisplayedBlocks)o;
            return this.typesPattern.equals(options.typesPattern) && this.offset == options.offset;
        }
        return false;
    }

    public static TrainDisplayedBlocks of(String typesPattern, int offset) {
        return new TrainDisplayedBlocks(typesPattern, offset);
    }
}

