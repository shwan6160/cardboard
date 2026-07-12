/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.mountiplex.logic.TextValueSequence;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class PlayerGameInfoCache {
    private static final Map<String, TextValueSequence> valueCache = new ConcurrentHashMap<String, TextValueSequence>();

    PlayerGameInfoCache() {
    }

    public static TextValueSequence parseVersion(String version) {
        return valueCache.computeIfAbsent(version, TextValueSequence::parse);
    }
}

