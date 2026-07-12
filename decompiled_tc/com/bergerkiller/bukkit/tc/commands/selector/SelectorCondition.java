/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.bergerkiller.bukkit.common.utils.ParseUtil
 */
package com.bergerkiller.bukkit.tc.commands.selector;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.commands.selector.SelectorException;
import com.bergerkiller.bukkit.tc.utils.BoundingRange;
import com.bergerkiller.bukkit.tc.utils.QuoteEscapedString;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectorCondition {
    private final Key key;
    private final String value;

    @Deprecated
    protected SelectorCondition(String key, String value) {
        this(Key.parse(key), value);
    }

    protected SelectorCondition(Key key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key.name();
    }

    public String getKeyPath() {
        return this.key.path();
    }

    public boolean hasKeyPath() {
        return !this.key.path().isEmpty();
    }

    public String getValue() {
        return this.value;
    }

    public boolean matchesAnyText(Collection<String> values) throws SelectorException {
        return values.contains(this.value);
    }

    public boolean matchesAnyText(Stream<String> values) throws SelectorException {
        return values.anyMatch(Predicate.isEqual(this.value));
    }

    public boolean matchesText(String value) throws SelectorException {
        return this.value.equals(value);
    }

    public BoundingRange getBoundingRange() throws SelectorException {
        throw new SelectorException(this.key + " value is not a number");
    }

    public double getDouble() throws SelectorException {
        BoundingRange range = this.getBoundingRange();
        if (range.isZeroLength()) {
            return range.getMin();
        }
        throw new SelectorException(this.key + " value is a range, expected a single number");
    }

    public boolean getBoolean() throws SelectorException {
        throw new SelectorException(this.key + " value is not a boolean");
    }

    public boolean matchesNumber(double value) throws SelectorException {
        throw new SelectorException(this.key + " value is not a number");
    }

    public boolean matchesNumber(long value) throws SelectorException {
        throw new SelectorException(this.key + " value is not a number");
    }

    public boolean matchesBoolean(boolean value) throws SelectorException {
        throw new SelectorException(this.key + " value is not a boolean flag");
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public static SelectorCondition parse(String key, String value) {
        return SelectorCondition.parse(Key.parse(key), value);
    }

    public static SelectorCondition parse(Key key, String value) {
        if (value.startsWith("!")) {
            SelectorCondition base = SelectorCondition.parse(key, value.substring(1));
            return new SelectorConditionInverted(key, value, base);
        }
        int rangeStart = QuoteEscapedString.unquotedIndexOf(value, "..", 0);
        if (rangeStart != -1) {
            int rangeNext;
            int rangeCurrent = QuoteEscapedString.unquotedIndexOf(value, "..", rangeStart + 2);
            if (rangeCurrent == -1) {
                SelectorConditionNumeric max;
                String first = rangeStart > 0 ? value.substring(0, rangeStart).trim() : null;
                String second = rangeStart + 2 < value.length() ? value.substring(rangeStart + 2).trim() : null;
                SelectorConditionNumeric min = first != null ? SelectorConditionNumeric.tryParse(key, first) : SelectorConditionNumeric.RANGE_MIN;
                SelectorConditionNumeric selectorConditionNumeric = max = second != null ? SelectorConditionNumeric.tryParse(key, second) : SelectorConditionNumeric.RANGE_MAX;
                if (min != null && max != null) {
                    return new SelectorConditionNumericRange(key, value, min, max);
                }
                return new SelectorConditionAnyOfText(key, value, SelectorCondition.parsePart(key, first), SelectorCondition.parsePart(key, second));
            }
            ArrayList<SelectorCondition> selectorValues = new ArrayList<SelectorCondition>(5);
            if (rangeStart > 0) {
                selectorValues.add(SelectorCondition.parsePart(key, value.substring(0, rangeStart).trim()));
            }
            if (rangeCurrent > rangeStart + 2) {
                selectorValues.add(SelectorCondition.parsePart(key, value.substring(rangeStart + 2, rangeCurrent).trim()));
            }
            while ((rangeNext = QuoteEscapedString.unquotedIndexOf(value, "..", rangeCurrent + 2)) != -1) {
                if (rangeNext > rangeCurrent + 2) {
                    selectorValues.add(SelectorCondition.parsePart(key, value.substring(rangeCurrent, rangeNext).trim()));
                }
                rangeCurrent = rangeNext;
            }
            if (rangeCurrent + 2 < value.length()) {
                selectorValues.add(SelectorCondition.parsePart(key, value.substring(rangeCurrent + 2).trim()));
            }
            return new SelectorConditionAnyOfText(key, value, selectorValues.toArray(new SelectorCondition[0]));
        }
        return SelectorCondition.parsePart(key, value.trim());
    }

    private static SelectorCondition parsePart(Key key, String value) {
        SelectorConditionBoolean truthy;
        SelectorConditionNumeric numeric;
        QuoteEscapedString unescapedValue = QuoteEscapedString.tryParseQuoted(value);
        value = unescapedValue.getUnescaped();
        if (!unescapedValue.isQuoteEscaped() && ParseUtil.isNumeric((String)value) && (numeric = SelectorConditionNumeric.tryParse(key, value)) != null) {
            return numeric;
        }
        String[] elements = value.split("\\*", -1);
        if (elements.length > 1) {
            boolean firstAny = value.startsWith("*");
            boolean lastAny = value.endsWith("*");
            return new SelectorConditionWildcardText(key, value, elements, firstAny, lastAny);
        }
        if (!unescapedValue.isQuoteEscaped() && (truthy = SelectorConditionBoolean.tryParse(key, value)) != null) {
            return truthy;
        }
        return new SelectorCondition(key, value);
    }

    public static List<SelectorCondition> parseAll(String conditionsString) {
        int separator = QuoteEscapedString.unquotedIndexOf(conditionsString, ",", 0);
        int length = conditionsString.length();
        if (separator == -1) {
            int equals = QuoteEscapedString.unquotedIndexOf(conditionsString, "=", 0);
            if (equals == -1 || equals == 0 || equals == length - 1) {
                return null;
            }
            Key condKey = Key.parse(conditionsString.substring(0, equals));
            String condValue = conditionsString.substring(equals + 1);
            return Collections.singletonList(SelectorCondition.parse(condKey, condValue));
        }
        ArrayList<SelectorCondition> conditions = new ArrayList<SelectorCondition>(10);
        int argStart = 0;
        int argEnd = separator;
        boolean valid = true;
        while (true) {
            int equals;
            if ((equals = QuoteEscapedString.unquotedIndexOf(conditionsString, "=", argStart)) == -1 || equals == argStart || equals >= argEnd - 1) {
                valid = false;
                break;
            }
            Key condKey = Key.parse(conditionsString.substring(argStart, equals));
            String condValue = conditionsString.substring(equals + 1, argEnd);
            conditions.add(SelectorCondition.parse(condKey, condValue));
            if (argEnd == length) break;
            argStart = argEnd + 1;
            if ((argEnd = QuoteEscapedString.unquotedIndexOf(conditionsString, ",", argEnd + 1)) != -1) continue;
            argEnd = length;
        }
        if (!valid) {
            return null;
        }
        return conditions;
    }

    public static final class Key {
        private final String name;
        private final String path;

        public static Key parse(String keyStr) {
            keyStr = keyStr.trim();
            QuoteEscapedString unescapedKey = QuoteEscapedString.tryParseQuoted(keyStr);
            keyStr = unescapedKey.getUnescaped();
            String keyPathStr = "";
            int keyPathStart = QuoteEscapedString.unquotedIndexOf(keyStr, ".", 0);
            if (keyPathStart != -1) {
                keyPathStr = keyStr.substring(keyPathStart + 1);
                keyStr = keyStr.substring(0, keyPathStart);
                if (!unescapedKey.isQuoteEscaped()) {
                    keyStr = QuoteEscapedString.tryParseQuoted(keyStr.trim()).getUnescaped();
                }
                if (!unescapedKey.isQuoteEscaped()) {
                    keyPathStr = QuoteEscapedString.tryParseQuoted(keyPathStr.trim()).getUnescaped();
                }
            }
            return Key.of(keyStr, keyPathStr);
        }

        public static Key of(String name) {
            return new Key(name);
        }

        public static Key of(String name, String path) {
            return new Key(name, path);
        }

        private Key(String name) {
            this(name, "");
        }

        private Key(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String name() {
            return this.name;
        }

        public String path() {
            return this.path;
        }

        public int hashCode() {
            return 31 * this.name.hashCode() + this.path.hashCode();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Key) {
                Key other = (Key)o;
                return this.name.equals(other.name) && this.path.equals(other.path);
            }
            return false;
        }

        public String toString() {
            if (this.path.isEmpty()) {
                return "Key{name=" + this.name + "}";
            }
            return "Key{name=" + this.name + ", path=" + this.path + "}";
        }
    }

    public static class SelectorConditionInverted
    extends SelectorCondition {
        private final SelectorCondition base;

        @Deprecated
        public SelectorConditionInverted(String key, String value, SelectorCondition base) {
            this(Key.parse(key), value, base);
        }

        public SelectorConditionInverted(Key key, String value, SelectorCondition base) {
            super(key, value);
            this.base = base;
        }

        @Override
        public boolean matchesAnyText(Collection<String> values) throws SelectorException {
            return !this.base.matchesAnyText(values);
        }

        @Override
        public boolean matchesAnyText(Stream<String> values) throws SelectorException {
            return !this.base.matchesAnyText(values);
        }

        @Override
        public boolean matchesText(String value) throws SelectorException {
            return !this.base.matchesText(value);
        }

        @Override
        public BoundingRange getBoundingRange() throws SelectorException {
            return this.base.getBoundingRange().invert();
        }

        @Override
        public boolean matchesNumber(double value) throws SelectorException {
            return !this.base.matchesNumber(value);
        }

        @Override
        public boolean matchesNumber(long value) throws SelectorException {
            return !this.base.matchesNumber(value);
        }

        @Override
        public boolean matchesBoolean(boolean value) throws SelectorException {
            return !this.base.matchesBoolean(value);
        }

        @Override
        public boolean isNumber() {
            return this.base.isNumber();
        }
    }

    private static class SelectorConditionNumeric
    extends SelectorCondition {
        public static final SelectorConditionNumeric RANGE_MIN = new SelectorConditionNumeric(Key.of("NONE"), "", Double.NEGATIVE_INFINITY, Long.MIN_VALUE);
        public static final SelectorConditionNumeric RANGE_MAX = new SelectorConditionNumeric(Key.of("NONE"), "", Double.POSITIVE_INFINITY, Long.MAX_VALUE);
        public final double valueDouble;
        public final long valueLong;

        @Deprecated
        public SelectorConditionNumeric(String key, String value, double valueDouble, long valueLong) {
            this(Key.parse(key), value, valueDouble, valueLong);
        }

        public SelectorConditionNumeric(Key key, String value, double valueDouble, long valueLong) {
            super(key, value);
            this.valueDouble = valueDouble;
            this.valueLong = valueLong;
        }

        @Override
        public BoundingRange getBoundingRange() throws SelectorException {
            return BoundingRange.create(this.valueDouble, this.valueDouble);
        }

        @Override
        public boolean matchesNumber(double value) throws SelectorException {
            return value == this.valueDouble;
        }

        @Override
        public boolean matchesNumber(long value) throws SelectorException {
            return value == this.valueLong;
        }

        @Override
        public boolean matchesBoolean(boolean value) throws SelectorException {
            return this.getBoolean() == value;
        }

        @Override
        public boolean getBoolean() throws SelectorException {
            if (this.valueDouble == 0.0) {
                return false;
            }
            if (this.valueDouble == 1.0) {
                return true;
            }
            throw new SelectorException(this.getKey() + " value is not a boolean (0, 1, true, etc.)");
        }

        @Override
        public boolean isNumber() {
            return true;
        }

        @Override
        public boolean isBoolean() {
            return this.valueDouble == 0.0 || this.valueDouble == 1.0;
        }

        @Deprecated
        public static SelectorConditionNumeric tryParse(String key, String value) {
            return SelectorConditionNumeric.tryParse(Key.parse(key), value);
        }

        public static SelectorConditionNumeric tryParse(Key key, String value) {
            double valueDouble = ParseUtil.parseDouble((String)value, (double)Double.NaN);
            if (!Double.isNaN(valueDouble)) {
                long valueLong = ParseUtil.parseLong((String)value, (long)0L);
                return new SelectorConditionNumeric(key, value, valueDouble, valueLong);
            }
            return null;
        }
    }

    private static class SelectorConditionNumericRange
    extends SelectorCondition {
        private final SelectorConditionNumeric min;
        private final SelectorConditionNumeric max;

        @Deprecated
        public SelectorConditionNumericRange(String key, String value, SelectorConditionNumeric min, SelectorConditionNumeric max) {
            this(Key.parse(key), value, min, max);
        }

        public SelectorConditionNumericRange(Key key, String value, SelectorConditionNumeric min, SelectorConditionNumeric max) {
            super(key, value);
            if (min.valueDouble > max.valueDouble) {
                this.min = max;
                this.max = min;
            } else {
                this.min = min;
                this.max = max;
            }
        }

        @Override
        public boolean matchesAnyText(Collection<String> values) throws SelectorException {
            return this.min.matchesAnyText(values) || this.max.matchesAnyText(values);
        }

        @Override
        public boolean matchesAnyText(Stream<String> values) throws SelectorException {
            Collection tmp = values.collect(Collectors.toList());
            return this.min.matchesAnyText(tmp) || this.max.matchesAnyText(tmp);
        }

        @Override
        public boolean matchesText(String value) throws SelectorException {
            return this.min.matchesText(value) || this.max.matchesText(value);
        }

        @Override
        public BoundingRange getBoundingRange() throws SelectorException {
            return BoundingRange.create(this.min.valueDouble, this.max.valueDouble);
        }

        @Override
        public boolean matchesNumber(double value) throws SelectorException {
            return value >= this.min.valueDouble && value <= this.max.valueDouble;
        }

        @Override
        public boolean matchesNumber(long value) throws SelectorException {
            return value >= this.min.valueLong && value <= this.max.valueLong;
        }

        @Override
        public boolean isNumber() {
            return true;
        }
    }

    private static class SelectorConditionAnyOfText
    extends SelectorCondition {
        private final SelectorCondition[] selectorValues;

        @Deprecated
        public SelectorConditionAnyOfText(String key, String value, SelectorCondition ... selectorValues) {
            this(Key.parse(key), value, selectorValues);
        }

        public SelectorConditionAnyOfText(Key key, String value, SelectorCondition ... selectorValues) {
            super(key, value);
            this.selectorValues = selectorValues;
        }

        @Override
        public boolean matchesAnyText(Collection<String> values) throws SelectorException {
            for (SelectorCondition selectorValue : this.selectorValues) {
                if (!selectorValue.matchesAnyText(values)) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean matchesAnyText(Stream<String> values) throws SelectorException {
            return values.anyMatch(s -> {
                for (SelectorCondition selectorValue : this.selectorValues) {
                    if (!selectorValue.matchesText((String)s)) continue;
                    return true;
                }
                return false;
            });
        }

        @Override
        public boolean matchesText(String value) throws SelectorException {
            for (SelectorCondition selectorValue : this.selectorValues) {
                if (!selectorValue.matchesText(value)) continue;
                return true;
            }
            return false;
        }

        @Override
        public BoundingRange getBoundingRange() throws SelectorException {
            double min = Double.MAX_VALUE;
            double max = -1.7976931348623157E308;
            for (SelectorCondition selectorValue : this.selectorValues) {
                double value = selectorValue.getBoundingRange().getMin();
                if (value < min) {
                    min = value;
                }
                if (!(value > max)) continue;
                max = value;
            }
            return BoundingRange.create(min, max);
        }

        @Override
        public boolean matchesNumber(double value) throws SelectorException {
            for (SelectorCondition selectorValue : this.selectorValues) {
                if (!selectorValue.matchesNumber(value)) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean matchesNumber(long value) throws SelectorException {
            for (SelectorCondition selectorValue : this.selectorValues) {
                if (!selectorValue.matchesNumber(value)) continue;
                return true;
            }
            return false;
        }
    }

    public static class SelectorConditionWildcardText
    extends SelectorCondition {
        private final String[] elements;
        private final boolean firstAny;
        private final boolean lastAny;

        @Deprecated
        public SelectorConditionWildcardText(String key, String value, String[] elements, boolean firstAny, boolean lastAny) {
            this(Key.parse(key), value, elements, firstAny, lastAny);
        }

        public SelectorConditionWildcardText(Key key, String value, String[] elements, boolean firstAny, boolean lastAny) {
            super(key, value);
            this.elements = elements;
            this.firstAny = firstAny;
            this.lastAny = lastAny;
        }

        @Override
        public boolean matchesAnyText(Collection<String> values) throws SelectorException {
            for (String value : values) {
                if (!this.matchesText(value)) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean matchesAnyText(Stream<String> values) throws SelectorException {
            return values.anyMatch(this::matchesText);
        }

        @Override
        public boolean matchesText(String value) throws SelectorException {
            return Util.matchText(value, this.elements, this.firstAny, this.lastAny);
        }
    }

    private static class SelectorConditionBoolean
    extends SelectorCondition {
        private static final Map<String, Boolean> booleanConstants = new HashMap<String, Boolean>();
        private final boolean booleanValue;

        private static void register(String key, Boolean value) {
            booleanConstants.put(key, value);
            booleanConstants.put(key.toLowerCase(Locale.ENGLISH), value);
            booleanConstants.put(key.substring(0, 1).toUpperCase(Locale.ENGLISH) + key.substring(1), value);
        }

        protected SelectorConditionBoolean(Key key, String value, boolean booleanValue) {
            super(key, value);
            this.booleanValue = booleanValue;
        }

        @Override
        public boolean isBoolean() {
            return true;
        }

        @Override
        public boolean matchesBoolean(boolean value) throws SelectorException {
            return value == this.booleanValue;
        }

        @Override
        public boolean getBoolean() throws SelectorException {
            return this.booleanValue;
        }

        @Deprecated
        public static SelectorConditionBoolean tryParse(String key, String value) {
            return SelectorConditionBoolean.tryParse(Key.parse(key), value);
        }

        public static SelectorConditionBoolean tryParse(Key key, String value) {
            Boolean truthy = booleanConstants.get(value);
            return truthy == null ? null : new SelectorConditionBoolean(key, value, truthy);
        }

        static {
            SelectorConditionBoolean.register("yes", Boolean.TRUE);
            SelectorConditionBoolean.register("true", Boolean.TRUE);
            SelectorConditionBoolean.register("no", Boolean.FALSE);
            SelectorConditionBoolean.register("false", Boolean.FALSE);
        }
    }
}

