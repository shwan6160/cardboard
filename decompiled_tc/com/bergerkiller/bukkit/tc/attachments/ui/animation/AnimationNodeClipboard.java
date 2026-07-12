/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.StreamUtil
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.attachments.ui.animation;

import com.bergerkiller.bukkit.common.utils.StreamUtil;
import com.bergerkiller.bukkit.tc.attachments.animation.AnimationNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;

public class AnimationNodeClipboard {
    private static final HashMap<UUID, AnimationNodeClipboard> byPlayerUUID = new HashMap();
    private List<AnimationNode> nodes = Collections.emptyList();

    public static AnimationNodeClipboard of(Player player) {
        return byPlayerUUID.computeIfAbsent(player.getUniqueId(), u -> new AnimationNodeClipboard());
    }

    public static boolean hasClipboard(Player player) {
        AnimationNodeClipboard clipboard = byPlayerUUID.get(player.getUniqueId());
        return clipboard != null && !clipboard.nodes.isEmpty();
    }

    private AnimationNodeClipboard() {
    }

    public List<AnimationNode> contents() {
        return this.nodes;
    }

    public void store(List<AnimationNode> nodes) {
        this.nodes = nodes.isEmpty() ? Collections.emptyList() : (List)nodes.stream().map(AnimationNode::clone).collect(StreamUtil.toUnmodifiableList());
    }
}

