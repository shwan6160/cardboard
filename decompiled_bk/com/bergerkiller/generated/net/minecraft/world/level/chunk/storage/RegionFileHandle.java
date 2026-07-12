/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.generated.net.minecraft.world.level.chunk.storage;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

@Template.InstanceType(value="net.minecraft.world.level.chunk.storage.RegionFile")
public abstract class RegionFileHandle
extends Template.Handle {
    public static final RegionFileClass T = Template.Class.create(RegionFileClass.class, Common.TEMPLATE_RESOLVER);

    public static RegionFileHandle createHandle(Object handleInstance) {
        return (RegionFileHandle)T.createHandle(handleInstance);
    }

    public abstract void closeStream();

    public abstract boolean chunkExists(int var1, int var2);

    public static final class RegionFileClass
    extends Template.Class<RegionFileHandle> {
        public final Template.Method<Void> closeStream = new Template.Method();
        public final Template.Method<Boolean> chunkExists = new Template.Method();
    }
}

