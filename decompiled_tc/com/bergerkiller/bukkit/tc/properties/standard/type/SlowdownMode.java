/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.properties.standard.type;

import java.util.Locale;

public enum SlowdownMode {
    FRICTION,
    GRAVITY;


    public final String getKey() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}

