/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.bukkit.common.internal.logic;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.logic.CreaturePreSpawnHandler_Paper;
import com.bergerkiller.bukkit.common.internal.logic.CreaturePreSpawnHandler_Spigot;
import java.util.logging.Level;
import org.bukkit.World;

public abstract class CreaturePreSpawnHandler {
    public static final CreaturePreSpawnHandler INSTANCE;

    public abstract void onEventHasHandlers();

    public abstract void onWorldEnabled(World var1);

    public abstract void onWorldDisabled(World var1);

    static {
        CreaturePreSpawnHandler handler;
        boolean hasPaperPreSpawnEvent = false;
        try {
            Class.forName("com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent");
            hasPaperPreSpawnEvent = true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        try {
            handler = hasPaperPreSpawnEvent ? new CreaturePreSpawnHandler_Paper() : (CommonBootstrap.evaluateMCVersion("<=", "1.17.1") ? new CreaturePreSpawnHandler_Spigot() : new DisabledHandler());
        }
        catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize creature pre spawn handler, event disabled", t);
            handler = new DisabledHandler();
        }
        INSTANCE = handler;
    }

    private static final class DisabledHandler
    extends CreaturePreSpawnHandler {
        private DisabledHandler() {
        }

        @Override
        public void onWorldEnabled(World world) {
        }

        @Override
        public void onWorldDisabled(World world) {
        }

        @Override
        public void onEventHasHandlers() {
        }
    }
}

