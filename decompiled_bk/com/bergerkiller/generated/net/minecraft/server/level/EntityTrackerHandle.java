/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 *  org.bukkit.entity.Entity
 */
package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import org.bukkit.World;
import org.bukkit.entity.Entity;

@Template.InstanceType(value="net.minecraft.server.level.EntityTracker")
public abstract class EntityTrackerHandle
extends Template.Handle {
    public static final EntityTrackerClass T = Template.Class.create(EntityTrackerClass.class, Common.TEMPLATE_RESOLVER);

    public static EntityTrackerHandle createHandle(Object handleInstance) {
        return (EntityTrackerHandle)T.createHandle(handleInstance);
    }

    public abstract World getWorld();

    public abstract void setWorld(World var1);

    public abstract Collection<EntityTrackerEntryHandle> getEntries();

    public abstract EntityTrackerEntryHandle getEntry(int var1);

    public abstract EntityTrackerEntryHandle putEntry(int var1, EntityTrackerEntryHandle var2);

    public abstract void sendPacketToEntity(Entity var1, CommonPacket var2);

    public abstract void trackEntity(Entity var1);

    public abstract void untrackEntity(Entity var1);

    public abstract int getTrackingDistance();

    public abstract void setTrackingDistance(int var1);

    public static final class EntityTrackerClass
    extends Template.Class<EntityTrackerHandle> {
        public final Template.Field.Integer trackingDistance = new Template.Field.Integer();
        public final Template.Method.Converted<World> getWorld = new Template.Method.Converted();
        public final Template.Method.Converted<Void> setWorld = new Template.Method.Converted();
        public final Template.Method.Converted<Collection<EntityTrackerEntryHandle>> getEntries = new Template.Method.Converted();
        public final Template.Method.Converted<EntityTrackerEntryHandle> getEntry = new Template.Method.Converted();
        public final Template.Method.Converted<EntityTrackerEntryHandle> putEntry = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method<Void> setVisibleChunksToUpdatingChunks = new Template.Method();
        public final Template.Method.Converted<Void> sendPacketToEntity = new Template.Method.Converted();
        public final Template.Method.Converted<Void> trackEntity = new Template.Method.Converted();
        public final Template.Method.Converted<Void> untrackEntity = new Template.Method.Converted();
    }
}

