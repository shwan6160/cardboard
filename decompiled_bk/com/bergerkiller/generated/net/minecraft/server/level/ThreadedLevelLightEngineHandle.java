/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;

@Template.Optional
@Template.InstanceType(value="net.minecraft.server.level.ThreadedLevelLightEngine")
public abstract class ThreadedLevelLightEngineHandle
extends Template.Handle {
    public static final ThreadedLevelLightEngineClass T = Template.Class.create(ThreadedLevelLightEngineClass.class, Common.TEMPLATE_RESOLVER);

    public static ThreadedLevelLightEngineHandle createHandle(Object handleInstance) {
        return (ThreadedLevelLightEngineHandle)T.createHandle(handleInstance);
    }

    public static ThreadedLevelLightEngineHandle forWorld(World world) {
        return ThreadedLevelLightEngineHandle.T.forWorld.invoke(world);
    }

    public abstract void schedule(Runnable var1);

    public static final class ThreadedLevelLightEngineClass
    extends Template.Class<ThreadedLevelLightEngineHandle> {
        public final Template.StaticMethod.Converted<ThreadedLevelLightEngineHandle> forWorld = new Template.StaticMethod.Converted();
        public final Template.Method<Void> schedule = new Template.Method();
    }
}

