/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  org.bukkit.entity.Player
 */
package com.bergerkiller.bukkit.tc.attachments.control.seat;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentViewer;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSeat;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonEyePositionArrow;
import com.bergerkiller.bukkit.tc.attachments.control.seat.FirstPersonViewMode;
import com.bergerkiller.bukkit.tc.attachments.control.seat.spectator.FirstPersonEyePreview;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.entity.Player;

public class SeatDebugUI {
    private final CartAttachmentSeat seat;
    private final Map<Player, FirstPersonEyePreview> _eyePreviews;
    private final FirstPersonEyePositionArrow _eyeArrow;

    public SeatDebugUI(CartAttachmentSeat seat) {
        this.seat = seat;
        this._eyePreviews = new HashMap<Player, FirstPersonEyePreview>();
        this._eyeArrow = new FirstPersonEyePositionArrow(seat);
    }

    public void previewEye(Player player, int numTicks) {
        if (!this.seat.isAttached() || this.seat.firstPerson.player == player || !player.isValid()) {
            return;
        }
        if (numTicks <= 0) {
            FirstPersonEyePreview preview = this._eyePreviews.remove(player);
            if (preview != null) {
                preview.stop();
                this.onEyePreviewStopped(preview.player);
            }
            return;
        }
        FirstPersonEyePreview preview = this._eyePreviews.computeIfAbsent(player, p -> new FirstPersonEyePreview(this.seat, this.seat.getManager().asAttachmentViewer((Player)p)));
        if (preview.start(numTicks, this.seat.firstPerson.getEyeTransform())) {
            this.onEyePreviewStarted(preview.player);
        }
    }

    public void showEyeArrow(Player player, int numTicks) {
        if (!this.seat.isAttached() || this.seat.firstPerson.player == player || !player.isValid() || this._eyePreviews.containsKey(player)) {
            return;
        }
        if (numTicks <= 0) {
            this._eyeArrow.stop(this.seat.getManager().asAttachmentViewer(player));
        } else {
            this._eyeArrow.start(this.seat.getManager().asAttachmentViewer(player), numTicks);
        }
    }

    public void updateEyePreview() {
        if (!this._eyePreviews.isEmpty()) {
            Matrix4x4 eyeTransform = this.seat.firstPerson.getEyeTransform();
            Iterator<FirstPersonEyePreview> iter = this._eyePreviews.values().iterator();
            do {
                FirstPersonEyePreview preview;
                if (!(preview = iter.next()).updateRemaining()) {
                    iter.remove();
                    this.onEyePreviewStopped(preview.player);
                    continue;
                }
                if (!preview.player.isValid()) {
                    iter.remove();
                    continue;
                }
                preview.updatePosition(eyeTransform);
            } while (iter.hasNext());
        }
        this._eyeArrow.updatePosition();
    }

    public void syncEyePreviews(boolean absolute) {
        if (!this._eyePreviews.isEmpty()) {
            for (FirstPersonEyePreview preview : this._eyePreviews.values()) {
                preview.syncPosition(absolute);
            }
        }
        this._eyeArrow.syncPosition(absolute);
    }

    public void stopEyePreviews() {
        if (!this._eyePreviews.isEmpty()) {
            for (FirstPersonEyePreview preview : this._eyePreviews.values()) {
                preview.stop();
            }
            this._eyePreviews.clear();
        }
        this._eyeArrow.stop();
    }

    public boolean isSeatedEntityHiddenBecauseOfPreview(Player player) {
        return this._eyePreviews.containsKey(player) && this.seat.seated.isDisplayed() && this.seat.firstPerson.getLiveMode() != FirstPersonViewMode.THIRD_P;
    }

    private void onEyePreviewStarted(AttachmentViewer player) {
        if (this.seat.seated.isDisplayed() && this.seat.firstPerson.getLiveMode() != FirstPersonViewMode.THIRD_P) {
            this.seat.seated.makeHidden(player);
        }
        this._eyeArrow.stop(player);
    }

    private void onEyePreviewStopped(AttachmentViewer player) {
        if (this.seat.seated.isDisplayed() && this.seat.firstPerson.getLiveMode() != FirstPersonViewMode.THIRD_P) {
            this.seat.seated.makeVisible(player);
        }
    }
}

