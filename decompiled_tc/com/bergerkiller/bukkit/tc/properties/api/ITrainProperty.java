/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.properties.api;

import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import java.util.Optional;

public interface ITrainProperty<T>
extends IProperty<T> {
    @Override
    default public T get(TrainProperties properties) {
        return (T)this.readFromConfig(properties.getConfig()).orElseGet(this::getDefault);
    }

    @Override
    default public T get(CartProperties properties) {
        return this.get(properties.getTrainProperties());
    }

    @Override
    default public void set(CartProperties properties, T value) {
        this.set(properties.getTrainProperties(), value);
    }

    @Override
    default public void set(TrainProperties properties, T value) {
        if (value == null || value.equals(this.getDefault())) {
            this.writeToConfig(properties.getConfig(), Optional.empty());
        } else {
            this.writeToConfig(properties.getConfig(), Optional.of(value));
        }
        properties.tryUpdate();
    }
}

