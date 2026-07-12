/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.hooks;

import com.bergerkiller.mountiplex.reflection.ClassHook;
import java.util.HashSet;

@ClassHook.HookPackage(value="net.minecraft.server")
public class EntityTrackerHook
extends ClassHook<EntityTrackerHook> {
    public final HashSet<Object> ignoredEntities = new HashSet();
    public final Object original;

    public EntityTrackerHook(Object original) {
        this.original = original;
    }

    @ClassHook.HookMethod(value="protected void trackEntity:???(net.minecraft.world.entity.Entity entity)")
    public void track(Object nmsEntityHandle) {
        if (!this.ignoredEntities.contains(nmsEntityHandle)) {
            ((EntityTrackerHook)this.base).track(nmsEntityHandle);
        }
    }

    public Object hookedInstance() {
        return super.instance();
    }
}

