/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.map.util;

import com.bergerkiller.bukkit.common.IndentedStringBuilder;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.dep.gson.GsonBuilder;
import com.bergerkiller.bukkit.common.dep.gson.JsonArray;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializationContext;
import com.bergerkiller.bukkit.common.dep.gson.JsonDeserializer;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.gson.JsonObject;
import com.bergerkiller.bukkit.common.dep.gson.JsonParseException;
import com.bergerkiller.bukkit.common.dep.gson.annotations.SerializedName;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.map.util.ItemModelOverride;
import com.bergerkiller.bukkit.common.map.util.ItemModelPredicate;
import com.bergerkiller.bukkit.common.map.util.ItemModelProperty;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public abstract class ItemModel
implements IndentedStringBuilder.AppendableToString {
    public final String toString() {
        return IndentedStringBuilder.toString(this);
    }

    public abstract List<MinecraftModel> resolveModels(CommonItemStack var1);

    public abstract boolean hasValidModels();

    public List<ItemModelOverride> listAllOverrides() {
        return this.listAllOverrides(null);
    }

    public final List<ItemModelOverride> listAllOverrides(@Nullable CommonItemStack baseItemStack) {
        ItemModelPredicate.ModelChain root = ItemModelPredicate.ModelChain.newRoot(baseItemStack);
        this.populateModelChain(root);
        return root.getAllLeafs().stream().filter(ItemModelPredicate.ModelChain::hasModels).map(ItemModelPredicate.ModelChain::collectAsOverride).collect(Collectors.toList());
    }

    protected abstract void populateModelChain(ItemModelPredicate.ModelChain var1);

    public static void registerDeserializers(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter((Type)((Object)ItemModel.class), new ItemModelDeserializer());
        gsonBuilder.registerTypeAdapter((Type)((Object)Condition.class), (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            Condition condition = new Condition();
            if (obj.has("property")) {
                // empty if block
            }
            condition.property = ItemModel.tryParseProperty(jsonDeserializationContext, obj, "property");
            condition.on_true = ItemModel.tryParseItemModel(jsonDeserializationContext, obj, "on_true");
            condition.on_false = ItemModel.tryParseItemModel(jsonDeserializationContext, obj, "on_false");
            return condition;
        });
        gsonBuilder.registerTypeAdapter((Type)((Object)RangeDispatch.class), (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            RangeDispatch dispatch = new RangeDispatch();
            dispatch.property = ItemModel.tryParseProperty(jsonDeserializationContext, obj, "property");
            dispatch.fallback = ItemModel.tryParseItemModel(jsonDeserializationContext, obj, "fallback");
            if (obj.has("entries")) {
                JsonArray arr = obj.get("entries").getAsJsonArray();
                int size = arr.size();
                dispatch.entries = new ArrayList<RangeDispatch.Entry>(size);
                for (int i = 0; i < size; ++i) {
                    RangeDispatch.Entry e = (RangeDispatch.Entry)jsonDeserializationContext.deserialize(arr.get(i), (Type)((Object)RangeDispatch.Entry.class));
                    e.property = dispatch.property;
                    dispatch.entries.add(e);
                }
                double maxThreshold = Double.MAX_VALUE;
                for (int i = size - 1; i >= 0; --i) {
                    RangeDispatch.Entry e = dispatch.entries.get(i);
                    e.maxThreshold = maxThreshold;
                    maxThreshold = Math.min(e.minThreshold, maxThreshold);
                }
                dispatch.entries = Collections.unmodifiableList(dispatch.entries);
            } else {
                dispatch.entries = Collections.emptyList();
            }
            return dispatch;
        });
        gsonBuilder.registerTypeAdapter((Type)((Object)Select.Case.class), (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            Select.Case c = new Select.Case();
            if (obj.has("when")) {
                JsonElement whenElement = obj.get("when");
                if (whenElement.isJsonArray()) {
                    JsonArray whenArray = whenElement.getAsJsonArray();
                    int whenCount = whenArray.size();
                    ArrayList<Select.WhenValue> whenArrayValues = new ArrayList<Select.WhenValue>(whenCount);
                    for (int i = 0; i < whenCount; ++i) {
                        whenArrayValues.add(Select.WhenValue.deserialize(whenArray.get(i)));
                    }
                    c.when = Collections.unmodifiableList(whenArrayValues);
                } else {
                    c.when = Collections.singletonList(Select.WhenValue.deserialize(whenElement));
                }
            } else {
                c.when = Collections.emptyList();
            }
            c.model = ItemModel.tryParseItemModel(jsonDeserializationContext, obj, "model");
            return c;
        });
        gsonBuilder.registerTypeAdapter((Type)((Object)Select.class), (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            Select select = new Select();
            select.property = ItemModel.tryParseProperty(jsonDeserializationContext, obj, "property");
            select.fallback = ItemModel.tryParseItemModel(jsonDeserializationContext, obj, "fallback");
            if (obj.has("cases")) {
                JsonArray arr = obj.get("cases").getAsJsonArray();
                int size = arr.size();
                select.cases = new ArrayList<Select.Case>(size);
                for (int i = 0; i < size; ++i) {
                    Select.Case c = (Select.Case)jsonDeserializationContext.deserialize(arr.get(i), (Type)((Object)Select.Case.class));
                    c.property = select.property;
                    select.cases.add(c);
                }
                select.cases = Collections.unmodifiableList(select.cases);
            } else {
                select.cases = Collections.emptyList();
            }
            return select;
        });
        gsonBuilder.registerTypeAdapter((Type)((Object)Overrides.OverriddenModel.class), (jsonElement, type, jsonDeserializationContext) -> {
            JsonObject obj = jsonElement.getAsJsonObject();
            Overrides.OverriddenModel result = new Overrides.OverriddenModel();
            if (obj.has("predicate")) {
                JsonObject predicateListObj = obj.get("predicate").getAsJsonObject();
                ArrayList conditions = new ArrayList(predicateListObj.size());
                for (Map.Entry<String, JsonElement> predicateObj : predicateListObj.entrySet()) {
                    ItemModelProperty.PredicateProperty<?> property = ItemModelProperty.get(predicateObj.getKey()).asPredicateProperty();
                    conditions.add(property.asPredicateCondition(predicateObj.getValue()));
                }
                result.predicate = Collections.unmodifiableList(conditions);
            }
            if (obj.has("model")) {
                result.models = Collections.singletonList(MinecraftModel.of(obj.get("model").getAsString()));
            }
            return result;
        });
    }

    private static ItemModelProperty tryParseProperty(JsonDeserializationContext jsonDeserializationContext, JsonObject obj, String key) {
        if (!obj.has(key)) {
            return ItemModelProperty.NONE;
        }
        String name = "<unknown>";
        try {
            name = obj.get(key).getAsString();
            return ItemModelProperty.get(name, obj);
        }
        catch (Throwable t) {
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Failed to deserialize an item model property '" + name + "'", t);
            return ItemModelProperty.NONE;
        }
    }

    private static ItemModel tryParseItemModel(JsonDeserializationContext jsonDeserializationContext, JsonObject obj, String key) {
        if (!obj.has(key)) {
            return MinecraftModel.NOT_SET;
        }
        try {
            return (ItemModel)jsonDeserializationContext.deserialize(obj.get(key), (Type)((Object)ItemModel.class));
        }
        catch (Throwable t) {
            Logging.LOGGER_MAPDISPLAY.log(Level.SEVERE, "Failed to deserialize an item model", t);
            return MinecraftModel.NOT_SET;
        }
    }

    public static class ItemModelDeserializer
    implements JsonDeserializer<ItemModel> {
        private final Map<String, JsonDeserializer<? extends ItemModel>> byName = new HashMap<String, JsonDeserializer<? extends ItemModel>>();

        public ItemModelDeserializer() {
            this.registerDeserializerToType("model", MinecraftModel.class);
            this.registerDeserializerToType("condition", Condition.class);
            this.registerDeserializerToType("range_dispatch", RangeDispatch.class);
            this.registerDeserializerToType("select", Select.class);
            this.registerDeserializerToType("composite", Composite.class);
        }

        private void registerDeserializerToType(String name, Class<? extends ItemModel> itemModelType) {
            this.registerDeserializer(name, (jsonElement, type, jsonDeserializationContext) -> (ItemModel)jsonDeserializationContext.deserialize(jsonElement, itemModelType));
        }

        private void registerDeserializer(String name, JsonDeserializer<? extends ItemModel> deserializer) {
            this.byName.put(name, deserializer);
            this.byName.put("minecraft:" + name, deserializer);
        }

        @Override
        public ItemModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject obj = jsonElement.getAsJsonObject();
            String typeName = obj.get("type").getAsString();
            JsonDeserializer<? extends ItemModel> deserializer = this.byName.get(typeName);
            if (deserializer != null) {
                return deserializer.deserialize(jsonElement, type, jsonDeserializationContext);
            }
            return new UnknownItemModel(typeName);
        }
    }

    public static class Condition
    extends ItemModel {
        public ItemModelProperty property;
        public ItemModel on_true;
        public ItemModel on_false;

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            boolean isTrue = this.property instanceof ItemModelProperty.BooleanProperty && ((ItemModelProperty.BooleanProperty)((Object)this.property)).testCondition(item);
            return (isTrue ? this.on_true : this.on_false).resolveModels(item);
        }

        @Override
        public boolean hasValidModels() {
            return this.on_true.hasValidModels() || this.on_false.hasValidModels();
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            if (this.property instanceof ItemModelProperty.BooleanProperty) {
                ItemModelProperty.BooleanProperty bProp = (ItemModelProperty.BooleanProperty)((Object)this.property);
                this.on_true.populateModelChain(chain.next(bProp.asPredicate(true)));
                this.on_false.populateModelChain(chain.next(bProp.asPredicate(false)));
            } else {
                this.on_false.populateModelChain(chain);
            }
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Condition {");
            str.indent().append("\nproperty: ").append(this.property).append("\non_true: ").append(this.on_true).append("\non_false: ").append(this.on_false);
            str.append("\n}");
        }
    }

    public static class RangeDispatch
    extends ItemModel {
        public ItemModelProperty property;
        public List<Entry> entries;
        public ItemModel fallback;

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            if (this.property instanceof ItemModelProperty.NumericProperty) {
                double propertyValue = ((ItemModelProperty.NumericProperty)((Object)this.property)).getNumericValue(item);
                List<Entry> entries = this.entries;
                for (int i = entries.size() - 1; i >= 0; --i) {
                    Entry e = entries.get(i);
                    if (!(propertyValue >= e.minThreshold)) continue;
                    return e.model.resolveModels(item);
                }
            }
            return this.fallback.resolveModels(item);
        }

        @Override
        public boolean hasValidModels() {
            if (this.fallback.hasValidModels()) {
                return true;
            }
            for (Entry e : this.entries) {
                if (!e.model.hasValidModels()) continue;
                return true;
            }
            return false;
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            for (Entry e : this.entries) {
                e.model.populateModelChain(chain.next(e));
            }
            this.fallback.populateModelChain(chain.next(ItemModelPredicate.ALWAYS_TRUE_PREDICATE));
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Range Dispatch {");
            IndentedStringBuilder ind = str.indent();
            ind.append("\nproperty: ").append(this.property);
            ind.append("\nentries: [");
            IndentedStringBuilder entryStr = ind.indent();
            for (Entry e : this.entries) {
                entryStr.append("\n{");
                entryStr.indent().append("\nthreshold: ").append(e.minThreshold).append("\nmodel: ").append(e.model);
                entryStr.append("\n}");
            }
            ind.append("\n]");
            ind.append("\nfallback: ").append(this.fallback);
            str.append("\n}");
        }

        public static class Entry
        implements ItemModelPredicate,
        IndentedStringBuilder.AppendableToString {
            protected transient ItemModelProperty property = ItemModelProperty.NONE;
            @SerializedName(value="threshold")
            public double minThreshold = -1.7976931348623157E308;
            public transient double maxThreshold = Double.MAX_VALUE;
            public ItemModel model = MinecraftModel.NOT_SET;

            @Override
            public boolean isMatching(CommonItemStack item) {
                if (!(this.property instanceof ItemModelProperty.NumericProperty)) {
                    return false;
                }
                double propertyValue = ((ItemModelProperty.NumericProperty)((Object)this.property)).getNumericValue(item);
                return propertyValue >= this.minThreshold && propertyValue < this.maxThreshold;
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                if (!(this.property instanceof ItemModelProperty.NumericProperty)) {
                    return Optional.empty();
                }
                return ((ItemModelProperty.NumericProperty)((Object)this.property)).setNumericValue(item, this.minThreshold);
            }

            public String toString() {
                return IndentedStringBuilder.toString(this);
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("RangeDispatch.Entry {");
                str.indent().append("\nproperty: ").append(this.property).append("\nminThreshold: ").append(this.minThreshold).append("\nmaxThreshold: ").append(this.maxThreshold).append("\nmodel: ").append(this.model);
                str.append("\n}");
            }
        }
    }

    public static class Select
    extends ItemModel {
        public ItemModelProperty property;
        public List<Case> cases;
        public ItemModel fallback;

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            for (Case c : this.cases) {
                for (WhenValue whenValue : c.when) {
                    if (!whenValue.isMatching(this.property, item)) continue;
                    return c.model.resolveModels(item);
                }
            }
            return this.fallback.resolveModels(item);
        }

        @Override
        public boolean hasValidModels() {
            if (this.fallback.hasValidModels()) {
                return true;
            }
            for (Case c : this.cases) {
                if (!c.model.hasValidModels()) continue;
                return true;
            }
            return false;
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            for (Case c : this.cases) {
                c.model.populateModelChain(chain.next(c));
            }
            this.fallback.populateModelChain(chain.next(ItemModelPredicate.ALWAYS_TRUE_PREDICATE));
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Select {");
            IndentedStringBuilder ind = str.indent();
            ind.append("\nproperty: ").append(this.property);
            ind.append("\ncases: [");
            IndentedStringBuilder entryStr = ind.indent();
            for (Case c : this.cases) {
                entryStr.append("\n{");
                entryStr.indent().append("\nwhen: ").append(c.when).append("\nmodel: ").append(c.model);
                entryStr.append("\n}");
            }
            ind.append("\n]");
            ind.append("\nfallback: ").append(this.fallback);
            str.append("\n}");
        }

        public static class Case
        implements ItemModelPredicate,
        IndentedStringBuilder.AppendableToString {
            protected transient ItemModelProperty property = ItemModelProperty.NONE;
            public List<WhenValue> when = Collections.emptyList();
            public ItemModel model = MinecraftModel.NOT_SET;

            @Override
            public boolean isMatching(CommonItemStack item) {
                for (WhenValue whenValue : this.when) {
                    if (!whenValue.isMatching(this.property, item)) continue;
                    return true;
                }
                return false;
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                if (this.when.isEmpty()) {
                    return Optional.of(item);
                }
                return this.when.get(0).tryMakeMatching(this.property, item);
            }

            public String toString() {
                return IndentedStringBuilder.toString(this);
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("Select.Case {");
                IndentedStringBuilder ind = str.indent();
                ind.append("\nproperty: ").append(this.property).append("\nwhen: [");
                ind.indent().appendLines(this.when);
                ind.append("\n]").append("\nmodel: ").append(this.model);
                str.append("\n}");
            }
        }

        public static interface WhenValue {
            public Optional<CommonItemStack> tryMakeMatching(ItemModelProperty var1, CommonItemStack var2);

            public boolean isMatching(ItemModelProperty var1, CommonItemStack var2);

            public static WhenValue deserialize(JsonElement element) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    LinkedHashMap<String, String> dict = new LinkedHashMap<String, String>();
                    for (String key : obj.keySet()) {
                        dict.put(key, obj.get(key).getAsString());
                    }
                    return new WhenDictValue(dict);
                }
                return new WhenStringValue(element.getAsString());
            }
        }

        public static final class WhenDictValue
        implements WhenValue {
            public final Map<String, String> dict;

            public WhenDictValue(Map<String, String> dict) {
                this.dict = dict;
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(ItemModelProperty property, CommonItemStack item) {
                return Optional.empty();
            }

            @Override
            public boolean isMatching(ItemModelProperty property, CommonItemStack item) {
                return false;
            }

            public String toString() {
                return this.dict.toString();
            }
        }

        public static final class WhenStringValue
        implements WhenValue {
            public final String value;

            public WhenStringValue(String value) {
                this.value = value;
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(ItemModelProperty property, CommonItemStack item) {
                if (property instanceof ItemModelProperty.StringProperty) {
                    return ((ItemModelProperty.StringProperty)((Object)property)).applyStringValue(item, this.value);
                }
                return Optional.empty();
            }

            @Override
            public boolean isMatching(ItemModelProperty property, CommonItemStack item) {
                if (property instanceof ItemModelProperty.StringProperty) {
                    String propertyValue = ((ItemModelProperty.StringProperty)((Object)property)).getStringValue(item);
                    return this.value.equals(propertyValue);
                }
                return false;
            }

            public String toString() {
                return this.value;
            }
        }
    }

    public static class Overrides
    extends ItemModel {
        public List<OverriddenModel> overrides = Collections.emptyList();
        public transient MinecraftModel fallback = MinecraftModel.NOT_SET;

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            for (OverriddenModel override : this.overrides) {
                if (!override.isMatching(item)) continue;
                return override.getOverrideModels();
            }
            return Collections.singletonList(this.fallback);
        }

        @Override
        public boolean hasValidModels() {
            if (this.fallback.hasValidModels()) {
                return true;
            }
            for (OverriddenModel override : this.overrides) {
                if (!override.hasValidOverrideModels()) continue;
                return true;
            }
            return false;
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            for (OverriddenModel override : this.overrides) {
                chain.next(override).addModels(override.models);
            }
            chain.next(PredicateCondition.ALWAYS_TRUE_PREDICATE).addModel(this.fallback);
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Predicate Overrides {");
            str.indent().append("\noverrides: [").appendWithIndent(ov_str -> ov_str.appendLines(this.overrides)).append("\n]").append("\nfallback: ").append(this.fallback);
            str.append("\n}");
        }

        public static class OverriddenModel
        implements IndentedStringBuilder.AppendableToString,
        ItemModelOverride {
            public List<PredicateCondition<?>> predicate = Collections.emptyList();
            public List<MinecraftModel> models = MinecraftModel.NOT_SET_LIST;
            @Nullable
            public CommonItemStack itemStack = null;

            @Override
            public boolean isMatching(CommonItemStack item) {
                for (PredicateCondition<?> condition : this.predicate) {
                    if (condition.isMatching(item)) continue;
                    return false;
                }
                return true;
            }

            @Override
            public boolean isMatchingAlways() {
                return this.predicate.isEmpty();
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                PredicateCondition<?> condition;
                Optional<CommonItemStack> result = Optional.of(item);
                Iterator<PredicateCondition<?>> iterator = this.predicate.iterator();
                while (iterator.hasNext() && (result = (condition = iterator.next()).tryMakeMatching(result.get())).isPresent()) {
                }
                return result;
            }

            @Override
            public List<MinecraftModel> getOverrideModels() {
                return this.models;
            }

            @Override
            public Optional<CommonItemStack> getItemStack() {
                return Optional.ofNullable(this.itemStack);
            }

            public String toString() {
                return IndentedStringBuilder.toString(this);
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("Override {");
                str.indent().append("\npredicates: [").appendWithIndent(predicatesStr -> predicatesStr.appendLines(this.predicate)).append("\n]").append("\nmodel: ").append(this.models.get(0));
                str.append("\n}");
            }
        }

        public static class PredicateCondition<T>
        implements IndentedStringBuilder.AppendableToString,
        ItemModelPredicate {
            public ItemModelProperty.PredicateProperty<T> property;
            @Nullable
            public T value;

            @Override
            public boolean isMatching(CommonItemStack item) {
                return this.value != null && this.property.isMatchingPredicate(item, this.value);
            }

            @Override
            public Optional<CommonItemStack> tryMakeMatching(CommonItemStack item) {
                return this.value == null ? Optional.empty() : this.property.tryApplyPredicate(item, this.value);
            }

            public String toString() {
                return IndentedStringBuilder.toString(this);
            }

            @Override
            public void toString(IndentedStringBuilder str) {
                str.append("Predicate {");
                str.indent().append("\npredicate: ").append(this.property).append("\nvalue: ").append((Object)(this.value == null ? "<failed to parse>" : this.value));
                str.append("\n}");
            }
        }
    }

    public static class MinecraftModel
    extends ItemModel {
        public static final MinecraftModel NOT_SET = new MinecraftModel();
        public static final List<MinecraftModel> NOT_SET_LIST = Collections.singletonList(NOT_SET);
        public String model;

        public static MinecraftModel of(String name) {
            MinecraftModel model = new MinecraftModel();
            model.model = name;
            return model;
        }

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            return Collections.singletonList(this);
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            chain.addModel(this);
        }

        @Override
        public boolean hasValidModels() {
            return this != NOT_SET;
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Model { name: " + this.model + " }");
        }

        static {
            MinecraftModel.NOT_SET.model = "minecraft:builtin/missing";
        }
    }

    public static class Composite
    extends ItemModel {
        public List<ItemModel> models = Collections.emptyList();

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            return this.models.stream().flatMap(m -> m.resolveModels(item).stream()).collect(Collectors.toList());
        }

        @Override
        public boolean hasValidModels() {
            for (ItemModel model : this.models) {
                if (!model.hasValidModels()) continue;
                return true;
            }
            return false;
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            for (ItemModel model : this.models) {
                model.populateModelChain(chain);
            }
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Composite [");
            str.indent().appendLines(this.models);
            str.append("\n]");
        }
    }

    public static class Root
    extends ItemModel {
        @Nullable
        public transient CommonItemStack baseItemStack = null;
        public ItemModel model;
        public boolean hand_animation_on_swap = true;

        public ItemModel getModel() {
            ItemModel model = this.model;
            return model != null ? model : MinecraftModel.NOT_SET;
        }

        @Override
        public List<ItemModelOverride> listAllOverrides() {
            return this.listAllOverrides(this.baseItemStack);
        }

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            return this.getModel().resolveModels(item);
        }

        @Override
        public boolean hasValidModels() {
            return this.getModel().hasValidModels();
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
            this.getModel().populateModelChain(chain);
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("{");
            str.indent().append("\nmodel: ").append(this.model);
            str.append("\n}");
        }
    }

    public static class UnknownItemModel
    extends ItemModel {
        public final String type;

        public UnknownItemModel(String type) {
            this.type = type;
        }

        @Override
        public void toString(IndentedStringBuilder str) {
            str.append("Unknown { type: ").append(this.type).append(" }");
        }

        @Override
        public List<MinecraftModel> resolveModels(CommonItemStack item) {
            return Collections.emptyList();
        }

        @Override
        public boolean hasValidModels() {
            return false;
        }

        @Override
        protected void populateModelChain(ItemModelPredicate.ModelChain chain) {
        }
    }
}

