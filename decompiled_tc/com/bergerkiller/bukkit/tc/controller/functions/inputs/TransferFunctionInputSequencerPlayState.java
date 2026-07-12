/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.MapCanvas
 *  com.bergerkiller.bukkit.common.map.MapFont
 */
package com.bergerkiller.bukkit.tc.controller.functions.inputs;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.MapCanvas;
import com.bergerkiller.bukkit.common.map.MapFont;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentSequencer;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSelectionBox;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInput;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInputSpeed;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import java.util.function.Function;

public class TransferFunctionInputSequencerPlayState
extends TransferFunctionInput {
    public static final TransferFunction.Serializer<TransferFunctionInputSequencerPlayState> SERIALIZER = new TransferFunction.Serializer<TransferFunctionInputSequencerPlayState>(){

        @Override
        public String typeId() {
            return "INPUT-SEQUENCER-PLAY-STATE";
        }

        @Override
        public String title() {
            return "In: Play State";
        }

        @Override
        public boolean isInput() {
            return true;
        }

        @Override
        public boolean isListed(TransferFunctionHost host) {
            return host.isSequencer();
        }

        @Override
        public TransferFunctionInputSequencerPlayState createNew(TransferFunctionHost host) {
            TransferFunctionInputSequencerPlayState function = new TransferFunctionInputSequencerPlayState(Mode.SPEED);
            function.updateSource(host);
            return function;
        }

        @Override
        public TransferFunctionInputSequencerPlayState load(TransferFunctionHost host, ConfigurationNode config) {
            Mode mode = (Mode)((Object)config.getOrDefault("mode", (Object)Mode.SPEED));
            TransferFunctionInputSequencerPlayState function = new TransferFunctionInputSequencerPlayState(mode);
            function.updateSource(host);
            return function;
        }

        @Override
        public void save(TransferFunctionHost host, ConfigurationNode config, TransferFunctionInputSequencerPlayState function) {
            config.set("mode", (Object)function.getMode());
        }
    };
    private Mode mode;

    public TransferFunctionInputSequencerPlayState(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public TransferFunction.Serializer<? extends TransferFunction> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public TransferFunctionInput.ReferencedSource createSource(TransferFunctionHost host) {
        Attachment attachment = host.getAttachment();
        if (attachment instanceof CartAttachmentSequencer) {
            return this.mode.createReferencedSource((CartAttachmentSequencer)attachment);
        }
        return TransferFunctionInput.ReferencedSource.NONE;
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
        view.draw(MapFont.MINECRAFT, 0, 3, (byte)30, (CharSequence)this.mode.previewTitle());
    }

    @Override
    public void openDialog(final TransferFunction.Dialog dialog) {
        super.openDialog(dialog);
        dialog.addWidget(new MapWidgetSelectionBox(this){
            private boolean loading = false;
            final /* synthetic */ TransferFunctionInputSequencerPlayState this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onAttached() {
                this.loading = true;
                for (Mode mode : Mode.values()) {
                    this.addItem(mode.title());
                    if (mode != this.this$0.mode) continue;
                    this.setSelectedIndex(this.getItemCount() - 1);
                }
                super.onAttached();
                this.loading = false;
            }

            @Override
            public void onSelectedItemChanged() {
                if (!this.loading && this.getSelectedIndex() >= 0 && this.getSelectedIndex() < Mode.values().length) {
                    this.this$0.setMode(Mode.values()[this.getSelectedIndex()]);
                    this.this$0.updateSource(dialog.getHost());
                    dialog.markChanged();
                }
            }
        }).setBounds(4, 18, dialog.getWidth() - 8, 11);
    }

    public static enum Mode {
        VOLUME("Volume", "<Play Volume>", EffectOptionsVolumeReferencedSource::new),
        SPEED("Speed", "<Play Speed>", EffectOptionsSpeedReferencedSource::new),
        PROGRESSION("Progression", "<Play Progress>", ProgressionReferencedSource::new);

        private final String title;
        private final String previewTitle;
        private final Function<CartAttachmentSequencer, TransferFunctionInput.ReferencedSource> sourceFactory;

        private Mode(String title, String previewTitle, Function<CartAttachmentSequencer, TransferFunctionInput.ReferencedSource> sourceFactory) {
            this.title = title;
            this.previewTitle = previewTitle;
            this.sourceFactory = sourceFactory;
        }

        public String title() {
            return this.title;
        }

        public String previewTitle() {
            return this.previewTitle;
        }

        public TransferFunctionInput.ReferencedSource createReferencedSource(CartAttachmentSequencer sequencer) {
            return this.sourceFactory.apply(sequencer);
        }
    }

    private static class ProgressionReferencedSource
    extends TransferFunctionInput.ReferencedSource {
        private final CartAttachmentSequencer sequencer;

        public ProgressionReferencedSource(CartAttachmentSequencer sequencer) {
            this.sequencer = sequencer;
        }

        @Override
        public void onTick() {
            this.value = this.sequencer.getProgression();
        }

        @Override
        public boolean isTickedDuringPlay() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ProgressionReferencedSource;
        }
    }

    private static class EffectOptionsSpeedReferencedSource
    extends TransferFunctionInput.ReferencedSource {
        private final CartAttachmentSequencer sequencer;

        public EffectOptionsSpeedReferencedSource(CartAttachmentSequencer sequencer) {
            this.sequencer = sequencer;
        }

        @Override
        public void onTick() {
            this.value = this.sequencer.getCurrentPlayOptions().speed();
        }

        @Override
        public boolean isTickedDuringPlay() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof EffectOptionsSpeedReferencedSource;
        }
    }

    private static class EffectOptionsVolumeReferencedSource
    extends TransferFunctionInput.ReferencedSource {
        private final CartAttachmentSequencer sequencer;

        public EffectOptionsVolumeReferencedSource(CartAttachmentSequencer sequencer) {
            this.sequencer = sequencer;
        }

        @Override
        public void onTick() {
            this.value = this.sequencer.getCurrentPlayOptions().volume();
        }

        @Override
        public boolean isTickedDuringPlay() {
            return true;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof EffectOptionsVolumeReferencedSource;
        }
    }
}

