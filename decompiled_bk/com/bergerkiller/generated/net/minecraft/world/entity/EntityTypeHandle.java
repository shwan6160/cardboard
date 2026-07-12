/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.core.MappedRegistryHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.List;
import java.util.Map;

@Template.InstanceType(value="net.minecraft.world.entity.EntityType")
public abstract class EntityTypeHandle
extends Template.Handle {
    public static final EntityTypeClass T = Template.Class.create(EntityTypeClass.class, Common.TEMPLATE_RESOLVER);

    public static EntityTypeHandle createHandle(Object handleInstance) {
        return (EntityTypeHandle)T.createHandle(handleInstance);
    }

    public static Class<?> getEntityClass(String internalEntityName) {
        return (Class)EntityTypeHandle.T.getEntityClass.invoker.invoke(null, internalEntityName);
    }

    public static String getEntityInternalName(Class<?> entityType) {
        return (String)EntityTypeHandle.T.getEntityInternalName.invoker.invoke(null, entityType);
    }

    public static int getEntityTypeId(Class<?> entityType) {
        return (Integer)EntityTypeHandle.T.getEntityTypeId.invoker.invoke(null, entityType);
    }

    public static final class EntityTypeClass
    extends Template.Class<EntityTypeHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Map<String, Class<?>>> opt_nameTypeMap_1_10_2 = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<Map<Class<?>, String>> opt_typeNameMap_1_10_2 = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField<List<String>> opt_typeIdToName_1_11 = new Template.StaticField();
        @Template.Optional
        public final Template.StaticField.Converted<Map<Class<?>, Integer>> opt_typeIdMap_1_8 = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticMethod.Converted<MappedRegistryHandle> opt_getRegistry = new Template.StaticMethod.Converted();
        @Template.Optional
        public final Template.StaticMethod.Converted<EntityTypeHandle> fromEntityClass = new Template.StaticMethod.Converted();
        public final Template.StaticMethod<Class<?>> getEntityClass = new Template.StaticMethod();
        public final Template.StaticMethod<String> getEntityInternalName = new Template.StaticMethod();
        public final Template.StaticMethod<Integer> getEntityTypeId = new Template.StaticMethod();
        @Template.Optional
        public final Template.Method.Converted<Class<?>> getEntityClassInst = new Template.Method.Converted();
        @Template.Optional
        public final Template.Method<Integer> getTypeId = new Template.Method();
    }
}

