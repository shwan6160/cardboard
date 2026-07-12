/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.properties;

import com.bergerkiller.bukkit.tc.properties.api.PropertyParseResult;

public interface IParsable {
    @Deprecated
    default public boolean parseSet(String key, String args) {
        return this.parseAndSet(key, args).isSuccessful();
    }

    public PropertyParseResult<?> parseAndSet(String var1, String var2);
}

