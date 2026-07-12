/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.BrewerInventory
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.BrewerInventory;

@Template.InstanceType(value="org.bukkit.craftbukkit.inventory.CraftInventoryBrewer")
public abstract class CraftInventoryBrewerHandle
extends Template.Handle {
    public static final CraftInventoryBrewerClass T = Template.Class.create(CraftInventoryBrewerClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftInventoryBrewerHandle createHandle(Object handleInstance) {
        return (CraftInventoryBrewerHandle)T.createHandle(handleInstance);
    }

    public static final BrewerInventory createNew(Object nmsTileEntityBrewingStand) {
        return CraftInventoryBrewerHandle.T.constr_nmsTileEntityBrewingStand.newInstance(nmsTileEntityBrewingStand);
    }

    public static final class CraftInventoryBrewerClass
    extends Template.Class<CraftInventoryBrewerHandle> {
        public final Template.Constructor.Converted<BrewerInventory> constr_nmsTileEntityBrewingStand = new Template.Constructor.Converted();
    }
}

