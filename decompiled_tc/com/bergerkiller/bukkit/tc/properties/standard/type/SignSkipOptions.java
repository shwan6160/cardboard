/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.BlockLocation
 */
package com.bergerkiller.bukkit.tc.properties.standard.type;

import com.bergerkiller.bukkit.common.BlockLocation;
import java.util.Collections;
import java.util.Set;

public final class SignSkipOptions {
    public static final SignSkipOptions NONE = new SignSkipOptions(0, 0, "", Collections.emptySet());
    private final int ignoreCounter;
    private final int skipCounter;
    private final String filter;
    private final Set<BlockLocation> skippedSigns;

    private SignSkipOptions(int ignoreCounter, int skipCounter, String filter, Set<BlockLocation> signs) {
        this.ignoreCounter = ignoreCounter;
        this.skipCounter = skipCounter;
        this.filter = filter;
        this.skippedSigns = signs;
    }

    public int ignoreCounter() {
        return this.ignoreCounter;
    }

    public int skipCounter() {
        return this.skipCounter;
    }

    public boolean hasFilter() {
        return !this.filter.isEmpty();
    }

    public String filter() {
        return this.filter;
    }

    public boolean hasSkippedSigns() {
        return !this.skippedSigns.isEmpty();
    }

    public Set<BlockLocation> skippedSigns() {
        return this.skippedSigns;
    }

    public boolean isActive() {
        return this.ignoreCounter != 0 || this.skipCounter != 0;
    }

    public int hashCode() {
        return this.skipCounter;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SignSkipOptions) {
            SignSkipOptions other = (SignSkipOptions)o;
            return this.ignoreCounter == other.ignoreCounter && this.skipCounter == other.skipCounter && this.filter.equals(other.filter) && this.skippedSigns.equals(other.skippedSigns);
        }
        return false;
    }

    public static SignSkipOptions create(int ignoreCounter, int skipCounter, String filter) {
        return new SignSkipOptions(ignoreCounter, skipCounter, filter, Collections.emptySet());
    }

    public static SignSkipOptions create(int ignoreCounter, int skipCounter, String filter, Set<BlockLocation> skippedSigns) {
        return new SignSkipOptions(ignoreCounter, skipCounter, filter, skippedSigns);
    }
}

