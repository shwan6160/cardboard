/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.resources;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.resources.ResourceKey")
public abstract class ResourceKeyHandle
extends Template.Handle {
    public static final ResourceKeyClass T = Template.Class.create(ResourceKeyClass.class, Common.TEMPLATE_RESOLVER);

    public static ResourceKeyHandle createHandle(Object handleInstance) {
        return (ResourceKeyHandle)T.createHandle(handleInstance);
    }

    public static ResourceKeyHandle create(ResourceKeyHandle category, IdentifierHandle name) {
        return ResourceKeyHandle.T.create.invoke(category, name);
    }

    public static ResourceKeyHandle createCategory(IdentifierHandle categoryName) {
        return ResourceKeyHandle.T.createCategory.invoke(categoryName);
    }

    public abstract IdentifierHandle getCategory();

    public abstract IdentifierHandle getName();

    public static final class ResourceKeyClass
    extends Template.Class<ResourceKeyHandle> {
        public final Template.StaticMethod.Converted<ResourceKeyHandle> create = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ResourceKeyHandle> createCategory = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<IdentifierHandle> getCategory = new Template.Method.Converted();
        public final Template.Method.Converted<IdentifierHandle> getName = new Template.Method.Converted();
    }
}

