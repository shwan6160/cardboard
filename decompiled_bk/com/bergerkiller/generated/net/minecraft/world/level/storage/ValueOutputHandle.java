/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.storage;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.storage.ValueOutput")
public abstract class ValueOutputHandle
extends Template.Handle {
    public static final ValueOutputClass T = Template.Class.create(ValueOutputClass.class, Common.TEMPLATE_RESOLVER);

    public static ValueOutputHandle createHandle(Object handleInstance) {
        return (ValueOutputHandle)T.createHandle(handleInstance);
    }

    public abstract void putString(String var1, String var2);

    public static final class ValueOutputClass
    extends Template.Class<ValueOutputHandle> {
        public final Template.Method<Void> putString = new Template.Method();
    }
}

