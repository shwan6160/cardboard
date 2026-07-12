/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.player;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.world.ContainerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.player.Inventory")
public abstract class InventoryHandle
extends ContainerHandle {
    public static final InventoryClass T = Template.Class.create(InventoryClass.class, Common.TEMPLATE_RESOLVER);

    public static InventoryHandle createHandle(Object handleInstance) {
        return (InventoryHandle)T.createHandle(handleInstance);
    }

    public static int getHotbarSize() {
        return (Integer)InventoryHandle.T.getHotbarSize.invoker.invoke(null);
    }

    public abstract CommonTagList saveToNBT(CommonTagList var1);

    public abstract void loadFromNBT(CommonTagList var1);

    public static final class InventoryClass
    extends Template.Class<InventoryHandle> {
        public final Template.StaticMethod<Integer> getHotbarSize = new Template.StaticMethod();
        public final Template.Method.Converted<CommonTagList> saveToNBT = new Template.Method.Converted();
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted();
    }
}

