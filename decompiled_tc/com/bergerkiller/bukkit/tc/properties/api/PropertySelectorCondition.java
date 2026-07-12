/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.properties.api;

import com.bergerkiller.bukkit.tc.properties.api.PropertySelectorConditionList;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Repeatable(value=PropertySelectorConditionList.class)
public @interface PropertySelectorCondition {
    public String value();
}

