/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.math.Matrix4x4
 */
package com.bergerkiller.bukkit.tc.controller.functions.inputs;

import com.bergerkiller.bukkit.common.math.Matrix4x4;
import com.bergerkiller.bukkit.tc.attachments.ui.MapWidgetSelectionBox;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public abstract class TransferFunctionInput
implements TransferFunction {
    private ReferencedSource source = ReferencedSource.NONE;

    public abstract ReferencedSource createSource(TransferFunctionHost var1);

    public final void updateSource(TransferFunctionHost host) {
        ReferencedSource newSource = this.createSource(host);
        if (!this.source.equals(newSource = host.registerInputSource(newSource))) {
            this.source.removeRecipient(this);
            this.source = newSource;
            newSource.addRecipient(this);
        }
    }

    @Override
    public double map(double unusedInput) {
        return this.source.value();
    }

    @Override
    public final boolean isBooleanOutput(BooleanSupplier isBooleanInput) {
        return this.isBooleanOutput();
    }

    public abstract boolean isBooleanOutput();

    @Override
    public final TransferFunctionInput clone() {
        TransferFunctionInput copy = this.cloneInput();
        if (!copy.source.equals(this.source)) {
            copy.source = this.source;
            copy.source.addRecipient(copy);
        }
        return copy;
    }

    protected abstract TransferFunctionInput cloneInput();

    @Override
    public void openDialog(final TransferFunction.Dialog dialog) {
        dialog.addWidget(new MapWidgetSelectionBox(this){
            private final List<TransferFunction.Serializer<?>> serializers = new ArrayList();
            private boolean loading = false;
            final /* synthetic */ TransferFunctionInput this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void onAttached() {
                this.serializers.clear();
                this.loading = true;
                for (TransferFunction.Serializer<?> serializer : dialog.getHost().getRegistry().all()) {
                    if (!serializer.isListed(dialog.getHost()) || !serializer.isInput()) continue;
                    this.serializers.add(serializer);
                    this.addItem(serializer.title());
                    if (serializer != this.this$0.getSerializer()) continue;
                    this.setSelectedIndex(this.getItemCount() - 1);
                }
                super.onAttached();
                this.loading = false;
                this.focus();
            }

            @Override
            public void onSelectedItemChanged() {
                if (!this.loading && this.getSelectedIndex() >= 0 && this.getSelectedIndex() < this.serializers.size()) {
                    TransferFunction.Serializer<?> newSerializer = this.serializers.get(this.getSelectedIndex());
                    dialog.setFunction((TransferFunction)newSerializer.createNew(dialog.getHost()));
                }
            }
        }).setBounds(4, 5, dialog.getWidth() - 8, 11);
    }

    public static abstract class ReferencedSource {
        public static final ReferencedSource NONE = new ReferencedSource(){

            @Override
            public boolean equals(Object o) {
                return this == o;
            }

            @Override
            public void addRecipient(TransferFunctionInput recipient) {
            }
        };
        protected double value = 0.0;
        private final List<WeakReference<TransferFunctionInput>> recipients = new ArrayList<WeakReference<TransferFunctionInput>>();

        public double value() {
            return this.value;
        }

        public void addRecipient(TransferFunctionInput recipient) {
            this.recipients.add(new WeakReference<TransferFunctionInput>(recipient));
        }

        public void removeRecipient(TransferFunctionInput recipient) {
            this.recipients.removeIf(ref -> {
                TransferFunctionInput input = (TransferFunctionInput)ref.get();
                return input == null || input == recipient;
            });
        }

        public boolean hasRecipients() {
            this.recipients.removeIf(ref -> ref.get() == null);
            return !this.recipients.isEmpty();
        }

        public void onTick() {
        }

        public boolean isTickedDuringPlay() {
            return false;
        }

        public void onTransform(Matrix4x4 transform) {
        }

        public abstract boolean equals(Object var1);
    }
}

