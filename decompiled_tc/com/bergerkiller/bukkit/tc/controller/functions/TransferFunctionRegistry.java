/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.config.ConfigurationNode
 */
package com.bergerkiller.bukkit.tc.controller.functions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunction;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionBoolean;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionConditional;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionConstant;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionHost;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionIdentity;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionList;
import com.bergerkiller.bukkit.tc.controller.functions.TransferFunctionUnknown;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInputProperty;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInputSeatOccupied;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInputSequencerPlayState;
import com.bergerkiller.bukkit.tc.controller.functions.inputs.TransferFunctionInputSpeed;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class TransferFunctionRegistry {
    static final TransferFunctionRegistry INSTANCE = new TransferFunctionRegistry();
    private final Map<String, TransferFunction.Serializer<?>> byTypeId = new HashMap();
    private List<TransferFunction.Serializer<?>> values = Collections.emptyList();

    public TransferFunctionRegistry() {
        this.register(TransferFunctionConstant.SERIALIZER);
        this.register(TransferFunctionBoolean.SERIALIZER);
        this.register(TransferFunctionInputSpeed.SERIALIZER);
        this.register(TransferFunctionInputProperty.SERIALIZER);
        this.register(TransferFunctionInputSequencerPlayState.SERIALIZER);
        this.register(TransferFunctionInputSeatOccupied.SERIALIZER);
        this.register(TransferFunctionList.SERIALIZER);
        this.register(TransferFunctionIdentity.SERIALIZER);
        this.register(TransferFunctionConditional.SERIALIZER);
    }

    public List<TransferFunction.Serializer<?>> all() {
        return this.values;
    }

    public void register(TransferFunction.Serializer<?> serializer) {
        if (this.values.contains(serializer)) {
            return;
        }
        ArrayList newValues = new ArrayList(this.values);
        newValues.add(serializer);
        newValues.sort(Comparator.comparing(TransferFunction.Serializer::title));
        this.values = Collections.unmodifiableList(newValues);
        String id = serializer.typeId();
        this.byTypeId.put(id, serializer);
        this.byTypeId.put(id.toLowerCase(Locale.ENGLISH), serializer);
        this.byTypeId.put(id.toUpperCase(Locale.ENGLISH), serializer);
    }

    public void unregister(TransferFunction.Serializer<?> serializer) {
        int index = this.values.indexOf(serializer);
        if (index == -1) {
            return;
        }
        ArrayList newValues = new ArrayList(this.values);
        newValues.remove(index);
        this.values = Collections.unmodifiableList(newValues);
        String id = serializer.typeId();
        this.byTypeId.remove(id, serializer);
        this.byTypeId.remove(id.toLowerCase(Locale.ENGLISH), serializer);
        this.byTypeId.remove(id.toUpperCase(Locale.ENGLISH), serializer);
    }

    public TransferFunction load(TransferFunctionHost host, ConfigurationNode config) {
        String typeId = (String)config.getOrDefault("type", (Object)"");
        TransferFunction.Serializer<?> serializer = this.byTypeId.get(typeId);
        if (serializer == null) {
            serializer = this.byTypeId.get(typeId.toUpperCase(Locale.ENGLISH));
        }
        if (serializer == null) {
            return new TransferFunctionUnknown(typeId, config, false);
        }
        try {
            return serializer.load(host, config);
        }
        catch (Throwable t) {
            host.getTrainCarts().getLogger().log(Level.SEVERE, "Failed to load function of type " + typeId, t);
            return new TransferFunctionUnknown(typeId, config, true);
        }
    }

    public ConfigurationNode save(TransferFunctionHost host, TransferFunction function) {
        TransferFunction.Serializer<? extends TransferFunction> serializer = function.getSerializer();
        String typeId = "GET_TYPE_ID_FAILED";
        try {
            typeId = serializer.typeId();
            ConfigurationNode config = new ConfigurationNode();
            config.set("type", (Object)typeId);
            serializer.save(host, config, function);
            return config;
        }
        catch (Throwable t) {
            host.getTrainCarts().getLogger().log(Level.SEVERE, "Failed to save transfer function of type " + typeId, t);
            ConfigurationNode config = new ConfigurationNode();
            config.set("type", (Object)typeId);
            return config;
        }
    }
}

