/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.IndentedStringBuilder;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonObject;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.gson.MapResourcePackDeserializer;
import com.bergerkiller.bukkit.common.map.util.ItemModel;
import com.bergerkiller.bukkit.common.map.util.ItemModelPredicate;
import com.bergerkiller.bukkit.common.utils.MathUtil;
import com.bergerkiller.bukkit.common.wrappers.CustomModelData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ItemModelProperty
implements IndentedStringBuilder.AppendableToString {
    public static final ItemModelProperty NONE = new ItemModelProperty(""){

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("No Property Set");
        }
    };
    private static final Map<String, PropertyCreator> BY_NAME = new HashMap<String, PropertyCreator>();
    private static final JsonObject NO_OBJ = new JsonObject();
    private final String name;

    public static ItemModelProperty get(String name) {
        return ItemModelProperty.get(name, NO_OBJ);
    }

    public static ItemModelProperty get(String name, JsonObject contextObj) {
        return BY_NAME.getOrDefault(name, PropertyCreator.UNKNOWN).create(name, contextObj);
    }

    protected ItemModelProperty(String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }

    public final PredicateProperty<?> asPredicateProperty() {
        if (this instanceof PredicateProperty) {
            return (PredicateProperty)((Object)this);
        }
        return new PredicateProperty.Incompatible(this);
    }

    public final String toString() {
        return IndentedStringBuilder.toString(this);
    }

    @Override
    public void toString(IndentedStringBuilder str) {
        str.append("Property { name: ").append(this.name).append(" }");
    }

    private static void register(String name, PropertyCreator creator) {
        BY_NAME.put(name, creator);
        BY_NAME.put("minecraft:" + name, creator);
    }

    static {
        ItemModelProperty.register("damaged", (name, contextObj) -> new BaseBooleanProperty(name){

            @Override
            public boolean testCondition(CommonItemStack item) {
                return !item.isUnbreakable();
            }

            @Override
            public Optional<CommonItemStack> applyCondition(CommonItemStack item, boolean isTrue) {
                CommonItemStack copy = item.clone();
                copy.setUnbreakable(!isTrue);
                return Optional.of(copy);
            }
        });
        ItemModelProperty.register("damage", (name, contextObj) -> new BaseNumericProperty(name, contextObj){

            @Override
            public double getExactValue(CommonItemStack item) {
                return item.getDamage();
            }

            @Override
            public Optional<CommonItemStack> setExactValue(CommonItemStack item, double value) {
                int maxDamage = item.getMaxDamage();
                int newDamage = (int)value;
                if (newDamage < 0 || newDamage > maxDamage || !item.isDamageSupported()) {
                    return Optional.empty();
                }
                return Optional.of(item.clone().setDamage(newDamage));
            }

            @Override
            public double getMaximumValue(CommonItemStack item) {
                return item.getMaxDamage() + 1;
            }
        });
        ItemModelProperty.register("count", (name, contextObj) -> new BaseNumericProperty(name, contextObj){

            @Override
            public double getExactValue(CommonItemStack item) {
                return item.getAmount();
            }

            @Override
            public Optional<CommonItemStack> setExactValue(CommonItemStack item, double value) {
                int maxAmount = item.getMaxStackSize();
                int newAmount = (int)value;
                if (newAmount <= 0 || newAmount > maxAmount) {
                    return Optional.empty();
                }
                return Optional.of(item.clone().setAmount(newAmount));
            }

            @Override
            public double getMaximumValue(CommonItemStack item) {
                return item.getMaxStackSize();
            }
        });
        ItemModelProperty.register("custom_model_data", CustomModelDataProperty::new);
    }

    @FunctionalInterface
    private static interface PropertyCreator {
        public static final PropertyCreator UNKNOWN = (name, jsonObject) -> new Unknown(name);

        public ItemModelProperty create(String var1, JsonObject var2);
    }

    public static interface PredicateProperty<T>
    extends IndentedStringBuilder.AppendableToString {
        public ItemModelProperty asItemModelProperty();

        default public boolean isPredicateNormalized() {
            return true;
        }

        public Optional<T> tryParsePredicateValue(JsonElement var1);

        public boolean isMatchingPredicate(CommonItemStack var1, T var2);

        public Optional<CommonItemStack> tryApplyPredicate(CommonItemStack var1, T var2);

        default public ItemModel.Overrides.PredicateCondition<T> asPredicateCondition(JsonElement predicateValue) {
            ItemModel.Overrides.PredicateCondition condition = new ItemModel.Overrides.PredicateCondition();
            condition.property = this;
            condition.value = this.tryParsePredicateValue(predicateValue).orElse(null);
            return condition;
        }

        public static class Incompatible
        implements PredicateProperty<Object> {
            private final ItemModelProperty property;

            protected Incompatible(ItemModelProperty property) {
                this.property = property;
            }

            @Override
            public ItemModelProperty asItemModelProperty() {
                return this.property;
            }

            @Override
            public Optional<Object> tryParsePredicateValue(JsonElement element) {
                return Optional.empty();
            }

            @Override
            public boolean isMatchingPredicate(CommonItemStack item, Object value) {
                return false;
            }

            @Override
            public Optional<CommonItemStack> tryApplyPredicate(CommonItemStack item, Object value) {
                return Optional.empty();
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("Incompatible Predicate {");
                str.indent().append("\nproperty: ").append(this.property);
                str.append("\n}");
            }
        }
    }

    private static class Unknown
    extends ItemModelProperty {
        public Unknown(String name) {
            super(name);
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Unknown Property { name: ").append(this.getName()).append(" }");
        }
    }

    private static class Incomplete
    extends ItemModelProperty {
        public Incomplete(String name) {
            super(name);
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Incomplete Property { name: ").append(this.getName()).append(" }");
        }
    }

    private static abstract class BaseNumericProperty
    extends ItemModelProperty
    implements NumericProperty,
    PredicateProperty<Double> {
        private final boolean normalize;
        private final double scale;

        protected BaseNumericProperty(String name, JsonObject obj) {
            super(name);
            this.normalize = !obj.has("normalize") || obj.get("normalize").getAsBoolean();
            this.scale = obj.has("scale") ? obj.get("scale").getAsDouble() : 1.0;
        }

        @Override
        public ItemModelProperty asItemModelProperty() {
            return this;
        }

        @Override
        public Optional<Double> tryParsePredicateValue(JsonElement element) {
            return MapResourcePackDeserializer.tryParseAsDouble(element);
        }

        @Override
        public boolean isMatchingPredicate(CommonItemStack item, Double value) {
            double max;
            double itemValue = this.getExactValue(item);
            if (this.isPredicateNormalized() && !Double.isNaN(max = this.getMaximumValue(item))) {
                itemValue = max == 0.0 ? 0.0 : (itemValue /= max);
            }
            return Math.abs(itemValue - value) <= 1.0E-7;
        }

        @Override
        public Optional<CommonItemStack> tryApplyPredicate(CommonItemStack item, Double value) {
            if (!this.isPredicateNormalized()) {
                return this.setExactValue(item, value);
            }
            double max = this.getMaximumValue(item);
            if (Double.isNaN(max)) {
                return this.setExactValue(item, value);
            }
            if (max != 0.0) {
                return this.setExactValue(item, value * max);
            }
            return Optional.empty();
        }

        @Override
        public double getScale() {
            return this.scale;
        }

        @Override
        public boolean isNormalized() {
            return this.normalize;
        }
    }

    private static abstract class BaseBooleanProperty
    extends ItemModelProperty
    implements BooleanProperty,
    PredicateProperty<Boolean> {
        protected BaseBooleanProperty(String name) {
            super(name);
        }

        @Override
        public ItemModelProperty asItemModelProperty() {
            return this;
        }

        @Override
        public Optional<Boolean> tryParsePredicateValue(JsonElement element) {
            return MapResourcePackDeserializer.tryParseAsBoolean(element);
        }

        @Override
        public boolean isMatchingPredicate(CommonItemStack item, Boolean value) {
            return this.testCondition(item) == value.booleanValue();
        }

        @Override
        public Optional<CommonItemStack> tryApplyPredicate(CommonItemStack item, Boolean value) {
            return this.applyCondition(item, value);
        }
    }

    private static class CustomModelDataProperty
    extends ItemModelProperty
    implements NumericProperty,
    StringProperty,
    BooleanProperty,
    PredicateProperty<Integer> {
        private final int index;
        private final boolean normalize;
        private final double scale;

        protected CustomModelDataProperty(String name, JsonObject obj) {
            super(name);
            this.index = obj.has("index") ? obj.get("index").getAsInt() : 0;
            this.normalize = !obj.has("normalize") || obj.get("normalize").getAsBoolean();
            this.scale = obj.has("scale") ? obj.get("scale").getAsDouble() : 1.0;
        }

        @Override
        public double getScale() {
            return this.scale;
        }

        @Override
        public boolean isNormalized() {
            return this.normalize;
        }

        @Override
        public double getExactValue(CommonItemStack item) {
            List<Float> floats = item.getCustomModelDataComponents().floats();
            return this.index >= 0 && this.index < floats.size() ? (double)floats.get(this.index).floatValue() : 0.0;
        }

        @Override
        public Optional<CommonItemStack> setExactValue(CommonItemStack item, double value) {
            if (this.index < 0) {
                return Optional.empty();
            }
            CustomModelData cmd = item.getCustomModelDataComponents();
            ArrayList<Float> floats = new ArrayList<Float>(cmd.floats());
            while (this.index >= floats.size()) {
                floats.add(Float.valueOf(0.0f));
            }
            floats.set(this.index, Float.valueOf((float)value));
            return Optional.of(item.clone().setCustomModelDataComponents(cmd.withFloats(floats)));
        }

        @Override
        public double getMaximumValue(CommonItemStack item) {
            return Double.NaN;
        }

        @Override
        public String getStringValue(CommonItemStack item) {
            List<String> strings = item.getCustomModelDataComponents().strings();
            return this.index >= 0 && this.index < strings.size() ? strings.get(this.index) : "";
        }

        @Override
        public Optional<CommonItemStack> applyStringValue(CommonItemStack item, String value) {
            if (this.index < 0) {
                return Optional.empty();
            }
            CustomModelData cmd = item.getCustomModelDataComponents();
            ArrayList<String> strings = new ArrayList<String>(cmd.strings());
            while (this.index >= strings.size()) {
                strings.add("");
            }
            strings.set(this.index, value);
            return Optional.of(item.clone().setCustomModelDataComponents(cmd.withStrings(strings)));
        }

        @Override
        public boolean testCondition(CommonItemStack item) {
            List<Boolean> flags = item.getCustomModelDataComponents().flags();
            return this.index >= 0 && this.index < flags.size() && flags.get(this.index) != false;
        }

        @Override
        public Optional<CommonItemStack> applyCondition(CommonItemStack item, boolean isTrue) {
            if (this.index < 0) {
                return Optional.empty();
            }
            CustomModelData cmd = item.getCustomModelDataComponents();
            ArrayList<Boolean> flags = new ArrayList<Boolean>(cmd.flags());
            if (isTrue) {
                while (this.index >= flags.size()) {
                    flags.add(Boolean.FALSE);
                }
                flags.set(this.index, Boolean.TRUE);
                return Optional.of(item.clone().setCustomModelDataComponents(cmd.withFlags(flags)));
            }
            if (this.index >= flags.size() || !((Boolean)flags.get(this.index)).booleanValue()) {
                return Optional.of(item.clone());
            }
            flags.set(this.index, Boolean.FALSE);
            return Optional.of(item.clone().setCustomModelDataComponents(cmd.withFlags(flags)));
        }

        @Override
        public ItemModelProperty asItemModelProperty() {
            return this;
        }

        @Override
        public boolean isPredicateNormalized() {
            return false;
        }

        @Override
        public Optional<Integer> tryParsePredicateValue(JsonElement element) {
            return MapResourcePackDeserializer.tryParseAsInt(element);
        }

        @Override
        public boolean isMatchingPredicate(CommonItemStack item, Integer value) {
            return item.hasCustomModelData() && item.getCustomModelData() == value.intValue();
        }

        @Override
        public Optional<CommonItemStack> tryApplyPredicate(CommonItemStack item, Integer value) {
            return Optional.of(item.clone().setCustomModelData(value));
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Property {").append(" name: ").append(this.getName()).append(", index: ").append(this.index).append(" }");
        }
    }

    public static interface StringProperty {
        public String getStringValue(CommonItemStack var1);

        public Optional<CommonItemStack> applyStringValue(CommonItemStack var1, String var2);
    }

    public static interface NumericProperty {
        public double getScale();

        public boolean isNormalized();

        public double getExactValue(CommonItemStack var1);

        public Optional<CommonItemStack> setExactValue(CommonItemStack var1, double var2);

        public double getMaximumValue(CommonItemStack var1);

        default public double getNumericValue(CommonItemStack item) {
            double max = this.getMaximumValue(item);
            if (Double.isNaN(max)) {
                return this.getScale() * this.getExactValue(item);
            }
            if (max <= 0.0) {
                return 0.0;
            }
            if (this.isNormalized()) {
                return this.getScale() * MathUtil.clamp(this.getExactValue(item) / max, 0.0, 1.0);
            }
            return this.getScale() * MathUtil.clamp(this.getExactValue(item), 0.0, max);
        }

        default public Optional<CommonItemStack> setNumericValue(CommonItemStack item, double value) {
            double max;
            double scale = this.getScale();
            if (scale != 0.0) {
                value /= scale;
            }
            if (this.isNormalized() && !Double.isNaN(max = this.getMaximumValue(item))) {
                value *= max;
            }
            return this.setExactValue(item, value);
        }
    }

    private static class PrintableBooleanPredicate
    implements ItemModelPredicate,
    IndentedStringBuilder.AppendableToString {
        private final BooleanProperty property;
        private final boolean isTrue;

        public PrintableBooleanPredicate(BooleanProperty property, boolean isTrue) {
            this.property = property;
            this.isTrue = isTrue;
        }

        @Override
        public boolean isMatching(CommonItemStack item) {
            return this.isTrue == this.property.testCondition(item);
        }

        @Override
        public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
            return this.property.applyCondition(item, this.isTrue);
        }

        public String toString() {
            return IndentedStringBuilder.toString(this);
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("BooleanPredicate {");
            str.indent().append("\nproperty: ").append(this.property).append("\nwhen: ").append(this.isTrue);
            str.append("\n}");
        }
    }

    public static interface BooleanProperty {
        public boolean testCondition(CommonItemStack var1);

        public Optional<CommonItemStack> applyCondition(CommonItemStack var1, boolean var2);

        default public ItemModelPredicate asPredicate(boolean isTrue) {
            return new PrintableBooleanPredicate(this, isTrue);
        }
    }
}

