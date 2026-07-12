/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.HandlerList
 *  org.bukkit.plugin.RegisteredListener
 */
package com.bergerkiller.reflection.org.bukkit;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import java.util.ArrayList;
import java.util.EnumMap;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

public class BHandlerList {
    public static final ClassTemplate<HandlerList> T = ClassTemplate.create(HandlerList.class);
    public static final FieldAccessor<EnumMap<EventPriority, ArrayList<RegisteredListener>>> handlerslots = T.getField("handlerslots", EnumMap.class);
}

