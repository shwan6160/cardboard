/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import java.util.Map;

public class CBAsynchronousExecutor {
    public static final ClassTemplate<?> T = ClassTemplate.create("org.bukkit.craftbukkit.util.AsynchronousExecutor");
    public static final FieldAccessor<Map<?, ?>> tasks = T.selectField("final Map<P, Task> tasks");
}

