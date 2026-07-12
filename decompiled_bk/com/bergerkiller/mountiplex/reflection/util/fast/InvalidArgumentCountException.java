/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection.util.fast;

public class InvalidArgumentCountException
extends IllegalArgumentException {
    private static final long serialVersionUID = 8575190678319065492L;

    public InvalidArgumentCountException(String type, int given, int expected) {
        super("Invalid amount of arguments for " + type + " (" + given + " given, " + expected + " expected)");
    }
}

