/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetText
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetWindow
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetText;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetWindow;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetAttachmentNode;

public class MapWidgetMenu
extends MapWidgetWindow {
    protected MapWidgetAttachmentNode attachment;
    protected byte labelColor = (byte)30;
    protected boolean playSoundWhenBackClosed = false;
    protected boolean exitOnBack = true;

    public MapWidgetMenu() {
        this.setDepthOffset(4);
        this.setFocusable(true);
    }

    public void setAttachment(MapWidgetAttachmentNode attachment) {
        this.attachment = attachment;
    }

    public void setExitOnBack(boolean exitOnBack) {
        this.exitOnBack = exitOnBack;
    }

    public void onAttached() {
        super.onAttached();
        this.activate();
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (this.exitOnBack && event.getKey() == MapPlayerInput.Key.BACK && this.isActivated()) {
            if (this.playSoundWhenBackClosed) {
                this.display.playSound(SoundEffect.CLICK, 1.0f, 0.6f);
            }
            this.close();
            return;
        }
        super.onKeyPressed(event);
    }

    public void onTick() {
        super.onTick();
        if (this.attachment != null && this.attachment.getAttachmentConfig().isRemoved()) {
            this.close();
        }
    }

    public void close() {
        this.removeWidget();
    }

    public void addLabel(int x, int y, String text) {
        MapWidgetText label = new MapWidgetText();
        label.setFont(MapFont.TINY);
        label.setText(text);
        label.setPosition(x, y);
        label.setColor(MapColorPalette.getSpecular((byte)this.labelColor, (float)0.5f));
        this.addWidget((MapWidget)label);
    }
}

