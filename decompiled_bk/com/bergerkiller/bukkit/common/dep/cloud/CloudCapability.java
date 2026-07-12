/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud;

import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface CloudCapability {
    public @NonNull String toString();

    @API(status=API.Status.STABLE)
    public static final class CloudCapabilityMissingException
    extends RuntimeException {
        public CloudCapabilityMissingException(@NonNull CloudCapability capability) {
            super(String.format("Missing capability '%s'", capability));
        }
    }

    @API(status=API.Status.STABLE)
    public static enum StandardCapabilities implements CloudCapability
    {
        ROOT_COMMAND_DELETION;


        @Override
        public @NonNull String toString() {
            return this.name();
        }
    }
}

