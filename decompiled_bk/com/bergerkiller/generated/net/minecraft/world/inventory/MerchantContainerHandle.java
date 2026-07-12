/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.inventory;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.world.ContainerHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.inventory.MerchantContainer")
public abstract class MerchantContainerHandle
extends ContainerHandle {
    public static final MerchantContainerClass T = Template.Class.create(MerchantContainerClass.class, Common.TEMPLATE_RESOLVER);

    public static MerchantContainerHandle createHandle(Object handleInstance) {
        return (MerchantContainerHandle)T.createHandle(handleInstance);
    }

    public abstract Object getMerchant();

    public abstract void setMerchant(Object var1);

    public static final class MerchantContainerClass
    extends Template.Class<MerchantContainerHandle> {
        public final Template.Field.Converted<Object> merchant = new Template.Field.Converted();
    }
}

