/*
 * Decompiled with CFR 0.152.
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.DummyJSONComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.util.Services;
import java.util.Optional;
import java.util.function.Supplier;

final class JSONComponentSerializerAccessor {
    private static final Optional<JSONComponentSerializer.Provider> SERVICE = Services.serviceWithFallback(JSONComponentSerializer.Provider.class);

    private JSONComponentSerializerAccessor() {
    }

    static /* synthetic */ Optional access$000() {
        return SERVICE;
    }

    static final class Instances {
        static final JSONComponentSerializer INSTANCE = JSONComponentSerializerAccessor.access$000().map(JSONComponentSerializer.Provider::instance).orElse(DummyJSONComponentSerializer.INSTANCE);
        static final Supplier<JSONComponentSerializer.Builder> BUILDER_SUPPLIER = JSONComponentSerializerAccessor.access$000().map(JSONComponentSerializer.Provider::builder).orElse(DummyJSONComponentSerializer.BuilderImpl::new);

        Instances() {
        }
    }
}

