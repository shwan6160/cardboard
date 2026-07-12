/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.BlockFace
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.generated.net.minecraft.world.phys;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

@Template.InstanceType(value="net.minecraft.world.phys.HitResult")
public abstract class HitResultHandle
extends Template.Handle {
    public static final HitResultClass T = Template.Class.create(HitResultClass.class, Common.TEMPLATE_RESOLVER);

    public static HitResultHandle createHandle(Object handleInstance) {
        return (HitResultHandle)T.createHandle(handleInstance);
    }

    public abstract BlockFace getDirection();

    @Template.Readonly
    public abstract Vector getPos();

    public static final class HitResultClass
    extends Template.Class<HitResultHandle> {
        @Template.Readonly
        public final Template.Field.Converted<Vector> pos = new Template.Field.Converted();
        public final Template.Method.Converted<BlockFace> getDirection = new Template.Method.Converted();
    }
}

