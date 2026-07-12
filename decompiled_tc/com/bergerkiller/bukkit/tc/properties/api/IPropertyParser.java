/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 */
package com.bergerkiller.bukkit.tc.properties.api;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParseResult;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyInputContext;
import java.util.Iterator;
import java.util.function.Consumer;

public interface IPropertyParser<T> {
    public IProperty<T> getProperty();

    public String getName();

    public boolean isInputPreProcessed();

    public boolean isProcessedPerCart();

    default public PropertyParseResult<T> parse(IProperties properties, String input) {
        return this.parse(properties, PropertyInputContext.of(input));
    }

    public PropertyParseResult<T> parse(IProperties var1, PropertyInputContext var2);

    default public PropertyParseResult<T> parseAndSet(IProperties properties, String input) {
        return this.parseAndSet(properties, input, LogicUtil.noopConsumer());
    }

    default public PropertyParseResult<T> parseAndSet(IProperties properties, String input, Consumer<PropertyParseResult<?>> beforeSet) {
        return this.parseAndSet(properties, PropertyInputContext.of(input).beforeSet(beforeSet));
    }

    default public PropertyParseResult<T> parseAndSet(IProperties properties, PropertyInputContext inputContext) {
        if (!this.isProcessedPerCart() || !(properties instanceof TrainProperties)) {
            PropertyParseResult<T> result = inputContext.handleBeforeSet(this.parse(properties, inputContext));
            if (result.isSuccessful()) {
                properties.set(this.getProperty(), result.getValue());
            }
            return result;
        }
        TrainProperties trainProperties = (TrainProperties)properties;
        if (trainProperties.isEmpty()) {
            return this.parse(properties, inputContext);
        }
        boolean successful = false;
        String name = inputContext.input();
        Iterator cartIter = trainProperties.iterator();
        CartProperties cartProp = (CartProperties)cartIter.next();
        PropertyParseResult<T> result = inputContext.handleBeforeSet(this.parse((IProperties)cartProp, inputContext));
        if (result.isSuccessful()) {
            cartProp.set(this.getProperty(), result.getValue());
            name = result.getName();
            successful = true;
        }
        while (cartIter.hasNext()) {
            cartProp = (CartProperties)cartIter.next();
            PropertyParseResult<T> cartResult = inputContext.handleBeforeSet(this.parse((IProperties)cartProp, inputContext));
            if (!cartResult.isSuccessful()) continue;
            cartProp.set(this.getProperty(), cartResult.getValue());
            name = result.getName();
            successful = true;
        }
        if (successful) {
            result = PropertyParseResult.success(inputContext, this.getProperty(), name, properties.get(this.getProperty()));
        }
        return result;
    }
}

