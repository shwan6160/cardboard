/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control.sound;

import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundSelector;
import org.bukkit.plugin.java.JavaPlugin;

public enum SoundPerspectiveMode {
    SAME("play same sound for\nall perspectives"),
    CART("1p cart passengers\n3p outside cart"),
    TRAIN("1p train passengers\n3p outside train"),
    SEAT("1p seat passenger\n3p outside seat");

    private final MapTexture icon;
    private final String tooltip;

    private SoundPerspectiveMode(String tooltip) {
        MapTexture tex = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/sound_perspectives.png");
        this.icon = tex.getView(tex.getHeight() * this.ordinal(), 0, tex.getHeight(), tex.getHeight()).clone();
        this.tooltip = tooltip;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public MapTexture getIcon() {
        return this.icon;
    }

    public MapWidgetSoundSelector.Mode getSoundMode() {
        return this == SAME ? MapWidgetSoundSelector.Mode.ALL_PERSPECTIVE : MapWidgetSoundSelector.Mode.FIRST_PERSPECTIVE;
    }

    public MapWidgetSoundSelector.Mode getSoundAltMode() {
        return this == SAME ? MapWidgetSoundSelector.Mode.NONE : MapWidgetSoundSelector.Mode.THIRD_PERSPECTIVE;
    }
}

