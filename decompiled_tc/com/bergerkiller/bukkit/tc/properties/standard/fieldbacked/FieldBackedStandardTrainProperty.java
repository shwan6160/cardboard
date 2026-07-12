/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.properties.standard.fieldbacked;

import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.IDoubleProperty;
import com.bergerkiller.bukkit.tc.properties.api.ITrainProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;

public abstract class FieldBackedStandardTrainProperty<T>
extends FieldBackedProperty<T>
implements ITrainProperty<T> {
    public abstract T getData(FieldBackedProperty.TrainInternalData var1);

    public abstract void setData(FieldBackedProperty.TrainInternalData var1, T var2);

    @Override
    public void onConfigurationChanged(TrainProperties properties) {
        this.setData(FieldBackedProperty.TrainInternalData.get(properties), this.readFromConfig(properties.getConfig()).orElseGet(this::getDefault));
    }

    @Override
    public T get(TrainProperties properties) {
        return this.getData(FieldBackedProperty.TrainInternalData.get(properties));
    }

    @Override
    public void set(TrainProperties properties, T value) {
        ITrainProperty.super.set(properties, value);
        this.setData(FieldBackedProperty.TrainInternalData.get(properties), value);
    }

    public static abstract class StandardDouble
    extends FieldBackedStandardTrainProperty<Double>
    implements IDoubleProperty {
        @Override
        public abstract double getDoubleDefault();

        public abstract double getDoubleData(FieldBackedProperty.TrainInternalData var1);

        public abstract void setDoubleData(FieldBackedProperty.TrainInternalData var1, double var2);

        @Override
        public final double getDouble(TrainProperties properties) {
            return this.getDoubleData(FieldBackedProperty.TrainInternalData.get(properties));
        }

        @Override
        public final double getDouble(CartProperties properties) {
            return this.getDouble(properties.getTrainProperties());
        }

        @Override
        public Double getDefault() {
            return this.getDoubleDefault();
        }

        @Override
        public Double get(CartProperties properties) {
            return this.getDouble(properties);
        }

        @Override
        public Double get(TrainProperties properties) {
            return this.getDouble(properties);
        }

        @Override
        public Double getData(FieldBackedProperty.TrainInternalData holder) {
            return this.getDoubleData(holder);
        }

        @Override
        public void setData(FieldBackedProperty.TrainInternalData holder, Double value) {
            this.setDoubleData(holder, value);
        }
    }
}

