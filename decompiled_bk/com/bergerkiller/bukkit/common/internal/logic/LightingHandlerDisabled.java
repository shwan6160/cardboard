/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import org.bukkit.World;

class LightingHandlerDisabled
implements LightingHandler {
    private final Throwable cause;

    public LightingHandlerDisabled(LibraryComponentSelector<?, ?> selector) {
        this(selector.getLastError());
    }

    public LightingHandlerDisabled(Throwable cause) {
        this(cause, true);
    }

    public LightingHandlerDisabled(Throwable cause, boolean logCause) {
        this.cause = cause;
        if (logCause && cause != null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize lighting handler", cause);
        }
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    private UnsupportedOperationException fail() {
        return new UnsupportedOperationException("Failed to initialize lighting handler, BKCommonLib does not support this server", this.cause);
    }

    @Override
    public boolean isSupported(World world) {
        return false;
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        throw this.fail();
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        throw this.fail();
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        throw this.fail();
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        throw this.fail();
    }
}

