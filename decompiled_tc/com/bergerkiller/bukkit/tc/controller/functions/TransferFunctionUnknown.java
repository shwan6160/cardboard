/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapFont
 */
package com.bergerkiller.bukkit.tc.controller.functions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.config.yaml.YamlNodeAbstract;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import java.util.Collections;

class TransferFunctionUnknown
implements TransferFunction {
    private final String typeId;
    private final ConfigurationNode config;
    private final boolean error;

    public TransferFunctionUnknown(String typeId, ConfigurationNode config, boolean error) {
        this.typeId = typeId;
        this.config = config.clone();
        this.error = error;
    }

    @Override
    public TransferFunction.Serializer<? extends TransferFunction> getSerializer() {
        return new TransferFunction.Serializer<TransferFunctionUnknown>(){

            @Override
            public String typeId() {
                return TransferFunctionUnknown.this.typeId;
            }

            @Override
            public String title() {
                return (TransferFunctionUnknown.this.error ? "LOAD ERROR [" : "UNKNOWN [") + TransferFunctionUnknown.this.typeId + "]";
            }

            @Override
            public TransferFunctionUnknown createNew(TransferFunctionHost host) {
                return TransferFunctionUnknown.this.clone();
            }

            @Override
            public TransferFunctionUnknown load(TransferFunctionHost host, ConfigurationNode config) {
                return new TransferFunctionUnknown(this.typeId(), config, TransferFunctionUnknown.this.error);
            }

            @Override
            public void save(TransferFunctionHost host, ConfigurationNode config, TransferFunctionUnknown function) {
                config.setToExcept((YamlNodeAbstract)function.config.clone(), Collections.singleton("type"));
            }
        };
    }

    @Override
    public double map(double input) {
        return input;
    }

    @Override
    public boolean isPure() {
        return true;
    }

    @Override
    public void drawPreview(MapWidgetTransferFunctionItem widget, MapCanvas view) {
        view.draw(MapFont.MINECRAFT, 2, 2, (byte)18, (CharSequence)("Unknown [" + this.typeId + "]"));
    }

    @Override
    public void openDialog(TransferFunction.Dialog dialog) {
    }

    @Override
    public TransferFunction.DialogMode openDialogMode() {
        return TransferFunction.DialogMode.NONE;
    }

    @Override
    public TransferFunctionUnknown clone() {
        return new TransferFunctionUnknown(this.typeId, this.config, this.error);
    }
}

