/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.Component;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.option.OptionState;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class DummyJSONComponentSerializer
implements JSONComponentSerializer {
    static final JSONComponentSerializer INSTANCE = new DummyJSONComponentSerializer();
    private static final String UNSUPPORTED_MESSAGE = "No JsonComponentSerializer implementation found\n\nAre you missing an implementation artifact like adventure-text-serializer-gson?\nIs your environment configured in a way that causes ServiceLoader to malfunction?";

    DummyJSONComponentSerializer() {
    }

    @Override
    @NotNull
    public Component deserialize(@NotNull String input) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    @Override
    @NotNull
    public String serialize(@NotNull Component component) {
        throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
    }

    static final class BuilderImpl
    implements JSONComponentSerializer.Builder {
        BuilderImpl() {
        }

        @Override
        @NotNull
        public JSONComponentSerializer.Builder options(@NotNull OptionState flags) {
            return this;
        }

        @Override
        @NotNull
        public JSONComponentSerializer.Builder editOptions(@NotNull Consumer<OptionState.Builder> optionEditor) {
            return this;
        }

        @Override
        @Deprecated
        @NotNull
        public JSONComponentSerializer.Builder downsampleColors() {
            return this;
        }

        @Override
        @NotNull
        public JSONComponentSerializer.Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer serializer) {
            return this;
        }

        @Override
        @Deprecated
        @NotNull
        public JSONComponentSerializer.Builder emitLegacyHoverEvent() {
            return this;
        }

        @Override
        @NotNull
        public JSONComponentSerializer build() {
            return INSTANCE;
        }
    }
}

