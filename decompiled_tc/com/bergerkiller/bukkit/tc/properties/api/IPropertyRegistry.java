/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.CommonUtil
 */
package com.bergerkiller.bukkit.tc.properties.api;

import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.properties.IProperties;
import com.bergerkiller.bukkit.tc.properties.api.IProperty;
import com.bergerkiller.bukkit.tc.properties.api.IPropertyParser;
import com.bergerkiller.bukkit.tc.properties.api.PropertyParseResult;
import com.bergerkiller.bukkit.tc.properties.api.context.PropertyInputContext;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface IPropertyRegistry {
    public <T> Optional<IPropertyParser<T>> findParser(String var1);

    default public <T> Optional<IProperty<T>> find(String name) {
        return this.findParser(name).map(IPropertyParser::getProperty);
    }

    default public <T> PropertyParseResult<T> parse(IProperties properties, String name, String input) {
        return this.parse(properties, name, PropertyInputContext.of(input));
    }

    default public <T> PropertyParseResult<T> parse(IProperties properties, String name, PropertyInputContext inputContext) {
        Optional<IPropertyParser<T>> optParser = this.findParser(name);
        if (optParser.isPresent()) {
            return optParser.get().parse(properties, inputContext);
        }
        return PropertyParseResult.failPropertyNotFound(inputContext, name);
    }

    default public <T> PropertyParseResult<T> parseAndSet(IProperties properties, String name, String input) {
        return this.parseAndSet(properties, name, PropertyInputContext.of(input));
    }

    default public <T> PropertyParseResult<T> parseAndSet(IProperties properties, String name, String input, Consumer<PropertyParseResult<?>> beforeSet) {
        return this.parseAndSet(properties, name, PropertyInputContext.of(input).beforeSet(beforeSet));
    }

    default public <T> PropertyParseResult<T> parseAndSet(IProperties properties, String name, PropertyInputContext inputContext) {
        Optional<IPropertyParser<T>> optParser = this.findParser(name);
        if (optParser.isPresent()) {
            return optParser.get().parseAndSet(properties, inputContext);
        }
        return PropertyParseResult.failPropertyNotFound(inputContext, name);
    }

    public Collection<IProperty<Object>> all();

    public Map<String, IProperty<Object>> byListedName();

    public void register(IProperty<?> var1);

    public void unregister(IProperty<?> var1);

    default public void registerAll(Class<?> propertiesClass) {
        for (IProperty property : (IProperty[])CommonUtil.getClassConstants(propertiesClass, IProperty.class)) {
            this.register(property);
        }
    }

    default public void unregisterAll(Class<?> propertiesClass) {
        for (IProperty property : (IProperty[])CommonUtil.getClassConstants(propertiesClass, IProperty.class)) {
            this.unregister(property);
        }
    }

    public static IPropertyRegistry instance() {
        return TrainCarts.plugin.getPropertyRegistry();
    }
}

