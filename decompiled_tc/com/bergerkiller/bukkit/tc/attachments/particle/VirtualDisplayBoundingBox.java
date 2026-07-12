/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualDisplayBoundingPlane;
import java.util.List;

public class VirtualDisplayBoundingBox
extends VirtualDisplayBoundingPlane {
    public VirtualDisplayBoundingBox(AttachmentManager manager) {
        super(manager);
    }

    @Override
    protected void loadParts(List<VirtualDisplayBoundingPlane.Part> parts) {
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(0.0, 1.0, 1.0).applyScaleX(1.0)));
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(0.0, 0.0, 1.0).applyScaleX(1.0)));
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(0.0, 1.0, 0.0).applyScaleX(1.0)));
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(0.0, 0.0, 0.0).applyScaleX(1.0)));
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(1.0, 1.0, 0.0).applyScaleZ(1.0)));
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(1.0, 0.0, 0.0).applyScaleZ(1.0)));
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(0.0, 1.0, 0.0).applyScaleZ(1.0)));
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(0.0, 0.0, 0.0).applyScaleZ(1.0)));
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(1.0, 0.0, 1.0).applyScaleY(1.0)));
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(1.0, 0.0, 0.0).applyScaleY(1.0)));
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(0.0, 0.0, 1.0).applyScaleY(1.0)));
        parts.add(VirtualDisplayBoundingPlane.Line.transform(t -> t.applyPosition(0.0, 0.0, 0.0).applyScaleY(1.0)));
    }
}

