/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api;

import com.bergerkiller.bukkit.tc.dep.me.m56738.smoothcoasters.api.implementation.Implementation;

class PlayerEntry {
    private Implementation implementation;
    private String version;

    PlayerEntry() {
    }

    public Implementation getImplementation() {
        return this.implementation;
    }

    public void setImplementation(Implementation implementation) {
        this.implementation = implementation;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

