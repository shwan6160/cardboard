/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.EquipmentSlot
 */
package com.bergerkiller.generated.org.bukkit.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

@Template.InstanceType(value="org.bukkit.entity.Entity")
public abstract class EntityHandle
extends Template.Handle {
    public static final EntityClass T = Template.Class.create(EntityClass.class, Common.TEMPLATE_RESOLVER);

    public static EntityHandle createHandle(Object handleInstance) {
        return (EntityHandle)T.createHandle(handleInstance);
    }

    public abstract List<Entity> getPassengers();

    public abstract boolean addPassenger(Entity var1);

    public abstract boolean removePassenger(Entity var1);

    public abstract boolean isSeenBy(Player var1);

    public abstract boolean isEquipmentSlotSupported(EquipmentSlot var1);

    public static final class EntityClass
    extends Template.Class<EntityHandle> {
        public final Template.Method<List<Entity>> getPassengers = new Template.Method();
        public final Template.Method<Boolean> addPassenger = new Template.Method();
        public final Template.Method<Boolean> removePassenger = new Template.Method();
        public final Template.Method<Boolean> isSeenBy = new Template.Method();
        public final Template.Method<Boolean> isEquipmentSlotSupported = new Template.Method();
    }
}

