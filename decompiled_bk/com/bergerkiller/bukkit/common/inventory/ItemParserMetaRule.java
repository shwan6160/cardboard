/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.StringMapCaseInsensitive;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public final class ItemParserMetaRule {
    private static final Map<String, Function<CommonItemStack, Object>> valueExtractors = new StringMapCaseInsensitive<Function<CommonItemStack, Object>>();
    private final Function<CommonItemStack, Object> _valueExtractor;
    private String _name;
    private Operator _operator;
    private String _value;
    private boolean _valuePatternCheck;
    private Pattern _valuePattern;

    private ItemParserMetaRule(Function<CommonItemStack, Object> valueExtractor, String name, Operator operator, String value) {
        this._valueExtractor = valueExtractor;
        this._name = name;
        this._operator = operator;
        this._value = value;
        this._valuePatternCheck = false;
        this._valuePattern = null;
    }

    public String getName() {
        return this._name;
    }

    public Operator getOperator() {
        return this._operator;
    }

    public String getValue() {
        return this._value;
    }

    public final boolean match(CommonItemStack item) {
        Object value = this._valueExtractor.apply(item);
        if (value == null) {
            return "!=".equals((Object)this._operator);
        }
        if (this._value == null) {
            return this._operator == null || this._operator.equals("==") || this._operator.equals("=");
        }
        if (value instanceof Double || value instanceof Float) {
            try {
                double num_value = ((Number)value).doubleValue();
                double num_expect = value instanceof Double ? Double.parseDouble(this._value) : (double)Float.parseFloat(this._value);
                return this._operator.compare(num_value, num_expect);
            }
            catch (NumberFormatException ex) {
                return false;
            }
        }
        if (value instanceof Number) {
            try {
                long num_value = ((Number)value).longValue();
                long num_expect = Long.parseLong(this._value);
                return this._operator.compare(num_value, num_expect);
            }
            catch (NumberFormatException ex) {
                return false;
            }
        }
        if (value instanceof String) {
            boolean isNotEquals;
            String str_value = (String)value;
            boolean bl = isNotEquals = this._operator == Operator.NOT_EQUAL;
            if (isNotEquals || this._operator == Operator.EQUAL || this._operator == Operator.EQUAL_ALT) {
                if (!this._valuePatternCheck) {
                    this._valuePatternCheck = true;
                    this._valuePattern = null;
                    int wildcardIndex = this._value.indexOf(42);
                    if (wildcardIndex != -1) {
                        StringBuilder regexStr = new StringBuilder();
                        int startIndex = 0;
                        do {
                            if (wildcardIndex > startIndex) {
                                regexStr.append(Pattern.quote(this._value.substring(startIndex, wildcardIndex)));
                            }
                            if (wildcardIndex < this._value.length() - 1 && this._value.charAt(wildcardIndex + 1) == '*') {
                                regexStr.append("\\*\\*");
                                wildcardIndex += 2;
                                continue;
                            }
                            regexStr.append(".*");
                        } while ((wildcardIndex = this._value.indexOf(42, startIndex = ++wildcardIndex)) != -1);
                        regexStr.append(Pattern.quote(this._value.substring(startIndex)));
                        try {
                            this._valuePattern = Pattern.compile(regexStr.toString());
                        }
                        catch (PatternSyntaxException ex) {
                            Logging.LOGGER.log(Level.WARNING, "Failed to compile item parser meta regex pattern", ex);
                        }
                    }
                }
                boolean isValueEqual = this._valuePattern != null ? this._valuePattern.matcher(str_value).matches() : this._value.equals(str_value);
                return isValueEqual != isNotEquals;
            }
            return this._operator.compare(str_value, this._value);
        }
        return false;
    }

    public String toString() {
        if (this._value == null) {
            return this._name;
        }
        return this._name + (Object)((Object)this._operator) + this._value;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ItemParserMetaRule parse(String text) {
        boolean opposite = false;
        while (text.startsWith("!")) {
            opposite = !opposite;
            text = text.substring(1);
        }
        Operator operator = Operator.EQUAL;
        String value = null;
        for (Operator operatorCandidate : Operator.values()) {
            int index = text.indexOf(operatorCandidate.getOperatorName());
            if (index == -1) continue;
            operator = operatorCandidate;
            value = text.substring(index + operatorCandidate.getOperatorName().length());
            text = text.substring(0, index);
            break;
        }
        if (opposite) {
            operator = operator.opposite();
        }
        Function<CommonItemStack, Object> valueExtractor = null;
        Map<String, Function<CommonItemStack, Object>> map = valueExtractors;
        synchronized (map) {
            valueExtractor = valueExtractors.get(text);
        }
        if (valueExtractor == null) {
            String name = text;
            valueExtractor = item -> {
                if (item.hasCustomData()) {
                    return item.getCustomData().getValue(name);
                }
                return null;
            };
        }
        return new ItemParserMetaRule(valueExtractor, text, operator, value);
    }

    static {
        valueExtractors.put("damage", item -> item.isDamageSupported() ? Integer.valueOf(item.getDamage()) : null);
        valueExtractors.put("durability", item -> item.isDamageSupported() ? Integer.valueOf(item.getDamage()) : null);
        valueExtractors.put("name", item -> item.hasCustomName() ? item.getCustomNameMessage() : null);
        valueExtractors.put("custommodeldata", item -> item.hasCustomModelData() ? Integer.valueOf(item.getCustomModelData()) : null);
        valueExtractors.put("custom_model_data", item -> item.hasCustomModelData() ? Integer.valueOf(item.getCustomModelData()) : null);
        valueExtractors.put("map", item -> item.isFilledMap() ? Integer.valueOf(item.getFilledMapId()) : null);
    }

    public static enum Operator {
        GREATER_EQUAL_THAN(">=", c -> c >= 0),
        LESS_EQUAL_THAN("<=", c -> c <= 0),
        NOT_EQUAL("!=", c -> c != 0),
        EQUAL("==", c -> c == 0),
        EQUAL_ALT("=", c -> c == 0),
        GREATER_THAN(">", c -> c > 0),
        LESS_THAN("<", c -> c < 0);

        private final String name;
        private final ComparatorRule comparatorRule;
        private Operator opposite;

        private Operator(String name, ComparatorRule comparatorRule) {
            this.name = name;
            this.comparatorRule = comparatorRule;
        }

        public String getOperatorName() {
            return this.name;
        }

        public Operator opposite() {
            return this.opposite;
        }

        public boolean compare(double lhs, double rhs) {
            return this.comparatorRule.test(Double.compare(lhs, rhs));
        }

        public boolean compare(long lhs, long rhs) {
            return this.comparatorRule.test(Long.compare(lhs, rhs));
        }

        public boolean compare(String lhs, String rhs) {
            return this.comparatorRule.test(lhs.compareTo(rhs));
        }

        static {
            Operator.GREATER_EQUAL_THAN.opposite = LESS_THAN;
            Operator.LESS_EQUAL_THAN.opposite = GREATER_THAN;
            Operator.NOT_EQUAL.opposite = EQUAL;
            Operator.EQUAL.opposite = NOT_EQUAL;
            Operator.EQUAL_ALT.opposite = NOT_EQUAL;
            Operator.GREATER_THAN.opposite = LESS_EQUAL_THAN;
            Operator.LESS_THAN.opposite = GREATER_EQUAL_THAN;
        }

        @FunctionalInterface
        private static interface ComparatorRule {
            public boolean test(int var1);
        }
    }
}

