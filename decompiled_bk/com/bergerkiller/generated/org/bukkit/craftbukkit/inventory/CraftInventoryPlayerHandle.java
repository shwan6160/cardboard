/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.PlayerInventory
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.PlayerInventory;

@Template.InstanceType(value="org.bukkit.craftbukkit.inventory.CraftInventoryPlayer")
public abstract class CraftInventoryPlayerHandle
extends Template.Handle {
    public static final CraftInventoryPlayerClass T = Template.Class.create(CraftInventoryPlayerClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftInventoryPlayerHandle createHandle(Object handleInstance) {
        return (CraftInventoryPlayerHandle)T.createHandle(handleInstance);
    }

    public static final PlayerInventory createNew(Object nmsPlayerInventory) {
        return CraftInventoryPlayerHandle.T.constr_nmsPlayerInventory.newInstance(nmsPlayerInventory);
    }

    public static final class CraftInventoryPlayerClass
    extends Template.Class<CraftInventoryPlayerHandle> {
        public final Template.Constructor.Converted<PlayerInventory> constr_nmsPlayerInventory = new Template.Constructor.Converted();
    }
}

