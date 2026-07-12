/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Art
 */
package com.bergerkiller.generated.org.bukkit.craftbukkit;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.Art;

@Template.InstanceType(value="org.bukkit.craftbukkit.CraftArt")
public abstract class CraftArtHandle
extends Template.Handle {
    public static final CraftArtClass T = Template.Class.create(CraftArtClass.class, Common.TEMPLATE_RESOLVER);

    public static CraftArtHandle createHandle(Object handleInstance) {
        return (CraftArtHandle)T.createHandle(handleInstance);
    }

    public static Art NotchToBukkit(Object art) {
        return CraftArtHandle.T.NotchToBukkit.invoke(art);
    }

    public static Object BukkitToNotch(Art art) {
        return CraftArtHandle.T.BukkitToNotch.invoker.invoke(null, art);
    }

    public static Object NotchFromInternalId(int internalId) {
        return CraftArtHandle.T.NotchFromInternalId.invoker.invoke(null, internalId);
    }

    public static int NotchToInternalId(Object art) {
        return CraftArtHandle.T.NotchToInternalId.invoke(art);
    }

    public static Object NotchFromInternalName(String internalName) {
        return CraftArtHandle.T.NotchFromInternalName.invoker.invoke(null, internalName);
    }

    public static String NotchToInternalName(Object art) {
        return CraftArtHandle.T.NotchToInternalName.invoke(art);
    }

    public static final class CraftArtClass
    extends Template.Class<CraftArtHandle> {
        public final Template.StaticMethod.Converted<Art> NotchToBukkit = new Template.StaticMethod.Converted();
        public final Template.StaticMethod<Object> BukkitToNotch = new Template.StaticMethod();
        public final Template.StaticMethod<Object> NotchFromInternalId = new Template.StaticMethod();
        public final Template.StaticMethod.Converted<Integer> NotchToInternalId = new Template.StaticMethod.Converted();
        public final Template.StaticMethod<Object> NotchFromInternalName = new Template.StaticMethod();
        public final Template.StaticMethod.Converted<String> NotchToInternalName = new Template.StaticMethod.Converted();
    }
}

