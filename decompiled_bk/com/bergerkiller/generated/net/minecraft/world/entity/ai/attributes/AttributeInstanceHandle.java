/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.ai.attributes.AttributeInstance")
public abstract class AttributeInstanceHandle
extends Template.Handle {
    public static final AttributeInstanceClass T = Template.Class.create(AttributeInstanceClass.class, Common.TEMPLATE_RESOLVER);

    public static AttributeInstanceHandle createHandle(Object handleInstance) {
        return (AttributeInstanceHandle)T.createHandle(handleInstance);
    }

    public abstract Holder<AttributeHandle> getAttribute();

    public abstract void setBaseValue(double var1);

    public abstract double getBaseValue();

    public abstract double getValue();

    public abstract void removeAllModifiers();

    public static final class AttributeInstanceClass
    extends Template.Class<AttributeInstanceHandle> {
        public final Template.Method.Converted<Holder<AttributeHandle>> getAttribute = new Template.Method.Converted();
        public final Template.Method<Void> setBaseValue = new Template.Method();
        public final Template.Method<Double> getBaseValue = new Template.Method();
        public final Template.Method<Double> getValue = new Template.Method();
        public final Template.Method<Void> removeAllModifiers = new Template.Method();
    }
}

