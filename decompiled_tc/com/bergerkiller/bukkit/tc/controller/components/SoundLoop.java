/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.entity.type.CommonMinecart
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.WorldUtil
 *  org.bukkit.Location
 *  org.bukkit.Sound
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.entity.type.CommonMinecart;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Sound;

public class SoundLoop<T extends MinecartMember<?>> {
    protected final T member;
    protected final Random random = new Random();

    public SoundLoop(T member) {
        this.member = member;
    }

    public void play(Sound sound, float pitch, float volume) {
        ((CommonMinecart)this.member.getEntity()).makeSound(sound, volume, pitch);
    }

    public void play(ResourceKey<SoundEffect> sound, float pitch, float volume) {
        WorldUtil.playSound((Location)((CommonMinecart)this.member.getEntity()).getLocation(), sound, (float)volume, (float)pitch);
    }

    public void onTick() {
    }
}

