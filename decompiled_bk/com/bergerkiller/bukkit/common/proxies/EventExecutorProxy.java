/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventException
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.EventExecutor
 */
package com.bergerkiller.bukkit.common.proxies;

import com.bergerkiller.bukkit.common.proxies.ProxyBase;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public class EventExecutorProxy
extends ProxyBase<EventExecutor>
implements EventExecutor {
    public EventExecutorProxy(EventExecutor base) {
        super(base);
    }

    public void execute(Listener listener, Event event) throws EventException {
        ((EventExecutor)this.base).execute(listener, event);
    }
}

