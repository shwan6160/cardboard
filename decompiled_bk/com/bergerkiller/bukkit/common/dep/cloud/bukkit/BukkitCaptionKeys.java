/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apiguardian.api.API
 *  org.apiguardian.api.API$Status
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.bergerkiller.bukkit.common.dep.cloud.bukkit;

import com.bergerkiller.bukkit.common.dep.cloud.caption.Caption;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class BukkitCaptionKeys {
    private static final Collection<Caption> RECOGNIZED_CAPTIONS = new LinkedList<Caption>();
    public static final Caption ARGUMENT_PARSE_FAILURE_ENCHANTMENT = BukkitCaptionKeys.of("argument.parse.failure.enchantment");
    public static final Caption ARGUMENT_PARSE_FAILURE_MATERIAL = BukkitCaptionKeys.of("argument.parse.failure.material");
    public static final Caption ARGUMENT_PARSE_FAILURE_OFFLINEPLAYER = BukkitCaptionKeys.of("argument.parse.failure.offlineplayer");
    public static final Caption ARGUMENT_PARSE_FAILURE_PLAYER = BukkitCaptionKeys.of("argument.parse.failure.player");
    public static final Caption ARGUMENT_PARSE_FAILURE_WORLD = BukkitCaptionKeys.of("argument.parse.failure.world");
    public static final Caption ARGUMENT_PARSE_FAILURE_SELECTOR_UNSUPPORTED = BukkitCaptionKeys.of("argument.parse.failure.selector.unsupported");
    public static final Caption ARGUMENT_PARSE_FAILURE_LOCATION_INVALID_FORMAT = BukkitCaptionKeys.of("argument.parse.failure.location.invalid_format");
    public static final Caption ARGUMENT_PARSE_FAILURE_LOCATION_MIXED_LOCAL_ABSOLUTE = BukkitCaptionKeys.of("argument.parse.failure.location.mixed_local_absolute");
    public static final Caption ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NAMESPACE = BukkitCaptionKeys.of("argument.parse.failure.namespacedkey.namespace");
    public static final Caption ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_KEY = BukkitCaptionKeys.of("argument.parse.failure.namespacedkey.key");
    public static final Caption ARGUMENT_PARSE_FAILURE_NAMESPACED_KEY_NEED_NAMESPACE = BukkitCaptionKeys.of("argument.parse.failure.namespacedkey.need_namespace");
    public static final Caption ARGUMENT_PARSE_FAILURE_REGISTRY_ENTRY_MISSING = BukkitCaptionKeys.of("argument.parse.failure.registry_entry.missing");

    private BukkitCaptionKeys() {
    }

    private static @NonNull Caption of(@NonNull String key) {
        Caption caption = Caption.of(key);
        RECOGNIZED_CAPTIONS.add(caption);
        return caption;
    }

    @API(status=API.Status.STABLE, since="2.0.0")
    public static @NonNull Collection<@NonNull Caption> bukkitCaptionKeys() {
        return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
    }
}

