/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.particle;

import com.bergerkiller.bukkit.tc.attachments.api.AttachmentManager;
import com.bergerkiller.bukkit.tc.attachments.particle.VirtualFishingBoundingPlane;
import java.util.Arrays;

public class VirtualFishingBoundingBox
extends VirtualFishingBoundingPlane {
    private final VirtualFishingBoundingPlane.BBOXLine line_top_nx = new VirtualFishingBoundingPlane.BBOXLine(c -> c.top_nx_nz, c -> c.top_nx_pz);
    private final VirtualFishingBoundingPlane.BBOXLine line_top_px = new VirtualFishingBoundingPlane.BBOXLine(c -> c.top_px_nz, c -> c.top_nx_nz);
    private final VirtualFishingBoundingPlane.BBOXLine line_top_nz = new VirtualFishingBoundingPlane.BBOXLine(c -> c.top_px_pz, c -> c.top_px_nz);
    private final VirtualFishingBoundingPlane.BBOXLine line_top_pz = new VirtualFishingBoundingPlane.BBOXLine(c -> c.top_nx_pz, c -> c.top_px_pz);
    private final VirtualFishingBoundingPlane.BBOXLine line_vrt_nxnz = new VirtualFishingBoundingPlane.BBOXLine(c -> c.top_nx_nz, c -> c.btm_nx_nz);
    private final VirtualFishingBoundingPlane.BBOXLine line_vrt_pxnz = new VirtualFishingBoundingPlane.BBOXLine(c -> c.top_px_nz, c -> c.btm_px_nz);
    private final VirtualFishingBoundingPlane.BBOXLine line_vrt_pxpz = new VirtualFishingBoundingPlane.BBOXLine(c -> c.top_px_pz, c -> c.btm_px_pz);
    private final VirtualFishingBoundingPlane.BBOXLine line_vrt_nxpz = new VirtualFishingBoundingPlane.BBOXLine(c -> c.top_nx_pz, c -> c.btm_nx_pz);

    public VirtualFishingBoundingBox(AttachmentManager manager) {
        super(manager);
        this.lines = Arrays.asList(this.line_btm_nx, this.line_btm_nz, this.line_btm_px, this.line_btm_pz, this.line_top_nx, this.line_top_nz, this.line_top_px, this.line_top_pz, this.line_vrt_nxnz, this.line_vrt_pxnz, this.line_vrt_pxpz, this.line_vrt_nxpz);
    }
}

