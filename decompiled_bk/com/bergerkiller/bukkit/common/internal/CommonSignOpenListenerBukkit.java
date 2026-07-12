/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventException
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.EventExecutor
 *  org.bukkit.plugin.Plugin
 */
package com.bergerkiller.bukkit.common.internal;

import com.bergerkiller.bukkit.common.internal.CommonListener;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;

class CommonSignOpenListenerBukkit
implements Listener,
EventExecutor {
    public final FastMethod<Void> callback;

    public static void register(CommonPlugin plugin) throws Throwable {
        Class<?> openSignEventType = Class.forName("org.bukkit.event.player.PlayerSignOpenEvent");
        CommonSignOpenListenerBukkit listener = new CommonSignOpenListenerBukkit(openSignEventType);
        Bukkit.getPluginManager().registerEvent(openSignEventType, (Listener)listener, EventPriority.MONITOR, (EventExecutor)listener, (Plugin)plugin);
    }

    private CommonSignOpenListenerBukkit(Class<? extends Event> openSignEventType) {
        ClassResolver resolver = new ClassResolver();
        resolver.setDeclaredClass(openSignEventType);
        resolver.addImport(CommonListener.class.getName());
        MethodDeclaration callbackMethod = new MethodDeclaration(resolver, "public static void callback(PlayerSignOpenEvent event) {\n    if (event.getCause() != PlayerSignOpenEvent$Cause.PLACE) {\n        CommonListener.storeEditedSign(event.getPlayer(), event.getSign());\n    }\n}");
        this.callback = new FastMethod(callbackMethod);
        this.callback.forceInitialization();
    }

    public void execute(Listener listener, Event event) throws EventException {
        this.callback.invoke(null, event);
    }
}

