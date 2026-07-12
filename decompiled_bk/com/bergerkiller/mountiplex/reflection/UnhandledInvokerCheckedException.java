/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.mountiplex.reflection;

public class UnhandledInvokerCheckedException
extends RuntimeException {
    public UnhandledInvokerCheckedException(Throwable cause) {
        super("An error occurred in the invoked method", cause);
    }
}

