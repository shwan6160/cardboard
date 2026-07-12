/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.regionchangetracker;

import com.bergerkiller.bukkit.common.internal.regionchangetracker.RegionChangeTrackerHandlerOps;
import java.util.logging.Level;

interface RegionChangeTrackerHandler {
    public String name();

    public boolean isSupported(RegionChangeTrackerHandlerOps var1);

    public HandlerInstance<?> enable(RegionChangeTrackerHandlerOps var1) throws Exception, Error;

    public static abstract class HandlerInstance<H extends RegionChangeTrackerHandler> {
        protected final H handler;
        protected final RegionChangeTrackerHandlerOps ops;

        protected HandlerInstance(H handler, RegionChangeTrackerHandlerOps ops) {
            this.handler = handler;
            this.ops = ops;
        }

        public void notifyError(String message, Throwable error) {
            this.ops.getPlugin().getLogger().log(Level.SEVERE, "[RegionChangeTracker] An error occurred in handler for " + this.handler.name() + ": " + message, error);
        }

        public abstract void disable() throws Exception, Error;
    }
}

