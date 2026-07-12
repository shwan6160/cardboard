/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.MapTexture
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  org.bukkit.block.BlockFace
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.tc.controller.functions.ui.conditional;

import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.MapTexture;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetArrow;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.attachments.ui.SetValueTarget;
import java.text.NumberFormat;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MapWidgetTransferFunctionConditionalHysteresis
extends MapWidget
implements SetValueTarget {
    private static final byte COLOR_BG_DEFAULT = MapColorPalette.getColor((int)199, (int)199, (int)199);
    private static final byte COLOR_BG_FOCUSED = MapColorPalette.getColor((int)255, (int)252, (int)245);
    private static final byte COLOR_BG_ACTIVATED = MapColorPalette.getColor((int)247, (int)233, (int)163);
    private static final NumberFormat NUMBER_FORMAT = Util.createNumberFormat(1, 4);
    private static final MapTexture HYSTERESIS_ICON = MapTexture.loadPluginResource((JavaPlugin)TrainCarts.plugin, (String)"com/bergerkiller/bukkit/tc/textures/attachments/hysteresis.png");
    private final MapWidgetArrow leftArrow = new MapWidgetArrow(BlockFace.WEST);
    private final MapWidgetArrow rightArrow = new MapWidgetArrow(BlockFace.EAST);
    private double hysteresis;

    public MapWidgetTransferFunctionConditionalHysteresis(double hysteresis) {
        this.hysteresis = hysteresis;
        this.setFocusable(true);
    }

    public abstract void onHysteresisChanged(double var1);

    public void setHysteresis(double hysteresis) {
        if (this.hysteresis != hysteresis) {
            this.hysteresis = hysteresis;
            this.invalidate();
            this.onHysteresisChanged(hysteresis);
        }
    }

    private void increment(double incr, int repeat) {
        this.setHysteresis(MapWidgetNumberBox.scaledIncrease(this.hysteresis, incr, repeat));
    }

    @Override
    public String getAcceptedPropertyName() {
        return "Hysteresis";
    }

    @Override
    public boolean acceptTextValue(String value) {
        return this.acceptTextValue(SetValueTarget.Operation.SET, value);
    }

    @Override
    public boolean acceptTextValue(SetValueTarget.Operation operation, String value) {
        return operation.perform(() -> this.hysteresis, this::setHysteresis, value);
    }

    public void onActivate() {
        this.addWidget(this.leftArrow.setPosition(-this.leftArrow.getWidth() - 1, (this.getHeight() - this.leftArrow.getHeight()) / 2));
        this.addWidget(this.rightArrow.setPosition(this.getWidth() + 1, (this.getHeight() - this.rightArrow.getHeight()) / 2));
        super.onActivate();
    }

    public void onDeactivate() {
        this.removeWidget(this.leftArrow);
        this.removeWidget(this.rightArrow);
        super.onDeactivate();
    }

    public void onKeyPressed(MapKeyEvent event) {
        if (!this.isActivated()) {
            super.onKeyPressed(event);
            return;
        }
        if (event.getKey() == MapPlayerInput.Key.LEFT) {
            this.increment(-0.001, event.getRepeat());
            this.leftArrow.sendFocus();
            this.rightArrow.stopFocus();
        } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
            this.increment(0.001, event.getRepeat());
            this.rightArrow.sendFocus();
            this.leftArrow.stopFocus();
        } else if (event.getKey() == MapPlayerInput.Key.ENTER) {
            this.setHysteresis(0.0);
            this.display.playSound(SoundEffect.EXTINGUISH);
        } else {
            this.focus();
            if (event.getKey() == MapPlayerInput.Key.UP || event.getKey() == MapPlayerInput.Key.DOWN) {
                super.onKeyPressed(event);
            }
        }
    }

    public void onKeyReleased(MapKeyEvent event) {
        if (this.isActivated()) {
            if (event.getKey() == MapPlayerInput.Key.LEFT) {
                this.leftArrow.stopFocus();
            } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
                this.rightArrow.stopFocus();
            }
        }
        super.onKeyReleased(event);
    }

    public void onDraw() {
        this.view.drawRectangle(0, 0, this.getWidth(), this.getHeight(), (byte)119);
        this.view.fillRectangle(1, 1, this.getWidth() - 2, this.getHeight() - 2, this.isActivated() ? COLOR_BG_ACTIVATED : (this.isFocused() ? COLOR_BG_FOCUSED : COLOR_BG_DEFAULT));
        byte color = this.isFocused() ? (byte)50 : 119;
        this.view.draw((MapCanvas)HYSTERESIS_ICON, 2, 2, color);
        this.view.draw(MapFont.MINECRAFT, 14, 3, color, (CharSequence)NUMBER_FORMAT.format(this.hysteresis));
    }
}

