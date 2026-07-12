/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.lighting;

import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.internal.logic.LightingHandlerSelector;
import java.util.concurrent.CompletableFuture;
import org.bukkit.World;

public interface LightingHandler
extends LibraryComponent {
    public boolean isSupported(World var1);

    public byte[] getSectionSkyLight(World var1, int var2, int var3, int var4);

    public byte[] getSectionBlockLight(World var1, int var2, int var3, int var4);

    public CompletableFuture<Void> setSectionSkyLightAsync(World var1, int var2, int var3, int var4, byte[] var5);

    public CompletableFuture<Void> setSectionBlockLightAsync(World var1, int var2, int var3, int var4, byte[] var5);

    public static LightingHandler instance() {
        return LightingHandlerSelector.INSTANCE;
    }
}

