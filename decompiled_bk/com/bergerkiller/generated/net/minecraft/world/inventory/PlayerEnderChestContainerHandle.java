/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.world.ContainerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.inventory.PlayerEnderChestContainer")
public abstract class PlayerEnderChestContainerHandle
extends ContainerHandle {
    public static final PlayerEnderChestContainerClass T = Template.Class.create(PlayerEnderChestContainerClass.class, Common.TEMPLATE_RESOLVER);

    public static PlayerEnderChestContainerHandle createHandle(Object handleInstance) {
        return (PlayerEnderChestContainerHandle)T.createHandle(handleInstance);
    }

    public abstract void loadFromNBT(CommonTagList var1);

    public abstract CommonTagList saveToNBT();

    public static final class PlayerEnderChestContainerClass
    extends Template.Class<PlayerEnderChestContainerHandle> {
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted();
        public final Template.Method.Converted<CommonTagList> saveToNBT = new Template.Method.Converted();
    }
}

