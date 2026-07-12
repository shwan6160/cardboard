/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 */
package com.bergerkiller.bukkit.tc.attachments.control.effect;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetMenu;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;

public class SimpleScheduledEffectLoopDialog
extends MapWidgetMenu {
    private final ConfigurationNode config;

    public SimpleScheduledEffectLoopDialog(ConfigurationNode config) {
        this.config = config;
        this.setPositionAbsolute(true);
        this.setBounds(39, 40, 70, 30);
        this.setBackgroundColor(MapColorPalette.getColor((int)72, (int)108, (int)152));
        this.labelColor = (byte)119;
    }

    @Override
    public void onAttached() {
        this.addLabel(5, 6, "Delay (s):");
        (this.addWidget(new MapWidgetNumberBox(){

            @Override
            public void onAttached() {
                this.setIncrement(0.01);
                this.setRange(0.0, 10000.0);
                this.setInitialValue((Double)SimpleScheduledEffectLoopDialog.this.config.getOrDefault("delay", (Object)0.0));
                super.onAttached();
            }

            @Override
            public void onValueChanged() {
                SimpleScheduledEffectLoopDialog.this.config.set("delay", this.getValue() == 0.0 ? null : Double.valueOf(this.getValue()));
            }
        })).setBounds(5, 13, this.getWidth() - 10, 11);
        super.onAttached();
    }
}

