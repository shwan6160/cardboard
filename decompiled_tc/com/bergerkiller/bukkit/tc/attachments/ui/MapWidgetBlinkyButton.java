/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapBlendMode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.resources.ResourceKey
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapBlendMode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetTooltip;
import java.awt.Color;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MapWidgetBlinkyButton
extends MapWidget {
    private MapTexture icon = MapTexture.createEmpty((int)16, (int)16);
    private MapTexture icon_disabled = MapTexture.createEmpty((int)16, (int)16);
    private MapTexture icon_blink_a = MapTexture.createEmpty((int)16, (int)16);
    private MapTexture icon_blink_b = MapTexture.createEmpty((int)16, (int)16);
    private int blinkCtr = 0;
    private boolean isRepeatClicking = false;
    private boolean blinkMode = false;
    private boolean enableRepeatClicking = false;
    private ResourceKey<SoundEffect> clickSound = SoundEffect.EXTINGUISH;
    public final MapWidgetTooltip tooltip = new MapWidgetTooltip();

    public MapWidgetBlinkyButton() {
        this.setSize(16, 16);
        this.setFocusable(true);
    }

    public MapWidgetBlinkyButton setTooltip(String text) {
        this.tooltip.setText(text);
        return this;
    }

    public MapWidgetBlinkyButton setRepeatClickEnabled(boolean enabled) {
        this.enableRepeatClicking = enabled;
        return this;
    }

    public MapWidgetBlinkyButton setIcon(String filename) {
        return this.setIcon(MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)("com/bergerkiller/bukkit/tc/textures/" + filename)));
    }

    public MapWidgetBlinkyButton setIcon(MapTexture icon) {
        this.icon = icon;
        this.icon_blink_a.clear();
        this.icon_blink_a.setBlendMode(MapBlendMode.NONE);
        this.icon_blink_a.draw((MapCanvas)this.icon, 0, 0);
        this.icon_blink_a.setBlendMode(MapBlendMode.SUBTRACT);
        this.icon_blink_a.fill(MapColorPalette.getColor((int)20, (int)20, (int)64));
        this.icon_blink_b.clear();
        this.icon_blink_b.setBlendMode(MapBlendMode.NONE);
        this.icon_blink_b.draw((MapCanvas)this.icon, 0, 0);
        this.icon_blink_b.setBlendMode(MapBlendMode.ADD);
        this.icon_blink_b.fill(MapColorPalette.getColor((int)80, (int)80, (int)0));
        for (int x = 0; x < this.icon_disabled.getWidth(); ++x) {
            for (int y = 0; y < this.icon_disabled.getHeight(); ++y) {
                byte code = this.icon.readPixel(x, y);
                if (MapColorPalette.isTransparent((byte)code)) {
                    this.icon_disabled.writePixel(x, y, code);
                    continue;
                }
                Color c = MapColorPalette.getRealColor((byte)this.icon.readPixel(x, y));
                int avg = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                this.icon_disabled.writePixel(x, y, MapColorPalette.getColor((int)avg, (int)avg, (int)avg));
            }
        }
        this.setSize(icon.getWidth(), icon.getHeight());
        this.invalidate();
        return this;
    }

    public MapTexture getIcon() {
        return this.icon;
    }

    private void setBlink(boolean mode) {
        if (this.blinkMode != mode) {
            this.blinkMode = mode;
            this.invalidate();
        }
    }

    public MapWidgetBlinkyButton setClickSound(ResourceKey<SoundEffect> effect) {
        this.clickSound = effect;
        return this;
    }

    public void onTick() {
        if (this.isFocused()) {
            if (this.blinkCtr-- == 0) {
                this.blinkCtr = 5;
                this.setBlink(!this.blinkMode);
            }
        } else {
            this.setBlink(false);
            this.blinkCtr = 0;
        }
    }

    public void onDraw() {
        if (!this.isEnabled()) {
            this.view.draw((MapCanvas)this.icon_disabled, 0, 0);
        } else if (!this.isFocused()) {
            this.view.draw((MapCanvas)this.icon, 0, 0);
        } else if (this.blinkMode) {
            this.view.draw((MapCanvas)this.icon_blink_a, 0, 0);
        } else {
            this.view.draw((MapCanvas)this.icon_blink_b, 0, 0);
        }
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (event.getKey() == MapPlayerInput.Key.ENTER) {
            if (!this.enableRepeatClicking || event.getRepeat() <= 1) {
                this.isRepeatClicking = false;
                this.activate();
            } else {
                if (!this.isRepeatClicking) {
                    this.isRepeatClicking = true;
                    this.display.playSound(SoundEffect.CLICK);
                    this.onClickHold();
                }
                this.onRepeatClick();
            }
        } else {
            super.onKeyPressed(event);
        }
    }

    public void onKeyReleased(MapKeyEvent event) {
        super.onKeyReleased(event);
        if (event.getKey() == MapPlayerInput.Key.ENTER && this.isRepeatClicking) {
            this.isRepeatClicking = false;
            this.display.playSound(SoundEffect.CLICK_WOOD);
            this.onClickHoldRelease();
        }
    }

    public void onActivate() {
        if (this.clickSound != null) {
            this.display.playSound(this.clickSound);
        }
        this.onClick();
    }

    public void onFocus() {
        super.onFocus();
        this.addWidget(this.tooltip);
        this.display.playSound(SoundEffect.CLICK_WOOD);
    }

    public void onBlur() {
        super.onBlur();
        this.removeWidget(this.tooltip);
    }

    public abstract void onClick();

    public void onClickHold() {
    }

    public void onClickHoldRelease() {
    }

    public void onRepeatClick() {
    }
}

