/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.events.map.MapKeyEvent
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapColorPalette
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.MapPlayerInput$Key
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 */
package com.bergerkiller.bukkit.tc.controller.functions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.events.map.MapKeyEvent;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.MapPlayerInput;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import java.util.function.BooleanSupplier;

public class TransferFunctionBoolean
implements TransferFunction {
    public static final TransferFunctionBoolean TRUE = new TransferFunctionBoolean(true);
    public static final TransferFunctionBoolean FALSE = new TransferFunctionBoolean(false);
    public static final TransferFunction.Serializer<TransferFunctionBoolean> SERIALIZER = new TransferFunction.Serializer<TransferFunctionBoolean>(){

        @Override
        public String typeId() {
            return "BOOLEAN";
        }

        @Override
        public String title() {
            return "Yes/No";
        }

        @Override
        public TransferFunctionBoolean createNew(TransferFunctionHost host) {
            return TRUE;
        }

        @Override
        public TransferFunctionBoolean load(TransferFunctionHost host, ConfigurationNode config) {
            return (Boolean)config.getOrDefault("output", (Object)false) != false ? TRUE : FALSE;
        }

        @Override
        public void save(TransferFunctionHost host, ConfigurationNode config, TransferFunctionBoolean function) {
            config.set("output", (Object)function.output);
        }
    };
    private final boolean boolOutput;
    private final double output;

    private TransferFunctionBoolean(boolean output) {
        this.boolOutput = output;
        this.output = this.boolOutput ? 1.0 : 0.0;
    }

    @Override
    public TransferFunction.Serializer<? extends TransferFunction> getSerializer() {
        return SERIALIZER;
    }

    public boolean getOutput() {
        return this.boolOutput;
    }

    public TransferFunctionBoolean opposite() {
        return this.boolOutput ? FALSE : TRUE;
    }

    @Override
    public double map(double input) {
        return this.output;
    }

    @Override
    public boolean isBooleanOutput(BooleanSupplier isBooleanInput) {
        return true;
    }

    @Override
    public boolean isPure() {
        return true;
    }

    @Override
    public TransferFunction clone() {
        return this;
    }

    @Override
    public void drawPreview(MapWidgetTransferFunctionItem widget, MapCanvas view) {
        view.draw(MapFont.MINECRAFT, 0, 3, widget.defaultColor(this.boolOutput ? (byte)30 : 18), (CharSequence)(this.boolOutput ? "Yes [ 1 ]" : "No [ 0 ]"));
    }

    @Override
    public void openDialog(final TransferFunction.Dialog dialog) {
        dialog.addWidget(new TrueFalseToggleWidget(this, this.boolOutput){
            final /* synthetic */ TransferFunctionBoolean this$0;
            {
                this.this$0 = this$0;
                super(initial);
            }

            @Override
            public void onChanged(boolean state) {
                dialog.setFunction(state ? TRUE : FALSE);
            }

            @Override
            public void onClosed() {
                dialog.finish();
            }
        }).setBounds(8, 1, dialog.getWidth() - 16, dialog.getHeight() - 2);
    }

    @Override
    public TransferFunction.DialogMode openDialogMode() {
        return TransferFunction.DialogMode.INLINE;
    }

    private static abstract class TrueFalseToggleWidget
    extends MapWidget {
        private boolean state;

        public TrueFalseToggleWidget(boolean initial) {
            this.state = initial;
            this.setFocusable(true);
        }

        public abstract void onChanged(boolean var1);

        public abstract void onClosed();

        public void onDraw() {
            this.view.draw(MapFont.MINECRAFT, 11, 2, this.state ? MapColorPalette.getColor((int)0, (int)217, (int)58) : MapColorPalette.getColor((int)0, (int)65, (int)0), (CharSequence)"Yes");
            this.view.draw(MapFont.MINECRAFT, 33, 2, (byte)34, (CharSequence)"/");
            this.view.draw(MapFont.MINECRAFT, 43, 2, this.state ? MapColorPalette.getColor((int)100, (int)25, (int)25) : (byte)18, (CharSequence)"No");
        }

        public void onKeyPressed(MapKeyEvent event) {
            if (event.getKey() == MapPlayerInput.Key.LEFT) {
                if (!this.state) {
                    this.state = true;
                    this.invalidate();
                    this.onChanged(true);
                }
            } else if (event.getKey() == MapPlayerInput.Key.RIGHT) {
                if (this.state) {
                    this.state = false;
                    this.invalidate();
                    this.onChanged(false);
                }
            } else if (event.getKey() == MapPlayerInput.Key.ENTER || event.getKey() == MapPlayerInput.Key.BACK) {
                this.onClosed();
            }
        }
    }
}

