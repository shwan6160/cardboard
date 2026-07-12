/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 */
package com.bergerkiller.bukkit.tc.properties.api.context;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParseResult;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class PropertyInputContext {
    private final String input;
    private UnaryOperator<PropertyParseResult<?>> beforeSet;
    private SignActionEvent signEvent;
    private boolean hasParsedStatements;

    protected PropertyInputContext(String input) {
        this.input = input;
        this.beforeSet = UnaryOperator.identity();
        this.signEvent = null;
        this.hasParsedStatements = false;
    }

    public String input() {
        return this.input;
    }

    public PropertyInputContext beforeSet(Consumer<PropertyParseResult<?>> callback) {
        return this.beforeSet(input -> {
            callback.accept((PropertyParseResult<?>)input);
            return input;
        });
    }

    public <T> PropertyInputContext beforeSet(UnaryOperator<PropertyParseResult<T>> function) {
        this.beforeSet = (UnaryOperator)CommonUtil.unsafeCast(function);
        return this;
    }

    public <T> PropertyParseResult<T> handleBeforeSet(PropertyParseResult<T> result) {
        return result.isSuccessful() ? (PropertyParseResult)CommonUtil.unsafeCast(this.beforeSet.apply(result)) : result;
    }

    public PropertyInputContext signEvent(SignActionEvent event) {
        this.signEvent = event;
        return this;
    }

    public SignActionEvent signEvent() {
        return this.signEvent;
    }

    public boolean hasParsedStatements() {
        return this.hasParsedStatements;
    }

    void setHasParsedStatements(boolean state) {
        this.hasParsedStatements = state;
    }

    public static PropertyInputContext of(String input) {
        return new PropertyInputContext(input);
    }
}

