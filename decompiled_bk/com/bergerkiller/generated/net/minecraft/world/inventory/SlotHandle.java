/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.generated.net.minecraft.world.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.inventory.ItemStack;

@Template.InstanceType(value="net.minecraft.world.inventory.Slot")
public abstract class SlotHandle
extends Template.Handle {
    public static final SlotClass T = Template.Class.create(SlotClass.class, Common.TEMPLATE_RESOLVER);

    public static SlotHandle createHandle(Object handleInstance) {
        return (SlotHandle)T.createHandle(handleInstance);
    }

    public abstract ItemStack getItem();

    public static final class SlotClass
    extends Template.Class<SlotHandle> {
        public final Template.Method.Converted<ItemStack> getItem = new Template.Method.Converted();
    }
}

