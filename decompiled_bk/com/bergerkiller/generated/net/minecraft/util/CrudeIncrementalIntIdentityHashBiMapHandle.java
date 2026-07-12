/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.util;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap")
public abstract class CrudeIncrementalIntIdentityHashBiMapHandle
extends Template.Handle {
    public static final CrudeIncrementalIntIdentityHashBiMapClass T = Template.Class.create(CrudeIncrementalIntIdentityHashBiMapClass.class, Common.TEMPLATE_RESOLVER);

    public static CrudeIncrementalIntIdentityHashBiMapHandle createHandle(Object handleInstance) {
        return (CrudeIncrementalIntIdentityHashBiMapHandle)T.createHandle(handleInstance);
    }

    public abstract int getId(Object var1);

    public static final class CrudeIncrementalIntIdentityHashBiMapClass
    extends Template.Class<CrudeIncrementalIntIdentityHashBiMapHandle> {
        public final Template.Method<Integer> getId = new Template.Method();
    }
}

