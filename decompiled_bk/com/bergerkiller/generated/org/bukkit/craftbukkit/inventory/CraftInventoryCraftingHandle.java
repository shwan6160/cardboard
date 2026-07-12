/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.CraftingInventory
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.CraftingInventory;

@Template.InstanceType(value="org.bukkit.craftbukkit.inventory.CraftInventoryCrafting")
public abstract class CraftInventoryCraftingHandle
extends Template.Handle {
    public static final CraftInventoryCraftingClass T = Template.Class.create(CraftInventoryCraftingClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftInventoryCraftingHandle createHandle(Object handleInstance) {
        return (CraftInventoryCraftingHandle)T.createHandle(handleInstance);
    }

    public static final CraftingInventory createNew(Object nmsInventoryCrafting, Object nmsResultContainer) {
        return CraftInventoryCraftingHandle.T.constr_nmsInventoryCrafting_nmsResultContainer.newInstance(nmsInventoryCrafting, nmsResultContainer);
    }

    public static final class CraftInventoryCraftingClass
    extends Template.Class<CraftInventoryCraftingHandle> {
        public final Template.Constructor.Converted<CraftingInventory> constr_nmsInventoryCrafting_nmsResultContainer = new Template.Constructor.Converted();
    }
}

