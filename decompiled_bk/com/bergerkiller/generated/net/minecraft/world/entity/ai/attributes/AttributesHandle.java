/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.Holder;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.ai.attributes.Attributes")
public abstract class AttributesHandle
extends Template.Handle {
    public static final AttributesClass T = Template.Class.create(AttributesClass.class, Common.TEMPLATE_RESOLVER);
    public static final Holder<AttributeHandle> FOLLOW_RANGE = AttributesHandle.T.FOLLOW_RANGE.getSafe();
    public static final Holder<AttributeHandle> MOVEMENT_SPEED = AttributesHandle.T.MOVEMENT_SPEED.getSafe();

    public static AttributesHandle createHandle(Object handleInstance) {
        return (AttributesHandle)T.createHandle(handleInstance);
    }

    public static final class AttributesClass
    extends Template.Class<AttributesHandle> {
        public final Template.StaticField.Converted<Holder<AttributeHandle>> FOLLOW_RANGE = new Template.StaticField.Converted();
        public final Template.StaticField.Converted<Holder<AttributeHandle>> MOVEMENT_SPEED = new Template.StaticField.Converted();
    }
}

