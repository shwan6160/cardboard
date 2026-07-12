/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.generated.net.minecraft.world.entity.vehicle.minecart.AbstractMinecartHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.entity.vehicle.minecart.MinecartCommandBlock")
public abstract class MinecartCommandBlockHandle
extends AbstractMinecartHandle {
    public static final MinecartCommandBlockClass T = Template.Class.create(MinecartCommandBlockClass.class, Common.TEMPLATE_RESOLVER);
    public static final DataWatcher.Key<String> DATA_COMMAND = DataWatcher.Key.Type.STRING.createKey(MinecartCommandBlockHandle.T.DATA_COMMAND, 23);
    public static final DataWatcher.Key<ChatText> DATA_PREVIOUS_OUTPUT = DataWatcher.Key.Type.CHAT_TEXT.createKey(MinecartCommandBlockHandle.T.DATA_PREVIOUS_OUTPUT, 24);

    public static MinecartCommandBlockHandle createHandle(Object handleInstance) {
        return (MinecartCommandBlockHandle)T.createHandle(handleInstance);
    }

    public static final class MinecartCommandBlockClass
    extends Template.Class<MinecartCommandBlockHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<String>> DATA_COMMAND = new Template.StaticField.Converted();
        @Template.Optional
        public final Template.StaticField.Converted<DataWatcher.Key<Object>> DATA_PREVIOUS_OUTPUT = new Template.StaticField.Converted();
    }
}

