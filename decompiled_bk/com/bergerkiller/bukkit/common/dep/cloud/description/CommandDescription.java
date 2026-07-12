/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.immutables.value.Value$Immutable
 */
package com.bergerkiller.bukkit.common.dep.cloud.description;

import com.bergerkiller.bukkit.common.dep.cloud.description.CommandDescriptionImpl;
import com.bergerkiller.bukkit.common.dep.cloud.description.Describable;
import com.bergerkiller.bukkit.common.dep.cloud.description.Description;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;

@API(status=API.Status.STABLE)
@Value.Immutable
public interface CommandDescription
extends Describable {
    public static @NonNull CommandDescription empty() {
        return CommandDescriptionImpl.of(Description.empty(), Description.empty());
    }

    public static @NonNull CommandDescription commandDescription(@NonNull Description description, @NonNull Description verboseDescription) {
        return CommandDescriptionImpl.of(description, verboseDescription);
    }

    public static @NonNull CommandDescription commandDescription(@NonNull Description description) {
        return CommandDescriptionImpl.of(description, description);
    }

    public static @NonNull CommandDescription commandDescription(@NonNull String description, @NonNull String verboseDescription) {
        return CommandDescriptionImpl.of(Description.of(description), Description.of(verboseDescription));
    }

    public static @NonNull CommandDescription commandDescription(@NonNull String description) {
        return CommandDescriptionImpl.of(Description.of(description), Description.of(description));
    }

    @Override
    public @NonNull Description description();

    public @NonNull Description verboseDescription();

    default public boolean isEmpty() {
        return this.description().equals(Description.empty());
    }
}

