/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.ChunkPos")
public abstract class ChunkPosHandle
extends Template.Handle {
    public static final ChunkPosClass T = Template.Class.create(ChunkPosClass.class, Common.TEMPLATE_RESOLVER);

    public static ChunkPosHandle createHandle(Object handleInstance) {
        return (ChunkPosHandle)T.createHandle(handleInstance);
    }

    public static final ChunkPosHandle createNew(int x, int z) {
        return ChunkPosHandle.T.constr_x_z.newInstance(x, z);
    }

    public static Object fromIntVector2Raw(IntVector2 vector) {
        return ChunkPosHandle.T.fromIntVector2Raw.invoker.invoke(null, vector);
    }

    public abstract int x();

    public abstract int z();

    public abstract IntVector2 toIntVector2();

    public static ChunkPosHandle fromIntVector2(IntVector2 vector) {
        return ChunkPosHandle.createHandle(ChunkPosHandle.fromIntVector2Raw(vector));
    }

    public static final class ChunkPosClass
    extends Template.Class<ChunkPosHandle> {
        public final Template.Constructor.Converted<ChunkPosHandle> constr_x_z = new Template.Constructor.Converted();
        public final Template.StaticMethod<Object> fromIntVector2Raw = new Template.StaticMethod();
        public final Template.Method<Integer> x = new Template.Method();
        public final Template.Method<Integer> z = new Template.Method();
        public final Template.Method<IntVector2> toIntVector2 = new Template.Method();
    }
}

