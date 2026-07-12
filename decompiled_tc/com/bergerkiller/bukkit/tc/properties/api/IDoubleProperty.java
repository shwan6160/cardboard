/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.properties.api;

import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;

public interface IDoubleProperty
extends IProperty<Double> {
    public double getDoubleDefault();

    public double getDouble(CartProperties var1);

    public double getDouble(TrainProperties var1);

    @Override
    default public Double getDefault() {
        return this.getDoubleDefault();
    }

    @Override
    default public Double get(CartProperties properties) {
        return this.getDouble(properties);
    }

    @Override
    default public Double get(TrainProperties properties) {
        return this.getDouble(properties);
    }
}

