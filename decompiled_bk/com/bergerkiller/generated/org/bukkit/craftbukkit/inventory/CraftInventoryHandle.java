/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.ContainerHandle;
import com.bergerkiller.generated.org.bukkit.inventory.InventoryHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.Inventory;

@Template.InstanceType(value="org.bukkit.craftbukkit.inventory.CraftInventory")
public abstract class CraftInventoryHandle
extends InventoryHandle {
    public static final CraftInventoryClass T = Template.Class.create(CraftInventoryClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftInventoryHandle createHandle(Object handleInstance) {
        return (CraftInventoryHandle)T.createHandle(handleInstance);
    }

    public static final Inventory createNew(Object nmsContainer) {
        return CraftInventoryHandle.T.constr_nmsContainer.newInstance(nmsContainer);
    }

    public abstract ContainerHandle getHandle();

    public abstract ContainerHandle getHandleField();

    public abstract void setHandleField(ContainerHandle var1);

    public static final class CraftInventoryClass
    extends Template.Class<CraftInventoryHandle> {
        public final Template.Constructor.Converted<Inventory> constr_nmsContainer = new Template.Constructor.Converted();
        public final Template.Field.Converted<ContainerHandle> handleField = new Template.Field.Converted();
        public final Template.Method.Converted<ContainerHandle> getHandle = new Template.Method.Converted();
    }
}

