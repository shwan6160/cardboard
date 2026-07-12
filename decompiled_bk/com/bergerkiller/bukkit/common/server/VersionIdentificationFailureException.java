/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.server;

public final class VersionIdentificationFailureException
extends RuntimeException {
    private static final long serialVersionUID = -3513069083904231139L;

    public VersionIdentificationFailureException(String reason) {
        super("Failed to identify the Minecraft version of the server: " + reason);
    }

    public VersionIdentificationFailureException(Throwable cause) {
        super("Failed to identify the Minecraft version of the server", cause);
    }
}

