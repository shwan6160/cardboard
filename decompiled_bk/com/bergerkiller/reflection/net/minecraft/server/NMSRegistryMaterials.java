/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.reflection.net.minecraft.server;

import com.bergerkiller.generated.net.minecraft.core.MappedRegistryHandle;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.MethodAccessor;
import java.util.Set;

public class NMSRegistryMaterials {
    public static final ClassTemplate<?> T = ClassTemplate.create(MappedRegistryHandle.T.getType());
    public static final MethodAccessor<Set<?>> keySet = T.selectMethod("public Set<K> keySet()");
    public static final MethodAccessor<Object> getValue = T.selectMethod("public V get(K paramK)");
}

