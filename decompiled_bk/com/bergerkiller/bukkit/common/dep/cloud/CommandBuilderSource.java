/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud;

import com.bergerkiller.bukkit.common.dep.cloud.Command;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import com.bergerkiller.bukkit.common.dep.cloud.meta.CommandMeta;
import java.util.Collection;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

@API(status=API.Status.STABLE)
public interface CommandBuilderSource<C> {
    default public @NonNull Command.Builder<C> commandBuilder(@NonNull String name, @NonNull Collection<String> aliases, @NonNull Description description, @NonNull CommandMeta meta) {
        return this.decorateBuilder(Command.newBuilder(name, meta, description, aliases.toArray(new String[0])));
    }

    default public @NonNull Command.Builder<C> commandBuilder(@NonNull String name, @NonNull Collection<String> aliases, @NonNull CommandMeta meta) {
        return this.decorateBuilder(Command.newBuilder(name, meta, Description.empty(), aliases.toArray(new String[0])));
    }

    default public @NonNull Command.Builder<C> commandBuilder(@NonNull String name, @NonNull CommandMeta meta, @NonNull Description description, String ... aliases) {
        return this.decorateBuilder(Command.newBuilder(name, meta, description, aliases));
    }

    default public @NonNull Command.Builder<C> commandBuilder(@NonNull String name, @NonNull CommandMeta meta, String ... aliases) {
        return this.decorateBuilder(Command.newBuilder(name, meta, Description.empty(), aliases));
    }

    default public @NonNull Command.Builder<C> commandBuilder(@NonNull String name, @NonNull Description description, String ... aliases) {
        return this.decorateBuilder(Command.newBuilder(name, this.createDefaultCommandMeta(), description, aliases));
    }

    default public @NonNull Command.Builder<C> commandBuilder(@NonNull String name, String ... aliases) {
        return this.decorateBuilder(Command.newBuilder(name, this.createDefaultCommandMeta(), Description.empty(), aliases));
    }

    public @NonNull CommandMeta createDefaultCommandMeta();

    @API(status=API.Status.INTERNAL)
    public @NonNull Command.Builder<C> decorateBuilder(@NonNull Command.Builder<C> var1);
}

