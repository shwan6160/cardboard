/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.resource;

public enum ResourcePackStatus {
    ACCEPTED(true),
    DECLINED(false),
    INVALID_URL(false),
    FAILED_DOWNLOAD(false),
    DOWNLOADED(true),
    FAILED_RELOAD(false),
    DISCARDED(false),
    SUCCESSFULLY_LOADED(false);

    private final boolean intermediate;

    private ResourcePackStatus(boolean intermediate) {
        this.intermediate = intermediate;
    }

    public boolean intermediate() {
        return this.intermediate;
    }
}

