/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.tc.commands.selector;

public final class SelectorHandlerConditionOption {
    private final String _name;

    private SelectorHandlerConditionOption(String name) {
        this._name = name;
    }

    public String name() {
        return this._name;
    }

    public static SelectorHandlerConditionOption optionString(String name) {
        return new SelectorHandlerConditionOption(name);
    }
}

