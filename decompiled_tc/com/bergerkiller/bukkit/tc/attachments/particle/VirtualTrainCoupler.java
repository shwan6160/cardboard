/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.internal.CommonCapabilities
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.attachments.VirtualSpawnableObject;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualDisplayTrainCoupler;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualFishingTrainCoupler;

public abstract class VirtualTrainCoupler
extends VirtualSpawnableObject {
    protected VirtualTrainCoupler(AttachmentManager manager) {
        super(manager);
    }

    public abstract void update(Matrix4x4 var1, double var2);

    public static VirtualTrainCoupler create(AttachmentManager manager) {
        if (CommonCapabilities.HAS_DISPLAY_ENTITY) {
            return new VirtualDisplayTrainCoupler(manager);
        }
        return new VirtualFishingTrainCoupler(manager);
    }
}

