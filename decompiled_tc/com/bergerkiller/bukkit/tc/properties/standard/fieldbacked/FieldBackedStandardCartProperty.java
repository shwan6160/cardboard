/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.properties.standard.fieldbacked;

import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.ICartProperty;
import com.bergerkiller.bukkit.tc.properties.api.IDoubleProperty;
import com.bergerkiller.bukkit.tc.properties.standard.fieldbacked.FieldBackedProperty;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class FieldBackedStandardCartProperty<T>
extends FieldBackedProperty<T>
implements ICartProperty<T> {
    public abstract T getData(FieldBackedProperty.CartInternalData var1);

    public abstract void setData(FieldBackedProperty.CartInternalData var1, T var2);

    @Override
    public void onConfigurationChanged(CartProperties properties) {
        this.setData(FieldBackedProperty.CartInternalData.get(properties), this.readFromConfig(properties.getConfig()).orElseGet(this::getDefault));
    }

    @Override
    public T get(CartProperties properties) {
        return this.getData(FieldBackedProperty.CartInternalData.get(properties));
    }

    @Override
    public void set(CartProperties properties, T value) {
        ICartProperty.super.set(properties, value);
        this.setData(FieldBackedProperty.CartInternalData.get(properties), value);
    }

    public static Set<String> combineCartValues(TrainProperties properties, FieldBackedStandardCartProperty<Set<String>> property) {
        if (properties.size() == 1) {
            return property.get(properties.get(0));
        }
        HashSet result = new HashSet();
        for (CartProperties cprop : properties) {
            result.addAll(property.get(cprop));
        }
        return Collections.unmodifiableSet(result);
    }

    public static abstract class StandardDouble
    extends FieldBackedStandardCartProperty<Double>
    implements IDoubleProperty {
        @Override
        public abstract double getDoubleDefault();

        public abstract double getDataDouble(FieldBackedProperty.CartInternalData var1);

        public abstract void setDataDouble(FieldBackedProperty.CartInternalData var1, double var2);

        @Override
        public final double getDouble(CartProperties properties) {
            return this.getDataDouble(FieldBackedProperty.CartInternalData.get(properties));
        }

        @Override
        public final double getDouble(TrainProperties properties) {
            if (properties.isEmpty()) {
                return this.getDoubleDefault();
            }
            return this.getDouble(properties.get(0));
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
        public Double getData(FieldBackedProperty.CartInternalData holder) {
            return this.getDataDouble(holder);
        }

        @Override
        public void setData(FieldBackedProperty.CartInternalData holder, Double value) {
            this.setDataDouble(holder, value);
        }
    }
}

