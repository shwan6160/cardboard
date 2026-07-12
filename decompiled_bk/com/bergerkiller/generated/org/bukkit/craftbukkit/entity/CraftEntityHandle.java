/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Server
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Server;
import org.bukkit.entity.Entity;

@Template.InstanceType(value="org.bukkit.craftbukkit.entity.CraftEntity")
public abstract class CraftEntityHandle
extends Template.Handle {
    public static final CraftEntityClass T = Template.Class.create(CraftEntityClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftEntityHandle createHandle(Object handleInstance) {
        return (CraftEntityHandle)T.createHandle(handleInstance);
    }

    public static Entity createCraftEntity(Server server, EntityHandle entity) {
        return CraftEntityHandle.T.createCraftEntity.invoke(server, entity);
    }

    public abstract void setHandle(EntityHandle var1);

    public abstract Object getHandle();

    public abstract EntityHandle getEntityHandle();

    public abstract void setEntityHandle(EntityHandle var1);

    public static final class CraftEntityClass
    extends Template.Class<CraftEntityHandle> {
        public final Template.Field.Converted<EntityHandle> entityHandle = new Template.Field.Converted();
        public final Template.StaticMethod.Converted<Entity> createCraftEntity = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<Void> setHandle = new Template.Method.Converted();
        public final Template.Method<Object> getHandle = new Template.Method();
    }
}

