/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.GameType")
public abstract class GameTypeHandle
extends Template.Handle {
    public static final GameTypeClass T = Template.Class.create(GameTypeClass.class, Common.TEMPLATE_RESOLVER);

    public static GameTypeHandle createHandle(Object handleInstance) {
        return (GameTypeHandle)T.createHandle(handleInstance);
    }

    public static GameTypeHandle getById(int id) {
        return GameTypeHandle.T.getById.invoke(id);
    }

    public abstract int getId();

    public static final class GameTypeClass
    extends Template.Class<GameTypeHandle> {
        public final Template.StaticMethod.Converted<GameTypeHandle> getById = new Template.StaticMethod.Converted();
        public final Template.Method<Integer> getId = new Template.Method();
    }
}

