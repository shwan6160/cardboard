/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;

public class TypedValue<T> {
    public final Class<T> type;
    public T value;

    public TypedValue(T value) {
        this(value.getClass(), value);
    }

    public TypedValue(Class<T> type, T value) {
        this.type = type;
        this.value = value;
    }

    public void parseSet(String text) {
        this.value = this.type == Boolean.TYPE || this.type == Boolean.class ? LogicUtil.unsafeCast(ParseUtil.parseBool(text)) : Conversion.convert(text, this.type, this.value);
    }

    public String toString() {
        if (this.type == Boolean.TYPE || this.type == Boolean.class) {
            return this.value == Boolean.TRUE ? "true" : "false";
        }
        return Conversion.toString.convert(this.value, "null");
    }
}

