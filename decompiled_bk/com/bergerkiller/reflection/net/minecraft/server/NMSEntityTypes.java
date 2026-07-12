/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.world.entity.EntityTypeHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;

@Deprecated
public class NMSEntityTypes {
    public static final ClassTemplate<?> T = ClassTemplate.create(EntityTypeHandle.T.getType());

    public static Class<?> getEntityClass(String entityName) {
        return EntityTypeHandle.getEntityClass(entityName);
    }

    public static String getName(Class<?> type) {
        return EntityTypeHandle.getEntityInternalName(type);
    }
}

