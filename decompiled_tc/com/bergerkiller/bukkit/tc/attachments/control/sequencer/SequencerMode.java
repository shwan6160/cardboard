/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapTexture
 */
package com.bergerkiller.bukkit.tc.attachments.control.sequencer;

import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.tc.attachments.control.sequencer.MapWidgetSequencerEffect;

public enum SequencerMode {
    START("start", "start"),
    LOOP("loop", "loop"),
    STOP("stop", "stop");

    private final String title;
    private final String configKey;
    private final MapTexture icon;

    private SequencerMode(String title, String configKey) {
        this.title = title;
        this.configKey = configKey;
        this.icon = MapWidgetSequencerEffect.TEXTURE_ATLAS.getView(7 * this.ordinal(), 35, 7, 5).clone();
    }

    public String title() {
        return this.title;
    }

    public String configKey() {
        return this.configKey;
    }

    public MapTexture icon() {
        return this.icon;
    }

    public SequencerMode next() {
        switch (this.ordinal()) {
            case 0: 
            case 1: {
                return LOOP;
            }
        }
        return START;
    }
}

