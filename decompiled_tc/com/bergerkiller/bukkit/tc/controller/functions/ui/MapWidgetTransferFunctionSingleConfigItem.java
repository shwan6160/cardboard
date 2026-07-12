/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 */
package com.bergerkiller.bukkit.tc.controller.functions.ui;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.ui.MapWidgetTransferFunctionSingleItem;
import java.util.function.BooleanSupplier;

public abstract class MapWidgetTransferFunctionSingleConfigItem
extends MapWidgetTransferFunctionSingleItem {
    private final ConfigurationNode config;
    private final String configKey;

    public MapWidgetTransferFunctionSingleConfigItem(TransferFunctionHost host, ConfigurationNode config, String configKey, BooleanSupplier isBooleanInput) {
        super(host, config.getNodeIfExists(configKey), isBooleanInput);
        this.config = config;
        this.configKey = configKey;
    }

    @Override
    public void onChanged(TransferFunction.Holder<TransferFunction> function) {
        if (function.isDefault()) {
            this.config.remove(this.configKey);
        } else {
            this.config.set(this.configKey, (Object)this.host.saveFunction(function.getFunction()));
        }
    }
}

