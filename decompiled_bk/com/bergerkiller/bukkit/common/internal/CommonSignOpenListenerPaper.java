/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.block.Sign
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventException
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerEvent
 *  org.bukkit.plugin.EventExecutor
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.internal.CommonListener;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

class CommonSignOpenListenerPaper
implements Listener,
EventExecutor {
    private final Enum<?> PLACE_CAUSE;
    private final FastMethod<Sign> getSignMethod;
    private final FastMethod<? extends Enum> getCauseMethod;

    private CommonSignOpenListenerPaper(Class<? extends Event> openSignEventType) throws Throwable {
        Class<?> causeType = Class.forName("io.papermc.paper.event.player.PlayerOpenSignEvent$Cause");
        this.PLACE_CAUSE = Arrays.stream((Enum[])causeType.getEnumConstants()).filter(n -> n.name().equals("PLACE")).findFirst().orElseThrow(() -> new UnsupportedOperationException("Cause enum PLACE not found"));
        this.getSignMethod = new FastMethod(openSignEventType.getMethod("getSign", new Class[0]));
        this.getSignMethod.forceInitialization();
        this.getCauseMethod = new FastMethod(openSignEventType.getMethod("getCause", new Class[0]));
        this.getCauseMethod.forceInitialization();
    }

    public static void register(CommonPlugin plugin) throws Throwable {
        Class<?> openSignEventType = Class.forName("io.papermc.paper.event.player.PlayerOpenSignEvent");
        CommonSignOpenListenerPaper listener = new CommonSignOpenListenerPaper(openSignEventType);
        Bukkit.getPluginManager().registerEvent(openSignEventType, (Listener)listener, EventPriority.MONITOR, (EventExecutor)listener, (Plugin)plugin);
    }

    public void execute(Listener listener, Event event) throws EventException {
        if (((Cancellable)event).isCancelled()) {
            return;
        }
        if (this.getCauseMethod.invoke(event) != this.PLACE_CAUSE) {
            return;
        }
        Player player = ((PlayerEvent)event).getPlayer();
        Sign sign = this.getSignMethod.invoke(event);
        CommonListener.storeEditedSign(player, sign);
    }
}

