/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit.util;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.network.chat.ComponentHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="org.bukkit.craftbukkit.util.CraftChatMessage")
public abstract class CraftChatMessageHandle
extends Template.Handle {
    public static final CraftChatMessageClass T = Template.Class.create(CraftChatMessageClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftChatMessageHandle createHandle(Object handleInstance) {
        return (CraftChatMessageHandle)T.createHandle(handleInstance);
    }

    public static String fromComponent(ComponentHandle component) {
        return CraftChatMessageHandle.T.fromComponent.invoke(component);
    }

    public static ComponentHandle[] fromString(String message, boolean keepNewlines) {
        return CraftChatMessageHandle.T.fromString.invoke(message, keepNewlines);
    }

    public static final class CraftChatMessageClass
    extends Template.Class<CraftChatMessageHandle> {
        public final Template.StaticMethod.Converted<String> fromComponent = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ComponentHandle[]> fromString = new Template.StaticMethod.Converted();
    }
}

