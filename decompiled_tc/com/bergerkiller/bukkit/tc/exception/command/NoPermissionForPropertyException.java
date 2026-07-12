/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.exception.command;

public class NoPermissionForPropertyException
extends RuntimeException {
    private static final long serialVersionUID = -39392068705811986L;
    private final String name;

    public NoPermissionForPropertyException(String name) {
        super("No permission to modify property '" + name + "'");
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

