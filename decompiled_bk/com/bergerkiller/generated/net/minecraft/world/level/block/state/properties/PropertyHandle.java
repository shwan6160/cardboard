/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.block.state.properties;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import java.util.Collection;

@Template.InstanceType(value="net.minecraft.world.level.block.state.properties.Property")
public abstract class PropertyHandle
extends Template.Handle {
    public static final PropertyClass T = Template.Class.create(PropertyClass.class, Common.TEMPLATE_RESOLVER);

    public static PropertyHandle createHandle(Object handleInstance) {
        return (PropertyHandle)T.createHandle(handleInstance);
    }

    public abstract String getKeyToken();

    public abstract String getValueToken(Comparable var1);

    public abstract Collection getValues();

    public static final class PropertyClass
    extends Template.Class<PropertyHandle> {
        public final Template.Method<String> getKeyToken = new Template.Method();
        public final Template.Method<String> getValueToken = new Template.Method();
        public final Template.Method<Collection> getValues = new Template.Method();
    }
}

