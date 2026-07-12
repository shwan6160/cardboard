/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.control.sound;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundButton;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetTooltip;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MapWidgetSoundPositionMode
extends MapWidgetSoundButton {
    private SoundPositionMode mode = SoundPositionMode.DEFAULT;
    private boolean isSamePerspective = false;
    public final MapWidgetTooltip tooltip = new MapWidgetTooltip().setText(this.mode.getTooltip());

    public MapWidgetSoundPositionMode() {
        this.setSize(this.mode.getIcon().getWidth(), this.mode.getIcon().getHeight());
    }

    public abstract void onModeChanged(SoundPositionMode var1);

    public MapWidgetSoundPositionMode setIsSamePerspective(boolean isSamePerspective) {
        if (this.isSamePerspective != isSamePerspective) {
            this.isSamePerspective = isSamePerspective;
            if (this.mode.isAtPlayer1P() != this.mode.isAtPlayer3P()) {
                this.tooltip.setText(this.getDisplayedMode().getTooltip());
                this.invalidate();
            }
        }
        return this;
    }

    public MapWidgetSoundPositionMode setMode(boolean atPerson1P, boolean atPerson3P) {
        return this.setMode(SoundPositionMode.fromPerspectives(atPerson1P, atPerson3P));
    }

    public MapWidgetSoundPositionMode setMode(SoundPositionMode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            this.tooltip.setText(this.getDisplayedMode().getTooltip());
            this.invalidate();
        }
        return this;
    }

    public SoundPositionMode getMode() {
        return this.mode;
    }

    @Override
    public void onClick() {
        SoundPositionMode[] values = this.isSamePerspective ? new SoundPositionMode[]{SoundPositionMode.DEFAULT, SoundPositionMode.AT_PLAYER} : SoundPositionMode.values();
        this.mode = values[(this.getDisplayedMode().ordinal() + 1) % values.length];
        this.tooltip.setText(this.getDisplayedMode().getTooltip());
        this.onModeChanged(this.mode);
        this.invalidate();
    }

    public void onFocus() {
        super.onFocus();
        this.addWidget(this.tooltip);
    }

    @Override
    public void onBlur() {
        super.onBlur();
        this.removeWidget(this.tooltip);
    }

    @Override
    public void onDraw() {
        super.onDraw();
        this.view.draw((MapCanvas)this.getDisplayedMode().getIcon(), 0, 0);
    }

    private SoundPositionMode getDisplayedMode() {
        if (this.isSamePerspective && this.mode.isAtPlayer1P() != this.mode.isAtPlayer3P()) {
            return this.mode.isAtPlayer1P() ? SoundPositionMode.AT_PLAYER : SoundPositionMode.DEFAULT;
        }
        return this.mode;
    }

    public static enum SoundPositionMode {
        DEFAULT("play at position", false, false),
        AT_PLAYER("play at player", true, true),
        FIRST_PERSON_AT_PLAYER("play 1p at player\nplay 3p at position", true, false),
        THIRD_PERSON_AT_PLAYER("play 1p at position\nplay 3p at player", false, true);

        private final MapTexture icon;
        private final String tooltip;
        private final boolean firstPersonAtPlayer;
        private final boolean thirdPersonAtPlayer;

        private SoundPositionMode(String tooltip, boolean firstPersonAtPlayer, boolean thirdPersonAtPlayer) {
            MapTexture tex = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/sound_positions.png");
            this.icon = tex.getView(tex.getHeight() * this.ordinal(), 0, tex.getHeight(), tex.getHeight()).clone();
            this.tooltip = tooltip;
            this.firstPersonAtPlayer = firstPersonAtPlayer;
            this.thirdPersonAtPlayer = thirdPersonAtPlayer;
        }

        public String getTooltip() {
            return this.tooltip;
        }

        public MapTexture getIcon() {
            return this.icon;
        }

        public boolean isAtPlayer1P() {
            return this.firstPersonAtPlayer;
        }

        public boolean isAtPlayer3P() {
            return this.thirdPersonAtPlayer;
        }

        public static SoundPositionMode fromPerspectives(boolean atPlayer1P, boolean atPlayer3P) {
            if (atPlayer1P) {
                if (atPlayer3P) {
                    return AT_PLAYER;
                }
                return FIRST_PERSON_AT_PLAYER;
            }
            if (atPlayer3P) {
                return THIRD_PERSON_AT_PLAYER;
            }
            return DEFAULT;
        }
    }
}

