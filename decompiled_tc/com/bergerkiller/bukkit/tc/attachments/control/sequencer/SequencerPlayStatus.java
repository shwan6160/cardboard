/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.attachments.control.sequencer;

public enum SequencerPlayStatus {
    PLAYING_MANUAL(true, false),
    PLAYING_AUTOMATIC(true, true),
    STOPPED_MANUAL(false, false),
    STOPPED_AUTOMATIC(false, true);

    private final boolean playing;
    private final boolean automatic;

    private SequencerPlayStatus(boolean playing, boolean automatic) {
        this.playing = playing;
        this.automatic = automatic;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public boolean isAutomatic() {
        return this.automatic;
    }
}

