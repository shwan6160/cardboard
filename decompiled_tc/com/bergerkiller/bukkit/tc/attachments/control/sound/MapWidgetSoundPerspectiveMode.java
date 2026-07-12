/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 */
package com.bergerkiller.bukkit.tc.attachments.control.sound;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundButton;
import com.bergerkiller.bukkit.tc.attachments.control.sound.SoundPerspectiveMode;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetTooltip;

public abstract class MapWidgetSoundPerspectiveMode
extends MapWidgetSoundButton {
    private SoundPerspectiveMode mode = SoundPerspectiveMode.SAME;
    public final MapWidgetTooltip tooltip = new MapWidgetTooltip().setText(this.mode.getTooltip());

    public MapWidgetSoundPerspectiveMode() {
        this.setSize(this.mode.getIcon().getWidth(), this.mode.getIcon().getHeight());
    }

    public abstract void onModeChanged(SoundPerspectiveMode var1);

    public MapWidgetSoundPerspectiveMode setMode(SoundPerspectiveMode mode) {
        if (this.mode != mode) {
            this.mode = mode;
            this.tooltip.setText(mode.getTooltip());
            this.invalidate();
        }
        return this;
    }

    public SoundPerspectiveMode getMode() {
        return this.mode;
    }

    @Override
    public void onClick() {
        SoundPerspectiveMode[] values = SoundPerspectiveMode.values();
        this.mode = values[(this.mode.ordinal() + 1) % values.length];
        this.tooltip.setText(this.mode.getTooltip());
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
        this.view.draw((MapCanvas)this.mode.getIcon(), 0, 0);
    }
}

