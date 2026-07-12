/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.detector;

import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.detector.DetectorRegion;

public interface DetectorListener {
    public void onRegister(DetectorRegion var1);

    public void onUnregister(DetectorRegion var1);

    public void onLeave(MinecartMember<?> var1);

    public void onEnter(MinecartMember<?> var1);

    public void onLeave(MinecartGroup var1);

    public void onEnter(MinecartGroup var1);

    public void onUnload(MinecartGroup var1);

    public void onUpdate(MinecartMember<?> var1);

    public void onUpdate(MinecartGroup var1);
}

