/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.events.map;

import com.bergerkiller.bukkit.common.utils.LogicUtil;

public class MapStatusEvent {
    private final String name;
    private final Object argument;

    public MapStatusEvent(String name, Object argument) {
        this.name = name;
        this.argument = argument;
    }

    public String getName() {
        return this.name;
    }

    public boolean isName(String name) {
        return this.name != null && this.name.equals(name);
    }

    public Object getArgument() {
        return this.argument;
    }

    public <T> T getArgument(Class<T> type) {
        return LogicUtil.tryCast(this.argument, type);
    }

    public String toString() {
        return "MapStatusEvent{name=" + this.name + ", arg=" + this.argument + "}";
    }
}

