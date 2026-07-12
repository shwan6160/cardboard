/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 *  com.bergerkiller.mountiplex.reflection.ClassHook
 *  com.bergerkiller.mountiplex.reflection.ClassHook$HookMethod
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.plugin.RegisteredListener
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.mountiplex.reflection.ClassHook;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

public class EventListenerHook {
    public static void unhook(Class<? extends Event> eventClass) {
        EventListenerHook.hook(eventClass, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T extends Event> void hook(Class<T> eventClass, Handler<T> handler) {
        Function<RegisteredListener, RegisteredListener> mutator;
        EnumMap map;
        HandlerList handlerlist = CommonUtil.getEventHandlerList(eventClass);
        if (handlerlist == null) {
            throw new IllegalArgumentException("Event class " + eventClass.getName() + " has no HandlerList");
        }
        try {
            Field f = HandlerList.class.getDeclaredField("handlerslots");
            boolean wasAccessible = f.isAccessible();
            f.setAccessible(true);
            map = (EnumMap)CommonUtil.unsafeCast((Object)f.get(handlerlist));
            f.setAccessible(wasAccessible);
        }
        catch (Throwable t) {
            throw new RuntimeException("Failed to modify HandlerList", t);
        }
        if (handler != null) {
            Hook hook = new Hook(handler);
            mutator = l -> (RegisteredListener)hook.hook((RegisteredListener)ClassHook.unhook((Object)l));
        } else {
            mutator = l -> (RegisteredListener)Hook.unhook((Object)l);
        }
        HandlerList handlerList = handlerlist;
        synchronized (handlerList) {
            for (List list : map.values()) {
                ListIterator<RegisteredListener> iter = list.listIterator();
                while (iter.hasNext()) {
                    iter.set(mutator.apply((RegisteredListener)iter.next()));
                }
            }
            try {
                Field f = HandlerList.class.getDeclaredField("handlers");
                boolean wasAccessible = f.isAccessible();
                f.setAccessible(true);
                f.set(handlerlist, null);
                f.setAccessible(wasAccessible);
            }
            catch (Throwable t) {
                throw new RuntimeException("Failed to modify HandlerList", t);
            }
        }
        handlerlist.bake();
    }

    @FunctionalInterface
    public static interface Handler<T extends Event> {
        public void handle(RegisteredListener var1, Consumer<Event> var2, T var3);
    }

    public static class Hook
    extends ClassHook<Hook> {
        private final Handler<Event> handler;

        private Hook(Handler<?> handler) {
            this.handler = (Handler)CommonUtil.unsafeCast(handler);
        }

        @ClassHook.HookMethod(value="public void callEvent(org.bukkit.event.Event event)")
        public void callEvent(Event event) {
            RegisteredListener listener = (RegisteredListener)this.instance();
            this.handler.handle(listener, ((Hook)this.base)::callEvent, event);
        }
    }
}

