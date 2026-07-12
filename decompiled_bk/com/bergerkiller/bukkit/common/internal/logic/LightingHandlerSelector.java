/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.bases.CheckedFunction;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.logic.LightingHandlerDisabled;
import com.bergerkiller.bukkit.common.internal.logic.LightingHandler_1_14;
import com.bergerkiller.bukkit.common.internal.logic.LightingHandler_1_16_4_StarLightEngine;
import com.bergerkiller.bukkit.common.internal.logic.LightingHandler_1_20;
import com.bergerkiller.bukkit.common.internal.logic.LightingHandler_1_8_to_1_13_2;
import com.bergerkiller.bukkit.common.internal.logic.LightingHandler_CubicChunks_1_12_2;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import java.util.concurrent.CompletableFuture;
import org.bukkit.World;

public final class LightingHandlerSelector
implements LightingHandler {
    public static final LightingHandlerSelector INSTANCE;
    private final LightingHandler fallback = LibraryComponentSelector.forModule(LightingHandler.class).setDefaultComponent((LightingHandler)((Object)((CheckedFunction<LibraryComponentSelector, LightingHandler>)LightingHandlerDisabled::new))).addWhen("Only paper starlight is supported", e -> CommonBootstrap.evaluateMCVersion(">=", "1.21") && CommonUtil.getClass("ca.spottedleaf.moonrise.patches.starlight.light.StarLightEngine") != null, e -> new LightingHandlerDisabled(new IllegalStateException("Only paper starlight is supported"), false)).addVersionOption(null, "1.13.2", LightingHandler_1_8_to_1_13_2::new).addVersionOption("1.14", "1.19.4", LightingHandler_1_14::new).addVersionOption("1.20", null, LightingHandler_1_20::new).update();
    private final LightingHandler cubicchunks = (LightingHandler)((Object)LibraryComponentSelector.forModule(LightingHandler.class).setDefaultComponent((LightingHandler)((Object)((CheckedFunction<LibraryComponentSelector, LightingHandler>)LightingHandlerDisabled::new))).addOption((LibraryComponent.Conditional<Void, CheckedFunction<LibraryComponentSelector, LightingHandler>>)new LibraryComponent.Conditional<Void, LightingHandler>(){

        @Override
        public String getIdentifier() {
            return "CubicChunks Handler";
        }

        @Override
        public boolean isSupported(Void environment) {
            return CommonUtil.getClass("io.github.opencubicchunks.cubicchunks.api.world.ICube") != null;
        }

        @Override
        public LightingHandler create(Void environment) throws Throwable {
            return new LightingHandler_CubicChunks_1_12_2();
        }
    }).update());
    private final LightingHandler starlight = (LightingHandler)((Object)LibraryComponentSelector.forModule(LightingHandler.class).setDefaultComponent((LightingHandler)((Object)((CheckedFunction<LibraryComponentSelector, LightingHandler>)LightingHandlerDisabled::new))).addOption((LibraryComponent.Conditional<Void, CheckedFunction<LibraryComponentSelector, LightingHandler>>)new LibraryComponent.Conditional<Void, LightingHandler>(){

        @Override
        public String getIdentifier() {
            return "StarLight Engine Handler";
        }

        @Override
        public boolean isSupported(Void environment) {
            return CommonUtil.getClass("ca.spottedleaf.moonrise.patches.starlight.light.StarLightEngine") != null;
        }

        @Override
        public LightingHandler create(Void environment) throws Throwable {
            return new LightingHandler_1_16_4_StarLightEngine();
        }
    }).update());

    public boolean isFallbackInitialized() {
        return !(this.fallback instanceof LightingHandlerDisabled);
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isSupported(World world) {
        return this.getHandler(world).isSupported(world);
    }

    private LightingHandler getHandler(World world) {
        if (this.cubicchunks.isSupported(world)) {
            return this.cubicchunks;
        }
        if (this.starlight.isSupported(world)) {
            return this.starlight;
        }
        return this.fallback;
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        return this.getHandler(world).getSectionSkyLight(world, cx, cy, cz);
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        return this.getHandler(world).getSectionBlockLight(world, cx, cy, cz);
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return this.getHandler(world).setSectionSkyLightAsync(world, cx, cy, cz, data);
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        return this.getHandler(world).setSectionBlockLightAsync(world, cx, cy, cz, data);
    }

    static {
        CommonBootstrap.initServer();
        INSTANCE = new LightingHandlerSelector();
    }
}

