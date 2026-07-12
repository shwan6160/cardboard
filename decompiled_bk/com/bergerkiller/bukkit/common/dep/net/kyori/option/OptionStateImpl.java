/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.option;

import com.bergerkiller.bukkit.common.dep.net.kyori.option.Option;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionSchema;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionState;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.value.ValueSource;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

final class OptionStateImpl
implements OptionState {
    private final OptionSchema schema;
    private final IdentityHashMap<Option<?>, Object> values;

    OptionStateImpl(OptionSchema schema, IdentityHashMap<Option<?>, Object> values) {
        this.schema = schema;
        this.values = new IdentityHashMap(values);
    }

    @Override
    public OptionSchema schema() {
        return this.schema;
    }

    @Override
    public boolean has(Option<?> option) {
        return this.values.containsKey(Objects.requireNonNull(option, "flag"));
    }

    @Override
    public <V> @Nullable V value(Option<V> option) {
        V value = option.valueType().type().cast(this.values.get(Objects.requireNonNull(option, "flag")));
        return value == null ? option.defaultValue() : value;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        OptionStateImpl that = (OptionStateImpl)other;
        return Objects.equals(this.values, that.values);
    }

    public int hashCode() {
        return Objects.hash(this.values);
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{values=" + this.values + '}';
    }

    static final class VersionedBuilderImpl
    implements OptionState.VersionedBuilder {
        private final OptionSchema schema;
        private final Map<Integer, BuilderImpl> builders = new TreeMap<Integer, BuilderImpl>();

        VersionedBuilderImpl(OptionSchema schema) {
            this.schema = schema;
        }

        @Override
        public OptionState.Versioned build() {
            if (this.builders.isEmpty()) {
                return new VersionedImpl(this.schema, Collections.emptySortedMap(), 0, this.schema.emptyState());
            }
            TreeMap<Integer, OptionState> built = new TreeMap<Integer, OptionState>();
            for (Map.Entry<Integer, BuilderImpl> entry : this.builders.entrySet()) {
                built.put(entry.getKey(), entry.getValue().build());
            }
            return new VersionedImpl(this.schema, built, (Integer)built.lastKey(), VersionedImpl.flattened(this.schema, built, (Integer)built.lastKey()));
        }

        @Override
        public OptionState.VersionedBuilder version(int version, Consumer<OptionState.Builder> versionBuilder) {
            Objects.requireNonNull(versionBuilder, "versionBuilder").accept(this.builders.computeIfAbsent(version, $ -> new BuilderImpl(this.schema)));
            return this;
        }
    }

    static final class BuilderImpl
    implements OptionState.Builder {
        private final OptionSchema schema;
        private final IdentityHashMap<Option<?>, Object> values = new IdentityHashMap();

        BuilderImpl(OptionSchema schema) {
            this.schema = schema;
        }

        @Override
        public OptionState build() {
            if (this.values.isEmpty()) {
                return this.schema.emptyState();
            }
            return new OptionStateImpl(this.schema, this.values);
        }

        @Override
        public <V> OptionState.Builder value(Option<V> option, @Nullable V value) {
            if (!this.schema.has(Objects.requireNonNull(option, "option"))) {
                throw new IllegalStateException("Option '" + option.id() + "' was not present in active schema");
            }
            if (value == null) {
                this.values.remove(option);
            } else {
                this.values.put(option, value);
            }
            return this;
        }

        private void putAll(Map<Option<?>, Object> values) {
            for (Map.Entry<Option<?>, Object> entry : values.entrySet()) {
                if (!this.schema.has(entry.getKey())) {
                    throw new IllegalStateException("Option '" + entry.getKey().id() + "' was not present in active schema");
                }
                this.values.put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public OptionState.Builder values(OptionState existing) {
            if (existing instanceof OptionStateImpl) {
                this.putAll(((OptionStateImpl)existing).values);
            } else if (existing instanceof VersionedImpl) {
                this.putAll(((OptionStateImpl)((VersionedImpl)existing).filtered).values);
            } else {
                throw new IllegalArgumentException("existing set " + existing + " is of an unknown implementation type");
            }
            return this;
        }

        @Override
        public OptionState.Builder values(ValueSource source) {
            for (Option<?> opt : this.schema.knownOptions()) {
                Object value = source.value(opt);
                if (value == null) continue;
                this.values.put(opt, value);
            }
            return this;
        }
    }

    static final class VersionedImpl
    implements OptionState.Versioned {
        private final OptionSchema schema;
        private final SortedMap<Integer, OptionState> sets;
        private final int targetVersion;
        private final OptionState filtered;

        VersionedImpl(OptionSchema schema, SortedMap<Integer, OptionState> sets, int targetVersion, OptionState filtered) {
            this.schema = schema;
            this.sets = sets;
            this.targetVersion = targetVersion;
            this.filtered = filtered;
        }

        @Override
        public OptionSchema schema() {
            return this.schema;
        }

        @Override
        public boolean has(Option<?> option) {
            return this.filtered.has(option);
        }

        @Override
        public <V> @Nullable V value(Option<V> option) {
            return this.filtered.value(option);
        }

        @Override
        public Map<Integer, OptionState> childStates() {
            return Collections.unmodifiableSortedMap(this.sets.headMap(this.targetVersion + 1));
        }

        @Override
        public OptionState.Versioned at(int version) {
            return new VersionedImpl(this.schema, this.sets, version, VersionedImpl.flattened(this.schema, this.sets, version));
        }

        public static OptionState flattened(OptionSchema schema, SortedMap<Integer, OptionState> versions, int targetVersion) {
            SortedMap<Integer, OptionState> applicable = versions.headMap(targetVersion + 1);
            OptionState.Builder builder = schema.stateBuilder();
            for (OptionState child : applicable.values()) {
                builder.values(child);
            }
            return builder.build();
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            VersionedImpl that = (VersionedImpl)other;
            return this.targetVersion == that.targetVersion && Objects.equals(this.schema, that.schema) && Objects.equals(this.sets, that.sets) && Objects.equals(this.filtered, that.filtered);
        }

        public int hashCode() {
            return Objects.hash(this.schema, this.sets, this.targetVersion, this.filtered);
        }

        public String toString() {
            return this.getClass().getSimpleName() + "{schema=" + this.schema + ", sets=" + this.sets + ", targetVersion=" + this.targetVersion + ", filtered=" + this.filtered + '}';
        }
    }
}

