/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.resources;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.resources.ResourceCategory;
import com.bergerkiller.bukkit.common.resources.ResourceKey;

public final class SoundEffect {
    public static final ResourceKey<SoundEffect> EXTINGUISH;
    public static final ResourceKey<SoundEffect> WALK_CLOTH;
    public static final ResourceKey<SoundEffect> CLICK;
    public static final ResourceKey<SoundEffect> CLICK_WOOD;
    public static final ResourceKey<SoundEffect> PISTON_CONTRACT;
    public static final ResourceKey<SoundEffect> PISTON_EXTEND;
    public static final ResourceKey<SoundEffect> ITEM_BREAK;

    public static ResourceKey<SoundEffect> fromName(String name) {
        return ResourceCategory.sound_effect.createKey(name);
    }

    static {
        if (CommonCapabilities.KEYED_EFFECTS) {
            if (Common.evaluateMCVersion(">=", "1.13")) {
                CLICK_WOOD = SoundEffect.fromName("block.wooden_button.click_on");
                WALK_CLOTH = SoundEffect.fromName("block.wool.fall");
            } else {
                CLICK_WOOD = SoundEffect.fromName("block.wood_button.click_on");
                WALK_CLOTH = SoundEffect.fromName("block.cloth.fall");
            }
            EXTINGUISH = SoundEffect.fromName("block.fire.extinguish");
            CLICK = SoundEffect.fromName("ui.button.click");
            PISTON_CONTRACT = SoundEffect.fromName("block.piston.contract");
            PISTON_EXTEND = SoundEffect.fromName("block.piston.extend");
            ITEM_BREAK = SoundEffect.fromName("entity.item.break");
        } else {
            EXTINGUISH = SoundEffect.fromName("random.fizz");
            WALK_CLOTH = SoundEffect.fromName("step.cloth");
            CLICK = SoundEffect.fromName("random.click");
            CLICK_WOOD = SoundEffect.fromName("random.wood_click");
            PISTON_CONTRACT = SoundEffect.fromName("tile.piston.in");
            PISTON_EXTEND = SoundEffect.fromName("tile.piston.out");
            ITEM_BREAK = SoundEffect.fromName("random.break");
        }
    }
}

