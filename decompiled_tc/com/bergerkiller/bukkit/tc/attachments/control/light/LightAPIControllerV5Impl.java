/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.bases.IntVector3
 *  org.bukkit.World
 *  ru.beykerykt.minecraft.lightapi.common.LightAPI
 *  ru.beykerykt.minecraft.lightapi.common.api.engine.EditPolicy
 *  ru.beykerykt.minecraft.lightapi.common.api.engine.SendPolicy
 *  ru.beykerykt.minecraft.lightapi.common.api.engine.sched.ICallback
 */
package com.bergerkiller.bukkit.tc.attachments.control.light;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.tc.attachments.control.light.LightAPIController;
import org.bukkit.World;
import ru.beykerykt.minecraft.lightapi.common.LightAPI;
import ru.beykerykt.minecraft.lightapi.common.api.engine.EditPolicy;
import ru.beykerykt.minecraft.lightapi.common.api.engine.SendPolicy;
import ru.beykerykt.minecraft.lightapi.common.api.engine.sched.ICallback;

class LightAPIControllerV5Impl
extends LightAPIController {
    private final LightAPI api = LightAPI.get();
    private final ICallback callback = (requestFlag, resultCode) -> {};
    private final String worldName;
    private final int lightFlag;

    private LightAPIControllerV5Impl(String worldName, int lightFlag) {
        this.worldName = worldName;
        this.lightFlag = lightFlag;
    }

    public static LightAPIControllerV5Impl forBlockLight(World world) {
        return new LightAPIControllerV5Impl(world.getName(), 1);
    }

    public static LightAPIControllerV5Impl forSkyLight(World world) {
        return new LightAPIControllerV5Impl(world.getName(), 2);
    }

    private void set(IntVector3 position, int level, EditPolicy editPolicy) {
        SendPolicy sendPolicy = SendPolicy.DEFERRED;
        this.api.setLightLevel(this.worldName, position.x, position.y, position.z, level, this.lightFlag, editPolicy, sendPolicy, this.callback);
    }

    @Override
    public void add(IntVector3 position, int level) {
        this.set(position, level, EditPolicy.DEFERRED);
    }

    @Override
    public void remove(IntVector3 position, int level) {
        this.set(position, 0, EditPolicy.DEFERRED);
    }

    @Override
    public void move(IntVector3 old_position, IntVector3 new_position, int level) {
        this.set(new_position, level, EditPolicy.DEFERRED);
        this.set(old_position, 0, EditPolicy.DEFERRED);
    }

    @Override
    public void update(IntVector3 position, int old_level, int new_level) {
        this.set(position, new_level, EditPolicy.DEFERRED);
    }

    @Override
    protected boolean onSync() {
        return false;
    }
}

