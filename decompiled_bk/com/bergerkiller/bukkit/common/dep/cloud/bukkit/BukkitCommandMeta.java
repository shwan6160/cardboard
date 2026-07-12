/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.key.CloudKey;
import org.apiguardian.api.API;

@API(status=API.Status.STABLE, since="2.0.0")
public final class BukkitCommandMeta {
    public static final CloudKey<String> BUKKIT_DESCRIPTION = CloudKey.of("bukkit_description", String.class);

    private BukkitCommandMeta() {
    }
}

