/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.ai.attributes.Attribute")
public abstract class AttributeHandle
extends Template.Handle {
    public static final AttributeClass T = Template.Class.create(AttributeClass.class, Common.TEMPLATE_RESOLVER);

    public static AttributeHandle createHandle(Object handleInstance) {
        return (AttributeHandle)T.createHandle(handleInstance);
    }

    public abstract String getDescriptionId();

    public static final class AttributeClass
    extends Template.Class<AttributeHandle> {
        public final Template.Method<String> getDescriptionId = new Template.Method();
    }
}

