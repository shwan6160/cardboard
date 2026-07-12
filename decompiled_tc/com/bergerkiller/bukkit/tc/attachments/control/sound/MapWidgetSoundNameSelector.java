/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 */
package com.bergerkiller.bukkit.tc.attachments.control.sound;

import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetSubmitText;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.attachments.control.sound.MapWidgetSoundElement;
import com.bergerkiller.bukkit.tc.attachments.ui.SetValueTarget;
import java.util.Locale;

abstract class MapWidgetSoundNameSelector
extends MapWidgetSoundElement
implements SetValueTarget {
    private static final int SCROLL_DELAY = 20;
    private static final int SCROLL_HOLD = 80;
    private static final int SCROLL_STEP = 5;
    private ResourceKey<SoundEffect> sound = null;
    private int namespaceWidth = -1;
    private int soundFullWidth = -1;
    private int scrollOffset = 0;
    private int scrollDelayCtr = 0;
    private int scrollHoldCtr = 0;
    private final MapWidgetSubmitText submitText = (this.addWidget((MapWidget)new MapWidgetSubmitText(){

        public void onAccept(String text) {
            MapWidgetSoundNameSelector.this.onSoundAccepted(text);
        }
    })).setDescription("Set Sound Name");

    public MapWidgetSoundNameSelector() {
        this.setSize(60, 11);
        this.setRetainChildWidgets(true);
    }

    public abstract void onSoundChanged(ResourceKey<SoundEffect> var1);

    public MapWidgetSoundNameSelector setSound(ResourceKey<SoundEffect> sound) {
        if (!LogicUtil.bothNullOrEqual(this.sound, sound)) {
            this.sound = sound;
            this.namespaceWidth = -1;
            this.soundFullWidth = -1;
            this.scrollOffset = 0;
            this.scrollDelayCtr = 0;
            this.scrollHoldCtr = 0;
            this.invalidate();
        }
        return this;
    }

    public ResourceKey<SoundEffect> getSound() {
        return this.sound;
    }

    @Override
    public void onDraw() {
        super.onDraw();
        MapCanvas textArea = this.view.getView(2, 2, this.getWidth() - 4, this.getHeight() - 3);
        if (this.sound != null) {
            byte namespaceColor = MapColorPalette.getColor((int)255, (int)160, (int)160);
            byte pathColor = 34;
            this.calcWidths();
            textArea.draw(MapFont.MINECRAFT, -this.scrollOffset, 0, namespaceColor, (CharSequence)(this.sound.getName().getNamespace() + ":"));
            textArea.draw(MapFont.MINECRAFT, this.namespaceWidth - this.scrollOffset, 0, pathColor, (CharSequence)this.sound.getName().getName());
        } else {
            textArea.draw(MapFont.MINECRAFT, 0, 0, (byte)18, (CharSequence)"<No Sound>");
        }
    }

    public void onTick() {
        this.calcWidths();
        int overflow = this.soundFullWidth - (this.getWidth() - 4);
        if (overflow > 0 && ++this.scrollDelayCtr > 20) {
            int newOffset = Math.min(overflow, this.scrollOffset + 5);
            if (this.scrollOffset != newOffset) {
                this.scrollOffset = newOffset;
                this.invalidate();
            } else if (++this.scrollHoldCtr > 80) {
                this.scrollDelayCtr = 0;
                this.scrollHoldCtr = 0;
                this.scrollOffset = 0;
                this.invalidate();
            }
        }
    }

    public void onActivate() {
        if (this.submitText.isActivated()) {
            this.focus();
        } else {
            this.submitText.activate();
        }
    }

    private void calcWidths() {
        if (this.namespaceWidth != -1 && this.soundFullWidth != -1) {
            return;
        }
        if (this.sound == null) {
            this.namespaceWidth = 0;
            this.soundFullWidth = 0;
        } else {
            this.namespaceWidth = (int)this.view.calcFontSize(MapFont.MINECRAFT, (CharSequence)(this.sound.getName().getNamespace() + ":")).getWidth();
            this.soundFullWidth = this.namespaceWidth + (int)this.view.calcFontSize(MapFont.MINECRAFT, (CharSequence)this.sound.getName().getName()).getWidth();
        }
    }

    private void onSoundAccepted(String soundName) {
        if ((soundName = soundName.trim().toLowerCase(Locale.ENGLISH).replace(' ', '_')).isEmpty()) {
            this.setSound(null);
        } else {
            this.setSound((ResourceKey<SoundEffect>)SoundEffect.fromName((String)soundName));
        }
        this.onSoundChanged(this.getSound());
    }

    @Override
    public String getAcceptedPropertyName() {
        return "Sound Name";
    }

    @Override
    public boolean acceptTextValue(String value) {
        this.onSoundAccepted(value);
        return true;
    }
}

