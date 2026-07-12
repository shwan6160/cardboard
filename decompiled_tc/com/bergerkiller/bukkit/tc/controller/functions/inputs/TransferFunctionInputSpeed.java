/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapFont
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 *  com.bergerkiller.bukkit.common.resources.SoundEffect
 *  com.bergerkiller.bukkit.common.utils.MathUtil
 *  org.bukkit.util.Vector
 */
package com.bergerkiller.bukkit.tc.controller.functions.inputs;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInput;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import org.bukkit.util.Vector;

public class TransferFunctionInputSpeed
extends TransferFunctionInput {
    public static final TransferFunction.Serializer<TransferFunctionInputSpeed> SERIALIZER = new TransferFunction.Serializer<TransferFunctionInputSpeed>(){

        @Override
        public String typeId() {
            return "INPUT-SPEED";
        }

        @Override
        public String title() {
            return "In: Move Speed";
        }

        @Override
        public boolean isInput() {
            return true;
        }

        @Override
        public TransferFunctionInputSpeed createNew(TransferFunctionHost host) {
            TransferFunctionInputSpeed speed = new TransferFunctionInputSpeed();
            speed.updateSource(host);
            return speed;
        }

        @Override
        public TransferFunctionInputSpeed load(TransferFunctionHost host, ConfigurationNode config) {
            TransferFunctionInputSpeed speed = new TransferFunctionInputSpeed();
            speed.setSourceMode((SourceMode)((Object)config.getOrDefault("mode", (Object)SourceMode.TRAIN)));
            speed.setOutputMode((OutputMode)((Object)config.getOrDefault("output", (Object)OutputMode.SPEED)));
            speed.updateSource(host);
            return speed;
        }

        @Override
        public void save(TransferFunctionHost host, ConfigurationNode config, TransferFunctionInputSpeed function) {
            config.set("mode", (Object)function.sourceMode);
            config.set("output", (Object)function.outputMode);
        }
    };
    private SourceMode sourceMode = SourceMode.TRAIN;
    private OutputMode outputMode = OutputMode.SPEED;

    public void setSourceMode(SourceMode mode) {
        this.sourceMode = mode;
    }

    public void setOutputMode(OutputMode mode) {
        this.outputMode = mode;
    }

    @Override
    public TransferFunction.Serializer<? extends TransferFunction> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public TransferFunctionInput.ReferencedSource createSource(TransferFunctionHost host) {
        MinecartMember<?> member;
        TransferFunctionInput.ReferencedSource result = this.sourceMode == SourceMode.TRAIN ? ((member = host.getMember()) != null ? new TrainSpeedReferencedSource(member) : TransferFunctionInput.ReferencedSource.NONE) : new AttachmentSpeedReferencedSource();
        if (this.outputMode == OutputMode.ACCELERATION) {
            result = new AccelerationReferencedSource(result);
        }
        return result;
    }

    @Override
    protected TransferFunctionInput cloneInput() {
        return new TransferFunctionInputSpeed();
    }

    @Override
    public boolean isBooleanOutput() {
        return false;
    }

    @Override
    public void drawPreview(MapWidgetTransferFunctionItem widget, MapCanvas view) {
        view.draw(MapFont.MINECRAFT, 0, 3, (byte)30, (CharSequence)(this.outputMode == OutputMode.SPEED ? "<Move Speed>" : "<Move Accel.>"));
    }

    @Override
    public void openDialog(final TransferFunction.Dialog dialog) {
        super.openDialog(dialog);
        dialog.addLabel(5, 20, (byte)18, "Speed of:");
        dialog.addWidget(new MapWidgetButton(this){
            final /* synthetic */ TransferFunctionInputSpeed this$0;
            {
                this.this$0 = this$0;
            }

            public void onAttached() {
                this.updateText();
                super.onAttached();
            }

            public void onActivate() {
                this.display.playSound(SoundEffect.CLICK);
                this.this$0.sourceMode = SourceMode.values()[(this.this$0.sourceMode.ordinal() + 1) % SourceMode.values().length];
                this.this$0.updateSource(dialog.getHost());
                this.updateText();
                dialog.markChanged();
            }

            private void updateText() {
                this.setText(this.this$0.sourceMode.name());
            }
        }).setBounds(5, 27, 70, 13);
        dialog.addLabel(5, 43, (byte)18, "Output:");
        dialog.addWidget(new MapWidgetButton(this){
            final /* synthetic */ TransferFunctionInputSpeed this$0;
            {
                this.this$0 = this$0;
            }

            public void onAttached() {
                this.updateText();
                super.onAttached();
            }

            public void onActivate() {
                this.display.playSound(SoundEffect.CLICK);
                this.this$0.outputMode = OutputMode.values()[(this.this$0.outputMode.ordinal() + 1) % SourceMode.values().length];
                this.this$0.updateSource(dialog.getHost());
                this.updateText();
                dialog.markChanged();
            }

            private void updateText() {
                this.setText(this.this$0.outputMode.name());
            }
        }).setBounds(5, 50, 70, 13);
    }

    public static enum OutputMode {
        SPEED,
        ACCELERATION;

    }

    public static enum SourceMode {
        TRAIN,
        ATTACHMENT;

    }

    private static class TrainSpeedReferencedSource
    extends TransferFunctionInput.ReferencedSource {
        private final MinecartMember<?> member;

        public TrainSpeedReferencedSource(MinecartMember<?> member) {
            this.member = member;
        }

        @Override
        public void onTick() {
            this.value = this.member.isUnloaded() ? 0.0 : this.member.getRealSpeedLimited();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof TrainSpeedReferencedSource;
        }
    }

    private static class AttachmentSpeedReferencedSource
    extends TransferFunctionInput.ReferencedSource {
        private final Vector prevPosition = new Vector();
        private boolean first = true;

        @Override
        public void onTransform(Matrix4x4 transform) {
            if (this.first) {
                this.first = false;
                MathUtil.setVector((Vector)this.prevPosition, (Vector)transform.toVector());
                this.value = 0.0;
            } else {
                Vector newPosition = transform.toVector();
                this.value = newPosition.distance(this.prevPosition);
                MathUtil.setVector((Vector)this.prevPosition, (Vector)newPosition);
            }
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof AttachmentSpeedReferencedSource;
        }
    }

    private static class AccelerationReferencedSource
    extends TransferFunctionInput.ReferencedSource {
        private final TransferFunctionInput.ReferencedSource base;
        private double prevValue = Double.NaN;

        public AccelerationReferencedSource(TransferFunctionInput.ReferencedSource base) {
            this.base = base;
        }

        @Override
        public void onTick() {
            this.base.onTick();
            double prevValue = this.prevValue;
            double newValue = this.base.value();
            this.value = Double.isNaN(prevValue) ? 0.0 : newValue - prevValue;
            this.prevValue = newValue;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof AccelerationReferencedSource) {
                return ((AccelerationReferencedSource)o).base.equals(this.base);
            }
            return false;
        }
    }
}

