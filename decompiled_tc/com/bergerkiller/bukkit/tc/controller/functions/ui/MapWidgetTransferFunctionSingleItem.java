/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidget
 */
package com.bergerkiller.bukkit.tc.controller.functions.ui;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.common.map.widgets.MapWidget;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionItem;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionTypeSelectorDialog;
import java.util.function.BooleanSupplier;

public abstract class MapWidgetTransferFunctionSingleItem
extends MapWidgetTransferFunctionItem {
    private boolean functionWasDefault = false;
    private boolean ignoreChanges = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MapWidgetTransferFunctionSingleItem(TransferFunctionHost host, ConfigurationNode functionConfig, BooleanSupplier isBooleanInput) {
        super(host, TransferFunction.Holder.of(functionConfig != null ? host.loadFunction(functionConfig) : TransferFunction.identity()), isBooleanInput);
        if (functionConfig == null) {
            this.ignoreChanges = true;
            try {
                this.function.setFunction(this.createDefault(), true);
            }
            finally {
                this.ignoreChanges = false;
            }
        }
        this.updateButtons();
    }

    public MapWidgetTransferFunctionSingleItem(TransferFunctionHost host, TransferFunction.Holder<TransferFunction> function, BooleanSupplier isBooleanInput) {
        super(host, function, isBooleanInput);
        this.updateButtons();
    }

    public abstract void onChanged(TransferFunction.Holder<TransferFunction> var1);

    public abstract TransferFunction createDefault();

    @Override
    protected void onChangedInternal(TransferFunction.Holder<TransferFunction> function) {
        if (this.ignoreChanges) {
            return;
        }
        this.updateButtons();
        this.onChanged(function);
    }

    protected void updateButtons() {
        if (!this.buttons.isEmpty() && this.functionWasDefault == this.function.isDefault()) {
            return;
        }
        this.functionWasDefault = this.function.isDefault();
        this.updateButtons(item -> {
            item.addConfigureButton();
            if (this.function.isDefault()) {
                item.addButton(MapWidgetTransferFunctionItem.ButtonIcon.ADD, () -> this.getParent().addWidget((MapWidget)new MapWidgetTransferFunctionTypeSelectorDialog(this.host){

                    @Override
                    public void onSelected(TransferFunction function) {
                        MapWidgetTransferFunctionSingleItem.this.function.setFunction(function);
                        MapWidgetTransferFunctionSingleItem.this.focus();
                    }
                }));
            } else {
                item.addButton(MapWidgetTransferFunctionItem.ButtonIcon.REMOVE, () -> this.function.setFunction(this.createDefault(), true));
            }
        });
    }
}

