/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.Difficulty")
public abstract class DifficultyHandle
extends Template.Handle {
    public static final DifficultyClass T = Template.Class.create(DifficultyClass.class, Common.TEMPLATE_RESOLVER);

    public static DifficultyHandle createHandle(Object handleInstance) {
        return (DifficultyHandle)T.createHandle(handleInstance);
    }

    public static DifficultyHandle getById(int id) {
        return DifficultyHandle.T.getById.invoke(id);
    }

    public abstract int getId();

    public static final class DifficultyClass
    extends Template.Class<DifficultyHandle> {
        public final Template.StaticMethod.Converted<DifficultyHandle> getById = new Template.StaticMethod.Converted();
        public final Template.Method<Integer> getId = new Template.Method();
    }
}

