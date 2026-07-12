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
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetNumberBox;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import java.text.NumberFormat;

public final class TransferFunctionConstant
implements TransferFunction {
    public static final TransferFunction.Serializer<TransferFunctionConstant> SERIALIZER = new TransferFunction.Serializer<TransferFunctionConstant>(){

        @Override
        public String typeId() {
            return "CONSTANT";
        }

        @Override
        public String title() {
            return "Constant";
        }

        @Override
        public TransferFunctionConstant createNew(TransferFunctionHost host) {
            return TransferFunctionConstant.zero();
        }

        @Override
        public TransferFunctionConstant load(TransferFunctionHost host, ConfigurationNode config) {
            double output = (Double)config.getOrDefault("output", (Object)0.0);
            return new TransferFunctionConstant(output);
        }

        @Override
        public void save(TransferFunctionHost host, ConfigurationNode config, TransferFunctionConstant function) {
            config.set("output", (Object)function.output);
        }
    };
    private static final NumberFormat PREVIEW_NUM_FORMAT = Util.createNumberFormat(1, 5);
    private double output;

    public static TransferFunctionConstant zero() {
        return new TransferFunctionConstant(0.0);
    }

    public static TransferFunctionConstant of(double output) {
        return new TransferFunctionConstant(output);
    }

    private TransferFunctionConstant(double output) {
        this.output = output;
    }

    @Override
    public TransferFunction.Serializer<? extends TransferFunction> getSerializer() {
        return SERIALIZER;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public double getOutput() {
        return this.output;
    }

    @Override
    public double map(double input) {
        return this.output;
    }

    @Override
    public boolean isPure() {
        return true;
    }

    @Override
    public TransferFunction clone() {
        return new TransferFunctionConstant(this.output);
    }

    @Override
    public void drawPreview(MapWidgetTransferFunctionItem widget, MapCanvas view) {
        view.draw(MapFont.MINECRAFT, 0, 3, widget.defaultColor((byte)30), (CharSequence)PREVIEW_NUM_FORMAT.format(this.output));
    }

    @Override
    public void openDialog(final TransferFunction.Dialog dialog) {
        dialog.addWidget(new MapWidgetNumberBox(this){
            final /* synthetic */ TransferFunctionConstant this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onAttached() {
                this.setInitialValue(this.this$0.output);
                this.setIncrement(0.001);
                super.onAttached();
            }

            @Override
            public void onValueChanged() {
                this.this$0.output = this.getValue();
                dialog.markChanged();
            }
        }).setBounds(8, 1, dialog.getWidth() - 16, dialog.getHeight() - 2);
    }

    @Override
    public TransferFunction.DialogMode openDialogMode() {
        return TransferFunction.DialogMode.INLINE;
    }
}

