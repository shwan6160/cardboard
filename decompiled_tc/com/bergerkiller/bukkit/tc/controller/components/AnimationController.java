/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.tc.attachments.animation.Animation;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationOptions;
import java.util.Collection;
import java.util.List;

public interface AnimationController {
    public List<String> getAnimationNames();

    public Collection<String> getAnimationScenes(String var1);

    public boolean playNamedAnimationFor(int[] var1, AnimationOptions var2);

    public boolean playAnimationFor(int[] var1, Animation var2);

    default public boolean playNamedAnimation(String name) {
        return this.playNamedAnimation(new AnimationOptions(name));
    }

    public boolean playNamedAnimation(AnimationOptions var1);
}

