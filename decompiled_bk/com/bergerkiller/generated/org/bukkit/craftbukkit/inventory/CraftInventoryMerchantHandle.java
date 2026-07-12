/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.MerchantInventory
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.MerchantInventory;

@Template.InstanceType(value="org.bukkit.craftbukkit.inventory.CraftInventoryMerchant")
public abstract class CraftInventoryMerchantHandle
extends Template.Handle {
    public static final CraftInventoryMerchantClass T = Template.Class.create(CraftInventoryMerchantClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftInventoryMerchantHandle createHandle(Object handleInstance) {
        return (CraftInventoryMerchantHandle)T.createHandle(handleInstance);
    }

    public static MerchantInventory createNew(Object nmsInventoryMerchant) {
        return CraftInventoryMerchantHandle.T.createNew.invoke(nmsInventoryMerchant);
    }

    public static final class CraftInventoryMerchantClass
    extends Template.Class<CraftInventoryMerchantHandle> {
        public final Template.StaticMethod.Converted<MerchantInventory> createNew = new Template.StaticMethod.Converted();
    }
}

