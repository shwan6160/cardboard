/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.LogicUtil
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 */
package com.bergerkiller.bukkit.tc.utils;

import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import java.util.Locale;

public class FormattedSpeed {
    public static final FormattedSpeed ZERO = FormattedSpeed.of(0.0);
    private final double _value;
    private final boolean _relative;
    private final double _unitMultiplier;
    private final String _unitName;

    public FormattedSpeed(double value, boolean isRelative, double unitMultiplier, String unitName) {
        this._value = value;
        this._relative = isRelative;
        this._unitMultiplier = unitMultiplier;
        this._unitName = unitName;
    }

    public double getValue() {
        return this._value;
    }

    public boolean isRelative() {
        return this._relative;
    }

    public double getUnitMultiplier() {
        return this._unitMultiplier;
    }

    public String getUnitName() {
        return this._unitName;
    }

    public static FormattedSpeed of(double value) {
        return new FormattedSpeed(value, false, 1.0, "b/t");
    }

    public static FormattedSpeed parse(String velocityString, FormattedSpeed defaultValue) {
        String numberText = velocityString;
        String unitText = "";
        for (int i = 0; i < velocityString.length(); ++i) {
            char c = velocityString.charAt(i);
            if (Character.isDigit(c) || c == '.' || c == ',' || c == ' ' || c == '-' || c == '+') continue;
            numberText = velocityString.substring(0, i);
            unitText = velocityString.substring(i).replace(" ", "").trim().toLowerCase(Locale.ENGLISH);
            break;
        }
        boolean relative = numberText.startsWith("-") || numberText.startsWith("+");
        double value = ParseUtil.parseDouble((String)numberText, (double)Double.NaN);
        if (Double.isNaN(value)) {
            return defaultValue;
        }
        double unitMultiplier = 1.0;
        if (unitText.length() >= 3) {
            if (unitText.equals("mph") || unitText.equals("mphr")) {
                unitText = "mi/h";
            } else if (LogicUtil.contains((Object)unitText, (Object[])new String[]{"kmh", "kmph", "kmphr"})) {
                unitText = "km/h";
            }
            int slashIndex = unitText.indexOf(47, 1);
            if (slashIndex != -1) {
                String num = unitText.substring(0, slashIndex);
                String den = unitText.substring(slashIndex + 1);
                if (num.equals("k") || num.equals("km")) {
                    unitMultiplier *= 1000.0;
                } else if (num.equals("mi")) {
                    unitMultiplier *= 1609.344;
                } else if (num.equals("ft")) {
                    unitMultiplier *= 0.3048780487804878;
                }
                if (LogicUtil.contains((Object)den, (Object[])new String[]{"s", "sec", "second"})) {
                    unitMultiplier /= 20.0;
                } else if (LogicUtil.contains((Object)den, (Object[])new String[]{"h", "hr", "hour"})) {
                    unitMultiplier /= 72000.0;
                }
            }
            value *= unitMultiplier;
        }
        return new FormattedSpeed(value, relative, unitMultiplier, unitText);
    }
}

