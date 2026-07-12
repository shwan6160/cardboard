/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.internal.resources;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ResourceOverrides {
    private static final Set<String> _overrided = new HashSet<String>();

    public static boolean isResourceOverrided(String path) {
        return _overrided.contains(path);
    }

    static {
        Stream.of("black", "blue", "brown", "cyan", "gray", "green", "light_blue", "lime", "magenta", "orange", "pink", "purple", "red", "silver", "white", "yellow", "light_gray").flatMap(color -> Stream.of("assets/minecraft/models/item/" + color + "_shulker_box.json", "assets/minecraft/models/block/" + color + "_shulker_box.json")).forEach(_overrided::add);
        Stream.of("acacia", "birch", "dark_oak", "jungle", "legacy", "oak", "spruce").flatMap(type -> Stream.of(type + "_wall_sign", type + "_sign")).flatMap(type -> Stream.of("assets/minecraft/models/block/" + type + ".json", "assets/minecraft/blockstates/" + type + ".json")).forEach(_overrided::add);
        Stream.of("chest", "ender_chest", "trapped_chest", "christmas_chest", "shulker_box").flatMap(name -> Stream.of("assets/minecraft/models/block/" + name + ".json", "assets/minecraft/models/item/" + name + ".json", "assets/minecraft/blockstates/" + name + ".json")).forEach(_overrided::add);
        Stream.of("player_head", "skeleton_skull", "wither_skeleton_skull", "creeper_head", "zombie_head").flatMap(name -> {
            int idx = name.lastIndexOf(95);
            return Stream.of(name, name.substring(0, idx) + "_wall" + name.substring(idx));
        }).flatMap(name -> Stream.of("assets/minecraft/models/block/" + name + ".json", "assets/minecraft/models/item/" + name + ".json", "assets/minecraft/blockstates/" + name + ".json")).forEach(_overrided::add);
    }
}

