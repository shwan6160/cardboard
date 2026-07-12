/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.resources;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.resources.Identifier")
public abstract class IdentifierHandle
extends Template.Handle {
    public static final IdentifierClass T = Template.Class.create(IdentifierClass.class, Common.TEMPLATE_RESOLVER);
    public static final String DEFAULT_NAMESPACE = "minecraft";

    public static IdentifierHandle createHandle(Object handleInstance) {
        return (IdentifierHandle)T.createHandle(handleInstance);
    }

    public static IdentifierHandle createNew(String keyToken) {
        return IdentifierHandle.T.createNew.invoke(keyToken);
    }

    public abstract String getName();

    public abstract String getNamespace();

    public abstract Object toBukkit();

    public boolean isDefaultNamespace() {
        return DEFAULT_NAMESPACE.equals(this.getNamespace());
    }

    public String toShortString() {
        return this.isDefaultNamespace() ? this.getName() : this.toString();
    }

    public static boolean isValid(String key) {
        return IdentifierHandle.createNew(key) != null;
    }

    public static boolean isValidNamespace(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return true;
        }
        for (int cidx = 0; cidx < namespace.length(); ++cidx) {
            char i = namespace.charAt(cidx);
            if (i == '_' || i == '-' || i >= 'a' && i <= 'z' || i >= '0' && i <= '9' || i == '.') continue;
            return false;
        }
        return true;
    }

    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        for (int cidx = 0; cidx < name.length(); ++cidx) {
            char i = name.charAt(cidx);
            if (i == '_' || i == '-' || i >= 'a' && i <= 'z' || i >= '0' && i <= '9' || i == '/' || i == '.') continue;
            return false;
        }
        return true;
    }

    public static IdentifierHandle createNew(String namespace, String name) {
        return IdentifierHandle.T.createNew2.invoke(namespace, name);
    }

    public static final class IdentifierClass
    extends Template.Class<IdentifierHandle> {
        @Template.Optional
        public final Template.Constructor<Object> constr_code_parts = new Template.Constructor();
        public final Template.StaticMethod.Converted<IdentifierHandle> createNew = new Template.StaticMethod.Converted();
        @Template.Optional
        public final Template.StaticMethod.Converted<IdentifierHandle> createNew2 = new Template.StaticMethod.Converted();
        public final Template.Method<String> getName = new Template.Method();
        public final Template.Method<String> getNamespace = new Template.Method();
        public final Template.Method<Object> toBukkit = new Template.Method();
    }
}

