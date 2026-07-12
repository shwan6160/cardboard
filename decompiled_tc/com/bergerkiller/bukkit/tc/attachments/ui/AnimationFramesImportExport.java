/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.tc.attachments.animation.AnimationNode;
import java.util.List;

public interface AnimationFramesImportExport {
    public String getAnimationName();

    public List<AnimationNode> exportAnimationFrames();

    public void importAnimationFrames(List<AnimationNode> var1, boolean var2);
}

