/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.EventExecutor
 *  org.bukkit.plugin.RegisteredListener
 */
package com.bergerkiller.reflection.org.bukkit;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;

public class BRegisteredListener {
    public static final ClassTemplate<RegisteredListener> T = ClassTemplate.create(RegisteredListener.class);
    public static final FieldAccessor<EventExecutor> executor = T.selectField("private final EventExecutor executor");
}

