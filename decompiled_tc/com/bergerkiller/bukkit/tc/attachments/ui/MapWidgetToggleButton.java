/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 */
package com.bergerkiller.bukkit.tc.attachments.ui;

import com.bergerkiller.bukkit.common.map.widgets.MapWidgetButton;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class MapWidgetToggleButton<T>
extends MapWidgetButton {
    private final Map<T, String> _values = new LinkedHashMap<T, String>();
    private T _value = null;

    public abstract void onSelectionChanged();

    public MapWidgetToggleButton<T> addOption(T value, String text) {
        if (this._values.isEmpty()) {
            this._value = value;
            this.setText(text);
            if (this.getDisplay() != null) {
                this.onSelectionChanged();
            }
        }
        this._values.put(value, text);
        return this;
    }

    @SafeVarargs
    public final MapWidgetToggleButton<T> addOptions(Function<T, String> textFunction, T ... values) {
        for (T value : values) {
            this.addOption(value, textFunction.apply(value));
        }
        return this;
    }

    public final MapWidgetToggleButton<T> addOptions(Function<T, String> textFunction, Class<T> enumType) {
        return this.addOptions(textFunction, enumType.getEnumConstants());
    }

    public MapWidgetToggleButton<T> setSelectedOption(T value) {
        String text = this._values.get(value);
        if (text == null) {
            throw new IllegalArgumentException("Value " + value + " is not a valid option");
        }
        if (!LogicUtil.bothNullOrEqual(this._value, value)) {
            this._value = value;
            this.setText(text);
            if (this.getDisplay() != null) {
                this.onSelectionChanged();
            }
        }
        return this;
    }

    public T getSelectedOption() {
        return this._value;
    }

    public void nextOption() {
        if (this._values.size() > 1) {
            block3: {
                Iterator<Map.Entry<T, String>> iter = this._values.entrySet().iterator();
                do {
                    if (iter.hasNext()) continue;
                    Map.Entry<T, String> e = this._values.entrySet().iterator().next();
                    this._value = e.getKey();
                    this.setText(e.getValue());
                    break block3;
                } while (!iter.next().getKey().equals(this._value) || !iter.hasNext());
                Map.Entry<T, String> e = iter.next();
                this._value = e.getKey();
                this.setText(e.getValue());
            }
            if (this.getDisplay() != null) {
                this.onSelectionChanged();
            }
        }
    }

    public void onActivate() {
        this.nextOption();
    }
}

