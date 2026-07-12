/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key;

import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.KeyImpl;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.KeyPattern;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Keyed;
import com.bergerkiller.bukkit.common.dep.net.kyori.adventure.key.Namespaced;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.Examinable;
import com.bergerkiller.bukkit.common.dep.net.kyori.examination.ExaminableProperty;
import java.util.Comparator;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Key
extends Comparable<Key>,
Examinable,
Namespaced,
Keyed {
    public static final String MINECRAFT_NAMESPACE = "minecraft";
    public static final char DEFAULT_SEPARATOR = ':';

    @NotNull
    public static Key key(@KeyPattern @NotNull String string) {
        return Key.key(string, ':');
    }

    @NotNull
    public static Key key(@NotNull String string, char character) {
        Objects.requireNonNull(string, "string");
        int index = string.indexOf(character);
        String namespace = index >= 1 ? string.substring(0, index) : MINECRAFT_NAMESPACE;
        String value = index >= 0 ? string.substring(index + 1) : string;
        return Key.key(namespace, value);
    }

    @NotNull
    public static Key key(@NotNull Namespaced namespaced, @KeyPattern.Value @NotNull String value) {
        return Key.key(Objects.requireNonNull(namespaced, "namespaced").namespace(), value);
    }

    @NotNull
    public static Key key(@KeyPattern.Namespace @NotNull String namespace, @KeyPattern.Value @NotNull String value) {
        return new KeyImpl(namespace, value);
    }

    @NotNull
    public static Comparator<? super Key> comparator() {
        return KeyImpl.COMPARATOR;
    }

    public static boolean parseable(@Nullable String string) {
        if (string == null) {
            return false;
        }
        int index = string.indexOf(58);
        String namespace = index >= 1 ? string.substring(0, index) : MINECRAFT_NAMESPACE;
        String value = index >= 0 ? string.substring(index + 1) : string;
        return Key.parseableNamespace(namespace) && Key.parseableValue(value);
    }

    public static boolean parseableNamespace(@NotNull String namespace) {
        return !Key.checkNamespace(namespace).isPresent();
    }

    @NotNull
    public static OptionalInt checkNamespace(@NotNull String namespace) {
        Objects.requireNonNull(namespace, "namespace");
        int length = namespace.length();
        for (int i = 0; i < length; ++i) {
            if (Key.allowedInNamespace(namespace.charAt(i))) continue;
            return OptionalInt.of(i);
        }
        return OptionalInt.empty();
    }

    public static boolean parseableValue(@NotNull String value) {
        return !Key.checkValue(value).isPresent();
    }

    @NotNull
    public static OptionalInt checkValue(@NotNull String value) {
        Objects.requireNonNull(value, "value");
        int length = value.length();
        for (int i = 0; i < length; ++i) {
            if (Key.allowedInValue(value.charAt(i))) continue;
            return OptionalInt.of(i);
        }
        return OptionalInt.empty();
    }

    public static boolean allowedInNamespace(char character) {
        return KeyImpl.allowedInNamespace(character);
    }

    public static boolean allowedInValue(char character) {
        return KeyImpl.allowedInValue(character);
    }

    @Override
    @KeyPattern.Namespace
    @NotNull
    public String namespace();

    @KeyPattern.Value
    @NotNull
    public String value();

    @NotNull
    public String asString();

    @NotNull
    default public String asMinimalString() {
        if (this.namespace().equals(MINECRAFT_NAMESPACE)) {
            return this.value();
        }
        return this.asString();
    }

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("namespace", this.namespace()), ExaminableProperty.of("value", this.value()));
    }

    @Override
    default public int compareTo(@NotNull Key that) {
        return Key.comparator().compare(this, that);
    }

    @Override
    @NotNull
    default public Key key() {
        return this;
    }
}

