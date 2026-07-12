/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.World
 */
package com.bergerkiller.generated.net.minecraft.world.level.storage;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.World;

@Template.InstanceType(value="net.minecraft.world.level.storage.ValueInput")
public abstract class ValueInputHandle
extends Template.Handle {
    public static final ValueInputClass T = Template.Class.create(ValueInputClass.class, Common.TEMPLATE_RESOLVER);

    public static ValueInputHandle createHandle(Object handleInstance) {
        return (ValueInputHandle)T.createHandle(handleInstance);
    }

    public static ValueInputHandle forNBTOnWorld(Object problemreporter, World world, CommonTagCompound nbttagcompound) {
        return ValueInputHandle.T.forNBTOnWorld.invoke(problemreporter, world, nbttagcompound);
    }

    public static ValueInputHandle forNBT(Object problemreporter, Object holderLookup, CommonTagCompound nbttagcompound) {
        return ValueInputHandle.T.forNBT.invoke(problemreporter, holderLookup, nbttagcompound);
    }

    public abstract CommonTagCompound asNBT();

    public static final class ValueInputClass
    extends Template.Class<ValueInputHandle> {
        public final Template.StaticMethod.Converted<ValueInputHandle> forNBTOnWorld = new Template.StaticMethod.Converted();
        public final Template.StaticMethod.Converted<ValueInputHandle> forNBT = new Template.StaticMethod.Converted();
        public final Template.Method.Converted<CommonTagCompound> asNBT = new Template.Method.Converted();
    }
}

