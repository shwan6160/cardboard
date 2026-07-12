/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.reflection.org.bukkit.craftbukkit;

import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import org.bukkit.entity.Entity;

public class CBCraftEntity {
    public static final ClassTemplate<?> T = ClassTemplate.create("org.bukkit.craftbukkit.entity.CraftEntity");
    public static final MethodAccessor<Entity> getEntity = T.selectMethod("public static CraftEntity getEntity(org.bukkit.craftbukkit.CraftServer server,net.minecraft.world.entity.Entity entity)");
    public static final FieldAccessor<Object> entity = T.selectField("protected net.minecraft.world.entity.Entity entity");
}

