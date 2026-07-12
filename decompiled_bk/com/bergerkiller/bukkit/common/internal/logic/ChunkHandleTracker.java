/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.component.LibraryComponent;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.internal.logic.ChunkHandleTracker_Default;
import com.bergerkiller.bukkit.common.internal.logic.ChunkHandleTracker_Spigot_1_21;
import org.bukkit.Chunk;

public interface ChunkHandleTracker
extends LibraryComponent {
    public static final ChunkHandleTracker INSTANCE = ((LibraryComponentSelector)LibraryComponentSelector.forModule(ChunkHandleTracker.class).runFirst(CommonBootstrap::initServer)).addWhen("Spigot-1.21-broken", v -> !Common.IS_PAPERSPIGOT_SERVER && Common.evaluateMCVersion(">=", "1.21") && Common.evaluateMCVersion("<=", "1.21.1"), ChunkHandleTracker_Spigot_1_21::new).setDefaultComponent(ChunkHandleTracker_Default::new).update();

    public void startTracking(CommonPlugin var1);

    public void stopTracking();

    public Object getChunkHandle(Chunk var1);
}

