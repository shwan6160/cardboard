/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat.spectator;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.attachments.VirtualEntity;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewSpectator;
import com.bergerkiller.bukkit.tc.attachments.control.seat.SeatedEntity;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.FirstPersonSpectatedEntityInvisible;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.FirstPersonSpectatedEntityPlayerSitting;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.FirstPersonSpectatedEntityPlayerStanding;

public abstract class FirstPersonSpectatedEntity {
    protected final CartAttachmentSeat seat;
    protected final FirstPersonViewSpectator view;
    protected final AttachmentViewer player;

    public FirstPersonSpectatedEntity(CartAttachmentSeat seat, FirstPersonViewSpectator view, AttachmentViewer player) {
        this.seat = seat;
        this.view = view;
        this.player = player;
    }

    public abstract void start(Matrix4x4 var1);

    public abstract void stop();

    public abstract void updatePosition(Matrix4x4 var1);

    public abstract void syncPosition(boolean var1);

    public abstract VirtualEntity getCurrentEntity();

    public static FirstPersonSpectatedEntity create(CartAttachmentSeat seat, FirstPersonViewSpectator view, AttachmentViewer player) {
        if (view.getLiveMode() == FirstPersonViewMode.INVISIBLE || view.getLiveMode() == FirstPersonViewMode.THIRD_P) {
            return new FirstPersonSpectatedEntityInvisible(seat, view, player);
        }
        if (seat.seated.getDisplayMode() == SeatedEntity.DisplayMode.STANDING) {
            return new FirstPersonSpectatedEntityPlayerStanding(seat, view, player);
        }
        return new FirstPersonSpectatedEntityPlayerSitting(seat, view, player);
    }
}

