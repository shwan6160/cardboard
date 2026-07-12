/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.generated.net.minecraft.world.entity.ai.attributes.AttributeInstanceHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;
import java.util.Set;

@Template.InstanceType(value="net.minecraft.world.entity.ai.attributes.AttributeMap")
public abstract class AttributeMapHandle
extends Template.Handle {
    public static final AttributeMapClass T = Template.Class.create(AttributeMapClass.class, Common.TEMPLATE_RESOLVER);

    public static AttributeMapHandle createHandle(Object handleInstance) {
        return (AttributeMapHandle)T.createHandle(handleInstance);
    }

    public abstract Collection<AttributeInstanceHandle> getAllAttributes();

    public abstract Set<AttributeInstanceHandle> getChangedSynchronizedAttributes();

    public abstract Collection<AttributeInstanceHandle> getSynchronizedAttributes();

    public abstract void loadFromNBT(CommonTagList var1);

    public abstract CommonTagList saveToNBT();

    public static final class AttributeMapClass
    extends Template.Class<AttributeMapHandle> {
        public final Template.Method.Converted<Collection<AttributeInstanceHandle>> getAllAttributes = new Template.Method.Converted();
        public final Template.Method.Converted<Set<AttributeInstanceHandle>> getChangedSynchronizedAttributes = new Template.Method.Converted();
        public final Template.Method.Converted<Collection<AttributeInstanceHandle>> getSynchronizedAttributes = new Template.Method.Converted();
        public final Template.Method.Converted<Void> loadFromNBT = new Template.Method.Converted();
        public final Template.Method.Converted<CommonTagList> saveToNBT = new Template.Method.Converted();
    }
}

