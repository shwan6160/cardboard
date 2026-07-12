/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.exception.command;

public class InvalidClaimPlayerNameException
extends RuntimeException {
    private final String arg;
    private static final long serialVersionUID = -1462602705268773036L;

    public InvalidClaimPlayerNameException(String arg) {
        this.arg = arg;
    }

    public String getArgument() {
        return this.arg;
    }
}

