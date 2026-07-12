/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson;

import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.GsonBuilder;
import com.bergerkiller.bukkit.common.dep.gson.JsonElement;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.ComponentSerializerImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.gson.SerializerFactory;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.JSONOptions;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.Services;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionState;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class GsonComponentSerializerImpl
implements GsonComponentSerializer {
    private static final Optional<GsonComponentSerializer.Provider> SERVICE = Services.service(GsonComponentSerializer.Provider.class);
    static final Consumer<GsonComponentSerializer.Builder> BUILDER = SERVICE.map(GsonComponentSerializer.Provider::builder).orElseGet(() -> builder -> {});
    private final Gson serializer;
    private final UnaryOperator<GsonBuilder> populator;
    private final @Nullable LegacyHoverEventSerializer legacyHoverSerializer;
    private final OptionState flags;

    GsonComponentSerializerImpl(OptionState flags, @Nullable LegacyHoverEventSerializer legacyHoverSerializer) {
        this.flags = flags;
        this.legacyHoverSerializer = legacyHoverSerializer;
        this.populator = builder -> {
            builder.registerTypeAdapterFactory(new SerializerFactory(flags, legacyHoverSerializer));
            return builder;
        };
        this.serializer = ((GsonBuilder)this.populator.apply(new GsonBuilder().disableHtmlEscaping())).create();
    }

    @Override
    @NotNull
    public Gson serializer() {
        return this.serializer;
    }

    @Override
    @NotNull
    public UnaryOperator<GsonBuilder> populator() {
        return this.populator;
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull String string) {
        Component component = this.serializer().fromJson(string, Component.class);
        if (component == null) {
            throw ComponentSerializerImpl.notSureHowToDeserialize(string);
        }
        return component;
    }

    @Override
    @Nullable
    public Component deserializeOr(@Nullable String input, @Nullable Component fallback) {
        if (input == null) {
            return fallback;
        }
        Component component = this.serializer().fromJson(input, Component.class);
        if (component == null) {
            return fallback;
        }
        return component;
    }

    @Override
    @NotNull
    public String serialize(@NotNull Component component) {
        return this.serializer().toJson(component);
    }

    @Override
    @NotNull
    public Component deserializeFromTree(@NotNull JsonElement input) {
        Component component = this.serializer().fromJson(input, Component.class);
        if (component == null) {
            throw ComponentSerializerImpl.notSureHowToDeserialize(input);
        }
        return component;
    }

    @Override
    @NotNull
    public JsonElement serializeToTree(@NotNull Component component) {
        return this.serializer().toJsonTree(component);
    }

    @Override
    @NotNull
    public GsonComponentSerializer.Builder toBuilder() {
        return new BuilderImpl(this);
    }

    static /* synthetic */ Optional access$000() {
        return SERVICE;
    }

    static final class BuilderImpl
    implements GsonComponentSerializer.Builder {
        private OptionState flags = JSONOptions.byDataVersion();
        private @Nullable LegacyHoverEventSerializer legacyHoverSerializer;

        BuilderImpl() {
            BUILDER.accept(this);
        }

        BuilderImpl(GsonComponentSerializerImpl serializer) {
            this();
            this.flags = serializer.flags;
            this.legacyHoverSerializer = serializer.legacyHoverSerializer;
        }

        @Override
        @NotNull
        public GsonComponentSerializer.Builder options(@NotNull OptionState flags) {
            this.flags = Objects.requireNonNull(flags, "flags");
            return this;
        }

        @Override
        @NotNull
        public GsonComponentSerializer.Builder editOptions(@NotNull Consumer<OptionState.Builder> optionEditor) {
            OptionState.Builder builder = JSONOptions.schema().stateBuilder().values(this.flags);
            Objects.requireNonNull(optionEditor, "flagEditor").accept(builder);
            this.flags = builder.build();
            return this;
        }

        @Override
        @NotNull
        public GsonComponentSerializer.Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer serializer) {
            this.legacyHoverSerializer = serializer;
            return this;
        }

        @Override
        @NotNull
        public GsonComponentSerializer build() {
            return new GsonComponentSerializerImpl(this.flags, this.legacyHoverSerializer);
        }
    }

    static final class Instances {
        static final GsonComponentSerializer INSTANCE = GsonComponentSerializerImpl.access$000().map(GsonComponentSerializer.Provider::gson).orElseGet(() -> new GsonComponentSerializerImpl(JSONOptions.byDataVersion(), null));
        static final GsonComponentSerializer LEGACY_INSTANCE = GsonComponentSerializerImpl.access$000().map(GsonComponentSerializer.Provider::gsonLegacy).orElseGet(() -> new GsonComponentSerializerImpl(JSONOptions.byDataVersion().at(2525), null));

        Instances() {
        }
    }
}

