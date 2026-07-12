/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.org.bukkit.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="org.bukkit.inventory.Inventory")
public abstract class InventoryHandle
extends Template.Handle {
    public static final InventoryClass T = Template.Class.create(InventoryClass.class, Common.TEMPLATE_RESOLVER);

    public static InventoryHandle createHandle(Object handleInstance) {
        return (InventoryHandle)T.createHandle(handleInstance);
    }

    public static final class InventoryClass
    extends Template.Class<InventoryHandle> {
        @Template.Optional
        public final Template.Method<Location> getLocation = new Template.Method();
        @Template.Optional
        public final Template.Method<ItemStack[]> getStorageContents = new Template.Method();
        @Template.Optional
        public final Template.Method<Void> setStorageContents = new Template.Method();
        @Template.Optional
        public final Template.Method<String> getName = new Template.Method();
        @Template.Optional
        public final Template.Method<String> getTitle = new Template.Method();
        @Template.Optional
        public final Template.Method<HashMap<Integer, ItemStack>> removeItemAnySlot = new Template.Method();
    }
}

