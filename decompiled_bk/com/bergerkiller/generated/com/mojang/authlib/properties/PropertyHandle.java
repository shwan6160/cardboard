/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.com.mojang.authlib.properties;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="com.mojang.authlib.properties.Property")
public abstract class PropertyHandle
extends Template.Handle {
    public static final PropertyClass T = Template.Class.create(PropertyClass.class, Common.TEMPLATE_RESOLVER);

    public static PropertyHandle createHandle(Object handleInstance) {
        return (PropertyHandle)T.createHandle(handleInstance);
    }

    public static final PropertyHandle createNew(String name, String value) {
        return PropertyHandle.T.constr_name_value.newInstance(name, value);
    }

    public static final PropertyHandle createNew(String name, String value, String signature) {
        return PropertyHandle.T.constr_name_value_signature.newInstance(name, value, signature);
    }

    public abstract String getName();

    public abstract String getValue();

    public abstract String getSignature();

    public static final class PropertyClass
    extends Template.Class<PropertyHandle> {
        public final Template.Constructor.Converted<PropertyHandle> constr_name_value = new Template.Constructor.Converted();
        public final Template.Constructor.Converted<PropertyHandle> constr_name_value_signature = new Template.Constructor.Converted();
        public final Template.Method<String> getName = new Template.Method();
        public final Template.Method<String> getValue = new Template.Method();
        public final Template.Method<String> getSignature = new Template.Method();
    }
}

