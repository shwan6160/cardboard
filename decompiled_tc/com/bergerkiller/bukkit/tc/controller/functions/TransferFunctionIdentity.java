/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapFont
 */
package com.bergerkiller.bukkit.tc.controller.functions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import java.util.function.BooleanSupplier;

class TransferFunctionIdentity
implements TransferFunction {
    public static final TransferFunctionIdentity INSTANCE = new TransferFunctionIdentity();
    public static final TransferFunction.Serializer<TransferFunctionIdentity> SERIALIZER = new TransferFunction.Serializer<TransferFunctionIdentity>(){

        @Override
        public String typeId() {
            return "IDENTITY";
        }

        @Override
        public String title() {
            return "Identity";
        }

        @Override
        public boolean isListed(TransferFunctionHost host) {
            return false;
        }

        @Override
        public TransferFunctionIdentity createNew(TransferFunctionHost host) {
            return INSTANCE;
        }

        @Override
        public TransferFunctionIdentity load(TransferFunctionHost host, ConfigurationNode config) {
            return INSTANCE;
        }

        @Override
        public void save(TransferFunctionHost host, ConfigurationNode config, TransferFunctionIdentity function) {
        }
    };

    private TransferFunctionIdentity() {
    }

    @Override
    public TransferFunction.Serializer<? extends TransferFunction> getSerializer() {
        return SERIALIZER;
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
    public boolean isBooleanOutput(BooleanSupplier isBooleanInput) {
        return isBooleanInput.getAsBoolean();
    }

    @Override
    public TransferFunction clone() {
        return INSTANCE;
    }

    @Override
    public void drawPreview(MapWidgetTransferFunctionItem widget, MapCanvas view) {
        view.draw(MapFont.MINECRAFT, 2, 3, (byte)50, (CharSequence)"<Input>");
    }

    @Override
    public void openDialog(TransferFunction.Dialog dialog) {
    }

    @Override
    public TransferFunction.DialogMode openDialogMode() {
        return TransferFunction.DialogMode.NONE;
    }
}

