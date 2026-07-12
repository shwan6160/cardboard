/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.BeaconInventory
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.BeaconInventory;

@Template.InstanceType(value="org.bukkit.craftbukkit.inventory.CraftInventoryBeacon")
public abstract class CraftInventoryBeaconHandle
extends Template.Handle {
    public static final CraftInventoryBeaconClass T = Template.Class.create(CraftInventoryBeaconClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftInventoryBeaconHandle createHandle(Object handleInstance) {
        return (CraftInventoryBeaconHandle)T.createHandle(handleInstance);
    }

    public static BeaconInventory createNew(Object nmsTileEntityBeacon) {
        return CraftInventoryBeaconHandle.T.createNew.invoke(nmsTileEntityBeacon);
    }

    public static final class CraftInventoryBeaconClass
    extends Template.Class<CraftInventoryBeaconHandle> {
        public final Template.StaticMethod.Converted<BeaconInventory> createNew = new Template.StaticMethod.Converted();
    }
}

