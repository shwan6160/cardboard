/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.FurnaceInventory
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.FurnaceInventory;

@Template.InstanceType(value="org.bukkit.craftbukkit.inventory.CraftInventoryFurnace")
public abstract class CraftInventoryFurnaceHandle
extends Template.Handle {
    public static final CraftInventoryFurnaceClass T = Template.Class.create(CraftInventoryFurnaceClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftInventoryFurnaceHandle createHandle(Object handleInstance) {
        return (CraftInventoryFurnaceHandle)T.createHandle(handleInstance);
    }

    public static final FurnaceInventory createNew(Object nmsTileEntityFurnace) {
        return CraftInventoryFurnaceHandle.T.constr_nmsTileEntityFurnace.newInstance(nmsTileEntityFurnace);
    }

    public static final class CraftInventoryFurnaceClass
    extends Template.Class<CraftInventoryFurnaceHandle> {
        public final Template.Constructor.Converted<FurnaceInventory> constr_nmsTileEntityFurnace = new Template.Constructor.Converted();
    }
}

