/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 */
package com.bergerkiller.generated.org.bukkit;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.generated.net.minecraft.resources.IdentifierHandle;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Location;

@Template.InstanceType(value="org.bukkit.World")
public abstract class WorldHandle
extends Template.Handle {
    public static final WorldClass T = Template.Class.create(WorldClass.class, Common.TEMPLATE_RESOLVER);

    public static WorldHandle createHandle(Object handleInstance) {
        return (WorldHandle)T.createHandle(handleInstance);
    }

    public abstract boolean hasFeatureFlag(String var1);

    public abstract void playSound(Location var1, IdentifierHandle var2, float var3, float var4);

    public abstract int getMinHeight();

    public abstract void setClearWeatherDuration(int var1);

    public static final class WorldClass
    extends Template.Class<WorldHandle> {
        public final Template.Method<Boolean> hasFeatureFlag = new Template.Method();
        public final Template.Method<Void> playSound = new Template.Method();
        public final Template.Method<Integer> getMinHeight = new Template.Method();
        public final Template.Method<Void> setClearWeatherDuration = new Template.Method();
    }
}

